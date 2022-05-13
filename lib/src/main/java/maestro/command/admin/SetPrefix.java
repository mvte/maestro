package maestro.command.admin;

import java.util.List;

import maestro.PrefixManager;
import maestro.command.CommandInterface;
import maestro.database.DatabaseManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetPrefix implements CommandInterface {

	@Override
	public void handle(MessageReceivedEvent event, List<String> args) {
		final TextChannel channel = event.getTextChannel();
		final Member member = event.getMember();
		
		if(!member.hasPermission(Permission.MANAGE_SERVER)) {
			channel.sendMessage("you don't have permission to do that").queue();
			return;
		}
		
		if(args.isEmpty()) {
			channel.sendMessage("missing args").queue();
			return;
		}
		
		final String newPrefix = String.join("", args);
		updatePrefix(event.getGuild().getIdLong(), newPrefix);
		
		channel.sendMessageFormat("prefix has been set to `%s`", newPrefix).queue();
		
	}

	@Override
	public String getName() {
		return "setprefix";
	}

	@Override
	public String getHelp(String prefix) {
		return "sets the prefix for this server\n" + prefix  + "setprefix `<prefix>`";
	} 
	
	private void updatePrefix(long guildId, String prefix) {
		PrefixManager.PREFIXES.put(guildId, prefix);
		DatabaseManager.INSTANCE.setPrefix(guildId, prefix);
	}
	
	
	

}
