package maestro.blackjack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maestro.blackjack.interactions.CancelButton;
import maestro.blackjack.interactions.Display;
import maestro.blackjack.interactions.Interaction;
import maestro.blackjack.interactions.JoinButton;
import maestro.blackjack.interactions.Start;
import maestro.blackjack.interactions.StartButton;
import maestro.blackjack.interactions.Stop;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Receives Blackjack commands and distributes them to the proper GuildGameManager (and their game)
 */
public class BlackjackManager {

	private static BlackjackManager INSTANCE;
	
	private final Map<Long, GuildGameManager> gameManagers;
	private final List<Interaction> interactions;

	/**
	 * Constructs the BlackjackManager instance (and registers all Interactions). There can only be one BlackjackManager Instance for every instance of the Bot.
	 */
	private BlackjackManager() {
		this.gameManagers = new HashMap<>();
		interactions = new ArrayList<>();
		
		/* Register interactions here */
		addInteraction(new StartButton());
		addInteraction(new Display());
		addInteraction(new Start());
		addInteraction(new CancelButton());
		addInteraction(new Stop());
		addInteraction(new JoinButton());
	}
	
	/**
	 * Returns the GuildGameManager for a given guild. If a GuildGameManager for a guild doesn't exist, it will create one
	 * @param guild The guild whose GuildGameManager we are trying to get
	 * @return The GuildGameManager for the given guild
	 */
	public GuildGameManager getGameManager(Guild guild) {
		return this.gameManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
			final GuildGameManager guildGameManager = new GuildGameManager();
			return guildGameManager;
		});
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
	 * Gets the Interactoin whose getId() method returns the given ID
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
