package maestro.blackjack;

import java.util.ArrayList;

import maestro.blackjack.objects.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * Takes the commands from the BlackjackManager and manages the game accordingly
 */
public class GuildGameManager {
	
	private Game game;
	private TextChannel channel;
	public boolean started = false;
	private ArrayList<Player> players;
	
	/**
	 * Constructs an empty GuildGameManager
	 */
	public GuildGameManager() {
		players = new ArrayList<>();
	}
	
	
	/**
	 * Begins a game of blackjack.
	 * @param channel The channel in which to send the game messages to
	 */
	public void beginGame(TextChannel channel) {
		
		this.channel = channel;
		game = new Game(6, channel, players);
		game.started = true;
		this.started = game.started;
		
		
		game.run();
	}
	
	/**
	 * Stops the game of blackjack in a channel, if there is one (might remove this as eventwaiter is messing up how it works).
	 * @param channel
	 */
	public void stop(TextChannel channel) {
		//add other stuff to ensure game ends properly (like saving all data to db)
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
	
	/**
	 * Adds a player to the ArrayList of players
	 * @param player The player that is being added
	 */
	public void addPlayer(Player player) {
		players.add(player);
	}
	
	/**
	 * Gets the ArrayList of players
	 * @return the ArrayList of Players
	 */
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	/**
	 * Nullifies the game instance to ensure a proper game end. But to achieve the behavior we want, we also have to remove all the Players in this manager's ArrayList of Players
	 */
	public void nullGame() {
		players.removeAll(players);
		game = null;
	}
	

}
