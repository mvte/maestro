package maestro.sniper;

import maestro.Bot;
import maestro.model.UserModel;
import net.dv8tion.jda.api.entities.User;

import java.util.HashSet;
import java.util.concurrent.*;

/**
 * Singleton class that will check all snipes for stock.
 */
public class SnipeChecker {

    private static SnipeChecker INSTANCE;
    private final HashSet<Snipe> snipes;

    private SnipeChecker() {
        this.snipes = new HashSet<>();
        run();
    }

    public static SnipeChecker getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SnipeChecker();
        }
        return INSTANCE;
    }

    private void run() {
        Bot.service.scheduleAtFixedRate(this::checkSnipes, 0, 7, TimeUnit.SECONDS);
    }

    public Snipe getSnipe(Snipe snipe) {
        for(Snipe s : snipes) {
            if(s.equals(snipe))
                return s;
        }

        return null;
    }

    public boolean addSnipe(Snipe snipe) {
        return snipes.add(snipe);
    }

    public void clearEmptySnipes() {
        snipes.removeIf(s -> s.getUsers().isEmpty());
    }

    private void checkSnipes() {
        for(Snipe s : snipes) {
            Bot.service.submit(() -> {
                if(s.inStock()) notifyAllUsers(s);
            });
        }

    }

    private void notifyAllUsers(Snipe snipe) {
        for(UserModel user : snipe.getUsers()) {
            user.getSnipes().remove(snipe);
            snipes.remove(snipe);
            User jdaUser = Bot.bot.getUserById(user.getId());

            String stockMessage = snipe instanceof RutgersSnipe ? " **is open!**\n" : " **is in stock!**\n";
            jdaUser.openPrivateChannel()
                    .flatMap(channel -> channel.sendMessage(snipe.getItemName() + stockMessage + snipe.getUrl()))
                    .queue();
        }
    }


}
