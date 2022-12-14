package maestro.command.commands.sniping;

import maestro.command.CommandInterface;
import maestro.model.UserModel;
import maestro.model.UserModelDatabase;
import maestro.sniper.SnipeChecker;
import maestro.sniper.SnipeFactory;
import maestro.sniper.URLType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class RemoveSnipe implements CommandInterface {
    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        if(args.size() != 1) {
            event.getChannel().sendMessage("incorrect number of arguments provided").queue();
            return;
        }
        if(URLType.getURLType(args.get(0)) == null) {
            event.getChannel().sendMessage("invalid argument").queue();
            return;
        }

        UserModel user = UserModelDatabase.getInstance().getUser(event.getAuthor().getIdLong());
        SnipeFactory sf = new SnipeFactory();
        maestro.sniper.Snipe s = sf.createSnipe(args.get(0));

        maestro.sniper.Snipe realSnipe;
        if((realSnipe = SnipeChecker.getInstance().getSnipe(s)) == null) {
            event.getChannel().sendMessage("this snipe does not exist").queue();
            return;
        }
        if(!realSnipe.getUsers().contains(user)) {
            System.out.println(s);
            System.out.println(realSnipe);
            event.getChannel().sendMessage("you are not sniping this item").queue();
            return;
        }

        realSnipe.getUsers().remove(user);
        user.getSnipes().remove(realSnipe);
        SnipeChecker.getInstance().clearEmptySnipes();

        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("successfully removed snipe request")
                .addField(realSnipe.getItemName(), "this item was removed from your requests", false)
                .setThumbnail(event.getChannel().getJDA().getSelfUser().getAvatarUrl());

        event.getChannel().sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public String getName() {
        return "removesnipe";
    }

    @Override
    public String getHelp(String prefix) {
        return "removes a snipe given the link that was provided to create it";
    }

    @Override
    public List<String> getAliases() {
        return List.of("rs");
    }
}
