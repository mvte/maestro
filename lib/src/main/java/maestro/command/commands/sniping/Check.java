package maestro.command.commands.sniping;

import maestro.command.CommandInterface;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class Check implements CommandInterface {
    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {

    }

    @Override
    public String getName() {
        return "check";
    }

    @Override
    public String getHelp(String prefix) {
        return null;
    }
}
