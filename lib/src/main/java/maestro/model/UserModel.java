package maestro.model;

import maestro.sniper.Snipe;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;

public class UserModel implements Serializable {

    /** unique user id from discord */
    private long id;
    /** blackjack cash*/
    private int cash;
    /** snipe list*/
    private ArrayList<Snipe> snipes;

    public UserModel(long id) {
        this.id = id;
        cash = 0;
        snipes = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public ArrayList<Snipe> getSnipes() {
        return snipes;
    }

    public void addSnipe(Snipe snipe) {
        snipes.add(snipe);
    }

    @Override
    public boolean equals(@NotNull Object o) {
        if(!(o instanceof UserModel))
            return false;

        return ((UserModel)o).getId() == id;
    }
}
