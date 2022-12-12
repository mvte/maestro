package maestro.sniper;

import maestro.Bot;
import maestro.model.UserModel;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Singleton class that will check all snipes for stock.
 */
public class SnipeChecker {

    private static SnipeChecker INSTANCE;
    private ArrayList<Snipe> snipes;

    private SnipeChecker() {
        this.snipes = new ArrayList<>();
        run();
    }

    public static SnipeChecker getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SnipeChecker();
        }
        return INSTANCE;
    }

    private void run() {
        Bot.service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                checkSnipes();
            }
        }, 0, 7, TimeUnit.SECONDS);
    }

    public void addSnipe(Snipe snipe) {
        snipes.add(snipe);
    }

    private void checkSnipes() {
        for(Snipe s : snipes) {
            Bot.service.submit(new Runnable() {
                @Override
                public void run() {
                    if(s.inStock()) notifyAllUsers(s);
                }
            });
        }

    }

    private void notifyAllUsers(Snipe snipe) {
        for(UserModel user : snipe.getUsers()) {
            user.getSnipes().remove(snipe);
            snipes.remove(snipe);
            User jdaUser = Bot.bot.getUserById(user.getId());
            jdaUser.openPrivateChannel()
                    .flatMap(channel -> channel.sendMessage(snipe.getItemName() + " **is in stock!**\n" + snipe.getUrl()))
                    .queue();
        }
    }


}
