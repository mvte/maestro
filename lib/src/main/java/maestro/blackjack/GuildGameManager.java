package maestro.blackjack;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import maestro.Bot;
import maestro.Config;
import maestro.blackjack.objects.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * Takes the commands from the BlackjackManager and manages the game accordingly
 */
public class GuildGameManager {
	
	private Game game;
	private TextChannel channel;
	public boolean started = false;
	private ArrayList<Player> players;
	private Connection conn;
	
	/**
	 * Constructs an empty GuildGameManager
	 */
	public GuildGameManager() {
		players = new ArrayList<>();
		try {
			conn = DriverManager.getConnection(Bot.db_url, Bot.db_user, Config.get("db_pass"));
			System.out.println("connected to db successfully");
		} catch (SQLException e) {
			conn = null;
			System.out.println("something went wrong connecting to database");
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		return conn;
	}

	public void retryConnection() {
		try {
			conn = DriverManager.getConnection(Bot.db_url, Bot.db_user, Config.get("db_pass"));
			System.out.println("connected to db successfully after retrying");
		} catch (SQLException e) {
			conn = null;
			System.out.println("something went wrong");
			e.printStackTrace();
		}
	}
	
	/**
	 * Begins a game of blackjack.
	 * @param channel The channel in which to send the game messages to
	 */
	public void beginGame(TextChannel channel) {
		
		this.channel = channel;
		game = new Game(6, channel, players, conn);
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
		players.clear();
		game = null;
	}
	
	/**
	 * Removes a user from the game
	 * @param user The user to be removed
	 */
	public void removePlayer(User user, ButtonInteractionEvent event) {
		game.removePlayer(user, event);
	}
	

}
