package maestro.blackjack;

import java.util.ArrayList;

import maestro.blackjack.objects.Player;
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
	private ArrayList<Player> players;
	
	public GuildGameManager() {
		players = new ArrayList<>();
	}
	
	public void beginGame(TextChannel channel) {
		
		this.channel = channel;
		game = new Game(6, channel, players);
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
		nullGame();
		
		eb.setTitle(":x: game stopped");
		this.channel.sendMessageEmbeds(eb.build()).queue();
	}
	
	public void addPlayer(Player player) {
		players.add(player);
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	/**
	 * nullifies the game instance to ensure a proper game end
	 * i also have to empty this list 
	 */
	public void nullGame() {
		players.removeAll(players);
		game = null;
	}
	

}
