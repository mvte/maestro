package maestro.sniper;

import maestro.model.UserModel;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Snipe implements Serializable {

    String url;
    String itemName;
    ArrayList<UserModel> users;

    public abstract boolean inStock();

    public void addUser(UserModel user) {
        users.add(user);
    }

    public String getUrl() {
        return url;
    }

    public String getItemName() {
        return itemName;
    }

    public ArrayList<UserModel> getUsers() {
        return users;
    }

    public abstract String parseItemName();


}
