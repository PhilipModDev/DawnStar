package com.engine.dawnstar.main.data;

import java.util.Objects;
import java.util.UUID;

public final class GameProfile {

    private final String id = UUID.randomUUID().toString();
    private String username;

    public GameProfile(String username){
        this.username = username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameProfile that)) return false;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getUsername(), that.getUsername());
    }


    @Override
    public String toString() {
        return "GameProfile{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
