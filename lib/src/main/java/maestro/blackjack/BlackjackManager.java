package maestro.blackjack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maestro.blackjack.interactions.CancelButton;
import maestro.blackjack.interactions.Display;
import maestro.blackjack.interactions.Interaction;
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

	public BlackjackManager() {
		this.gameManagers = new HashMap<>();
		interactions = new ArrayList<>();
		
		/* Register interactions here */
		addInteraction(new StartButton());
		addInteraction(new Display());
		addInteraction(new Start());
		addInteraction(new CancelButton());
		addInteraction(new Stop());
		
	}
	
	public GuildGameManager getGameManager(Guild guild) {
		return this.gameManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
			final GuildGameManager guildGameManager = new GuildGameManager();
			return guildGameManager;
		});
	}
	
	public void addInteraction(Interaction intr) {
		boolean exists = this.interactions.stream().anyMatch((it) -> it.getId().equalsIgnoreCase(intr.getId()));

		if (exists)
			throw new IllegalArgumentException("this command already exists");

		interactions.add(intr);
		
	}
	
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
	 * 		this could be merged with the handleCommand method using an if(x instanceof y)
	 */
	public void handleButtonPress(ButtonInteractionEvent event) {
		String invoke = event.getComponentId();
		Interaction intr = this.getInteraction(invoke);
		
		if(intr != null)
			intr.handle(event);
		
	}
	
	public static BlackjackManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new BlackjackManager();
		}
		
		return INSTANCE;
	}
	
}
