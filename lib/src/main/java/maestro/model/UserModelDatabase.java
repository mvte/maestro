package maestro.model;

import java.util.ArrayList;
import java.util.HashSet;

public class UserModelDatabase {

    private HashSet<UserModel> users;

    private static UserModelDatabase INSTANCE;

    private UserModelDatabase() {
        this.users = new HashSet<>();
    }

    public static UserModelDatabase getInstance() {
        if(INSTANCE == null)
            INSTANCE = new UserModelDatabase();

        return INSTANCE;
    }

    public UserModel getUser(long id) {
        for(UserModel user : users) {
            if(user.getId() == id)
                return user;
        }

        return null;
    }

    public boolean addUserIfNotExist(UserModel userModel) {
        return users.add(userModel);
    }

}
