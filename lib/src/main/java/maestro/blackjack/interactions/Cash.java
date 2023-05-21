package maestro.blackjack.interactions;

import maestro.blackjack.BlackjackManager;
import maestro.blackjack.objects.Player;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Cash implements Interaction {

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {
        Player player = BlackjackManager.getInstance()
                .getPlayerFromDb(event.getAuthor(),
                        BlackjackManager.getInstance().getGameManager(event.getGuild()).getConnection());

        if(player == null) {
            event.getChannel().sendMessage("you have not played any games yet. use m.bj start to play").queue();
            return;
        }

        event.getChannel().sendMessage("you have " + player.getCash() + " cash").queue();
    }

    @Override
    public String getId() {
        return "blackjack:cash";
    }
}
