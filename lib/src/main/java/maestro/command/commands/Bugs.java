package maestro.command.commands;

import java.util.List;

import maestro.command.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Bugs implements CommandInterface {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		MessageChannel channel = event.getChannel();
		EmbedBuilder eb = new EmbedBuilder()
				.setTitle("submit bugs here", "https://github.com/mvte/maestro/issues")
				.setThumbnail(channel.getJDA().getSelfUser().getAvatarUrl())
				.setDescription("click the link above to submit any bugs you find. in addition, below is a list of currently known bugs")
				.addField("play sometimes doesn't work", "this issue only occurs with youtube links (youtube and discord music bots are not on amicable terms). the only solution for now is to try again or use soundcloud links", false);
					//integrating lavalink would give me the tools to fix this
		
		channel.sendMessageEmbeds(eb.build()).queue();
	}

	@Override
	public String getName() {
		return "bugs";
	}

	@Override
	public String getHelp(String prefix) {
		return "provides a link to report bugs, and lists any currently known bugs";
	}

}
