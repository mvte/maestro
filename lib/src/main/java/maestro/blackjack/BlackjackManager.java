package maestro.blackjack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import maestro.Bot;
import maestro.Config;
import maestro.blackjack.interactions.*;
import maestro.blackjack.objects.Player;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Receives Blackjack commands and distributes them to the proper GuildGameManager (and their game)
 */
public class BlackjackManager {

	private static BlackjackManager INSTANCE;
	private final Map<Long, GuildGameManager> gameManagers;
	private final List<Interaction> interactions;
	private final ScheduledExecutorService scheduler;


	/**
	 * Constructs the BlackjackManager instance (and registers all Interactions). There can only be one BlackjackManager Instance for every instance of the Bot.
	 */
	private BlackjackManager() {
		this.gameManagers = new HashMap<>();
		interactions = new ArrayList<>();

		/* Create the scheduler */
		scheduler = Executors.newScheduledThreadPool(1);
		LocalTime time = LocalTime.of(23,59,59);
		long initialDelay = Duration.between(LocalTime.now(), time).toMillis();
		scheduler.scheduleAtFixedRate(this::giveDailyCash, initialDelay, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);

		/* Register interactions here */
		addInteraction(new StartButton());
		addInteraction(new Display());
		addInteraction(new Start());
		addInteraction(new CancelButton());
		addInteraction(new JoinButton());
		addInteraction(new Rules());
		addInteraction(new Leave());
		addInteraction(new Leaderboard());
		addInteraction(new Cash());
	}

	private void giveDailyCash() {
		User user = Bot.bot.getUserById(Config.get("owner_id"));
		try {
			Connection conn = DriverManager.getConnection(Bot.db_url, Bot.db_user, Config.get("db_pass"));
			conn.createStatement().executeUpdate("UPDATE amounts SET cash = cash + 100");
			conn.close();
			user.openPrivateChannel()
					.flatMap(channel ->
							channel.sendMessage("daily cash distributed successfully"))
					.queue();
		} catch(SQLException e) {
			user.openPrivateChannel()
					.flatMap(channel ->
							channel.sendMessage("something went wrong giving daily cash, please increment manually"))
					.queue();
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns the GuildGameManager for a given guild. If a GuildGameManager for a guild doesn't exist, it will create one
	 * @param guild The guild whose GuildGameManager we are trying to get
	 * @return The GuildGameManager for the given guild
	 */
	public GuildGameManager getGameManager(Guild guild) {
		return this.gameManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> new GuildGameManager());
	}
	
	/**
	 * Adds an Interaction Object to the registered list of interactions
	 * @param intr The Interaction Object we are adding to the manager
	 */
	private void addInteraction(Interaction intr) {
		boolean exists = this.interactions.stream().anyMatch((it) -> it.getId().equalsIgnoreCase(intr.getId()));

		if (exists)
			throw new IllegalArgumentException("this command already exists");

		interactions.add(intr);
		
	}
	
	/**
	 * Gets the Interaction whose getId() method returns the given ID
	 * @param id ID of the interaction we are trying to get
	 * @return the Interaction if it exists (null if it doesn't)
	 */
	public Interaction getInteraction(String id) {
		id = id.toLowerCase();

		for (Interaction intr : this.interactions) {
			if (intr.getId().equals(id))
				return intr;
		}
		
		return null;
	}
	
	/**
	 * Handles a blackjack text command. This method is usually called by the Blackjack class in the maestro.command.commands package. 
	 * @param event the message event
	 * @param args the arguments of the command (omitting the [prefix]blackjack, as it already went through the commandmanager)
	 */
	public void handleCommand(MessageReceivedEvent event, List<String> args) {
		//Blackjack interactions must start with "blackjack:"
			// This is so that the maestro listener can tell which manager to send the event to
		String invoke = args.isEmpty() ?  "blackjack:display" : "blackjack:" + args.get(0);
		Interaction intr = this.getInteraction(invoke);
		
		if(intr != null) {
			if(args.size() > 1)
				args = args.subList(1, args.size());
			intr.handle(event, args);
		}
		
	}
	
	/**
	 * Handles a blackjack button press. 
	 * @param event The button press event
	 */
	public void handleButtonPress(ButtonInteractionEvent event) {
		String invoke = event.getComponentId();
		Interaction intr = this.getInteraction(invoke);
		
		if(intr != null)
			intr.handle(event);
		
	}

	public Player getPlayerFromDb(User user, Connection conn) {
		String id = user.getId();
		try {
			Statement get = conn.createStatement();
			get.execute("SELECT * FROM amounts WHERE id = " + id);
			if(get.getResultSet().next()) {
				return new Player(get.getResultSet().getInt("cash"), user);
			}

			Statement create = conn.createStatement();
			create.execute("INSERT INTO amounts (id) VALUES (" + id + ")");
			return new Player(1000, user);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * Gets the instance of the BlackjackManager, if the instance doesn't exist create it. There can only be one BlackjackManager instance
	 * @return
	 */
	public static BlackjackManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new BlackjackManager();
		}
		
		return INSTANCE;
	}


}
