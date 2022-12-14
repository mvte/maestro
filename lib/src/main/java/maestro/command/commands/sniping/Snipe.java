package maestro.command.commands.sniping;

import java.util.List;

import maestro.command.CommandInterface;
import maestro.model.UserModel;
import maestro.model.UserModelDatabase;
import maestro.sniper.SnipeChecker;
import maestro.sniper.SnipeFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Snipe implements CommandInterface {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		MessageChannel channel = event.getChannel();
		SnipeFactory snipeFactory = new SnipeFactory();
		long userId = event.getAuthor().getIdLong();

		if(args.size() != 1) {
			channel.sendMessage("incorrect amount of arguments. use the help command for usage").queue();
			return;
		}

		maestro.sniper.Snipe snipe = snipeFactory.createSnipe(args.get(0), userId);

		if(snipe == null) {
			channel.sendMessage("something went wrong creating your snipe! check your url?").queue();
			return;
		}

		UserModel user = UserModelDatabase.getInstance().getUser(userId);
		user.addSnipe(snipe);
		snipe.addUser(user);

		SnipeChecker.getInstance().addSnipe(snipe);

		EmbedBuilder eb = new EmbedBuilder()
				.setTitle("successfully added snipe request")
				.addField(snipe.getItemName(), "this item was added to your requests", false);

		channel.sendMessageEmbeds(eb.build()).queue();
	}

	@Override
	public String getName() {
		return "snipe";
	}

	@Override
	public String getHelp(String prefix) {
		return "the bot will send you a message when an item you want is in stock. currently, this bot only supports " +
				"sniping best buy, and gamestop links\nusage: `m.snipe <link>`";
	}
	
}