package maestro.sniper;

import maestro.Bot;
import maestro.model.UserModel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

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
        Bot.service.scheduleAtFixedRate(this::checkSnipes, 0, 800, TimeUnit.MILLISECONDS);
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
        snipes.remove(snipe);
        String stockMessage = snipe instanceof RutgersSnipe ? " **is open!**\n" : " **is in stock!**\n";
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(snipe.getItemName() + stockMessage)
                .setThumbnail(Bot.bot.getSelfUser().getAvatarUrl())
                .addField("now's your chance.", "press the button below to go to the item's webpage", false)
                .setTimestamp(java.time.Instant.now());
        ActionRow ar = ActionRow.of(Button.link(snipe.getUrl(), "go to"));

        for(UserModel user : snipe.getUsers()) {
            user.getSnipes().remove(snipe);
            User jdaUser = Bot.bot.getUserById(user.getId());

            jdaUser.openPrivateChannel()
                    .flatMap(channel -> channel.sendMessageEmbeds(eb.setFooter(jdaUser.getName()).build())
                            .setActionRows(ar))
                    .queue();
        }
    }
}
