package maestro.blackjack.interactions;

import maestro.Bot;
import maestro.Config;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

public class Leaderboard implements Interaction{

    @Override
    public void handle(MessageReceivedEvent event, List<String> args) {

        try {
            Connection conn = DriverManager.getConnection(Bot.db_url, Bot.db_user, Config.get("db_pass"));
            System.out.println("connected to db successfully");

            String q = "select * from amounts order by cash desc";

            Statement stmt = conn.createStatement();
            ResultSet lb = stmt.executeQuery(q);

            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle("blackjack leaderboard")
                    .setThumbnail(Bot.bot.getSelfUser().getAvatarUrl());

            int c = 1;
            int userPlace = -1;
            double userCash = -1;
            while(lb.next()) {
                String name = Objects.requireNonNull(Bot.bot.getUserById(lb.getString("id"))).getName();
                if(c <= 10) {
                    eb.addField("#" + c + " " + name, "$" + lb.getDouble("cash"), false);
                }

                if(lb.getString("id").equals(String.valueOf(event.getAuthor().getId()))) {
                    userPlace = c;
                    userCash = lb.getDouble("cash");
                }

                c++;
            }

            if(userPlace != -1 && userCash != -1)
                eb.setFooter("you are #" + userPlace + " with $" + userCash);
            else {
                eb.setFooter("you are not on the leaderboard");
            }

            event.getChannel().sendMessageEmbeds(eb.build()).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getId() {
        return "blackjack:leaderboard";
    }
}
