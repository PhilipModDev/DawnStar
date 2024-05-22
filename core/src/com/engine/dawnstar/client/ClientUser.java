package com.engine.dawnstar.client;

import com.engine.dawnstar.utils.infrastructure.User;
import java.util.Objects;

public class ClientUser {
    private final User user;

    public ClientUser(String username){
        user = new User();
        user.setUsername(username);
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return user.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClientUser that)) return false;
        return Objects.equals(getUser(), that.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUser());
    }
}
