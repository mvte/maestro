package maestro.model;

import maestro.sniper.Snipe;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;

public class UserModel implements Serializable {

    /** unique user id from discord */
    private final long id;
    /** blackjack cash*/
    private int cash;
    /** snipe list*/
    private final ArrayList<Snipe> snipes;

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

    public boolean addSnipe(Snipe snipe) {
        if(snipes.contains(snipe)) {
            return false;
        }
        return snipes.add(snipe);
    }

    @Override
    public boolean equals(@NotNull Object o) {
        if(!(o instanceof UserModel))
            return false;

        return ((UserModel)o).getId() == id;
    }

    @Override
    public int hashCode() {
        return (int)id;
    }
}
