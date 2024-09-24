package shticell.manager.user;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserManager {
    Set<String> users = new HashSet<>();

    public boolean addUser(String user){
        return users.add(user);
    }

    public boolean removeUser(String user){
        return users.remove(user);
    }

    public Set<String> getUsers(){
        return Collections.unmodifiableSet(users);
    }
}
