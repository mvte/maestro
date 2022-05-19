package maestro.blackjack.interactions;

import java.util.List;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface Interaction {
	
	
	/**
	 * Override if the interaction is a blackjack button
	 * @param event
	 */
	public default void handle(ButtonInteractionEvent event) { }
	
	/**
	 * Override if the interaction is a blackjack text command
	 * @param event the message event
	 * @param args the arguments of the command
	 */
	public default void handle(MessageReceivedEvent event, List<String> args) { }
	
	/**
	 * Returns the interaction id. This is how the interaction is identified in the BlackjackManager.
	 * For text commands, it is usually blackjack:[command].
	 * For buttons, it is blackjack:[button_name]button (e.g. blackjack:startbutton)
	 */
	public String getId();
	
	

}
