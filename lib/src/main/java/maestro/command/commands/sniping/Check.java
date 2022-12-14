package maestro.command.commands.sniping;

import maestro.Bot;
import maestro.command.CommandInterface;
import maestro.model.UserModel;
import maestro.model.UserModelDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Check implements CommandInterface {
    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        UserModel user = UserModelDatabase.getInstance().getUser(event.getAuthor().getIdLong());
        User jdaUser = Bot.bot.getUserById(user.getId());

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("your snipes")
                .setThumbnail(jdaUser.getAvatarUrl())
                .setFooter(jdaUser.getName());

        for(maestro.sniper.Snipe s : user.getSnipes()) {
            eb.addField(s.getItemName(), "[link](" + s.getUrl() + ")", false);
        }

        jdaUser.openPrivateChannel()
                .flatMap(channel -> channel.sendMessageEmbeds(eb.build()))
                .queue();

    }

    @Override
    public String getName() {
        return "check";
    }

    @Override
    public String getHelp(String prefix) {
        return "gives list of snipes";
    }
}
