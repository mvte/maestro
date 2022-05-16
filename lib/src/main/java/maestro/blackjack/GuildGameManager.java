package maestro.blackjack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

/**
 * Takes the commands from the BlackjackManager and manages the game accordingly
 */
public class GuildGameManager {
	
	private Game game;
	private TextChannel channel;
	public boolean started = false;
	
	public void beginGame(TextChannel channel, User user) {
		this.channel = channel;
		game = new Game(8, channel, user);
		game.started = true;
		this.started = game.started;
		
		game.run(true);
	}
	
	public void stop(TextChannel channel) {
		//other stuff to ensure game ends properly (like saving all data to db)
		//should check permissions
		
		EmbedBuilder eb = new EmbedBuilder();
		
		if(game.started == false) {
			this.started = game.started; //redundant, but just in case
			channel.sendMessage("there is no game currently running").queue();
			return;
		}
		
		game.started = false;
		this.started = false;
		eb.setTitle(":x: game stopped");
		this.channel.sendMessageEmbeds(eb.build()).queue();
	}
	

}
