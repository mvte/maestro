package maestro.command.admin;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import maestro.PrefixManager;
import maestro.command.CommandInterface;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Purge implements CommandInterface {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		TextChannel channel = event.getTextChannel();
		String prefix = PrefixManager.PREFIXES.get(event.getGuild().getIdLong());
		
		if(!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
			channel.sendMessage("you don't have the permission to do this").queue();
			return;
		}
		
		final List<Message> toBeDeleted = new ArrayList<Message>(1000); 
		
		channel.getIterableHistory().cache(false).forEachAsync((msg) ->
		{	
			//this should be a sufficient stop condition
			if(msg.getTimeCreated().plusWeeks(2).compareTo(OffsetDateTime.now()) < 0) {
				channel.sendMessage("cannot delete messages from more than two weeks ago! stopping purge").queue();
				return false;
			}
			
			if(msg.getAuthor().equals(msg.getChannel().getJDA().getSelfUser()) || msg.getContentRaw().startsWith(prefix) || msg.getContentRaw().startsWith("m."))
				toBeDeleted.add(msg);
		
			return toBeDeleted.size() < 1000;
		}).thenRun( () -> {
			channel.purgeMessages(toBeDeleted);
			channel.sendMessage("purge complete, deleted " + toBeDeleted.size() + " messages").queue();
			System.out.printf("deleted %d messages from channel %s in server %s\n", toBeDeleted.size(), channel.getName(), channel.getGuild().getName());
		});
		
	}

	@Override
	public String getName() {
		return "purge";
	}

	@Override
	public String getHelp(String prefix) {
		return "deletes the last 1000 messages sent by this bot (and the message that invoked it) in this channel\n\n"
				+ "_note: discord doesn't like it when you delete messages from more than two weeks ago, so we can't delete messages from more than two weeks ago :/_";
	}

}
