package com.engine.dawnstar.utils.infrastructure;

import com.engine.dawnstar.Constants;
import com.engine.dawnstar.server.DawnStarServer;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.engine.dawnstar.utils.io.ResourceLocation.GSON;


public class UserList {
    public static final File USER_CONNECTION = new File(Constants.SERVER_DATA + "user_connections.json");
    public static final Path PATH = Path.of(Constants.SERVER_DATA);
    private final DawnStarServer dawnStarServer;
    private List<User> list = new ArrayList<>();

    public UserList(DawnStarServer dawnStarServer){
        this.dawnStarServer = dawnStarServer;
        FileWriter writer;
        try {
            if (Files.notExists(PATH)){
                Files.createDirectories(PATH);
                Files.createFile(USER_CONNECTION.toPath());
                writer = new FileWriter(USER_CONNECTION);
                GSON.toJson(list,writer);
                writer.close();
            }else {
                readConnections();
            }
        }catch (Exception exception){
            exception.printStackTrace(System.out);
        }
    }

    private void readConnections(){
        try {
            FileReader reader = new FileReader(USER_CONNECTION);
            TypeToken<List<User>> listTypeToken = new TypeToken<>(){};
            list = GSON.fromJson(reader,listTypeToken);
        }catch (Exception exception){
            exception.printStackTrace(System.out);
        }
    }

    public boolean isAllow(String username){
        readConnections();
        User user = new User();
        user.setUsername(username);
        return list.contains(user);
    }

    public void setAllow(String username){
        User user = new User();
        user.setUsername(username);
       if (!list.contains(user)){
           try (FileWriter writer = new FileWriter(USER_CONNECTION)) {
               list.add(user);
               GSON.toJson(list,writer);
           }catch (Exception exception) {
               exception.printStackTrace(System.out);
           }
       }
    }

    public boolean isAllow(User user){
        readConnections();
        return list.contains(user);
    }

    public void setAllow(User user){
        if (!list.contains(user)){
            try (FileWriter writer = new FileWriter(USER_CONNECTION)) {
                list.add(user);
                GSON.toJson(list,writer);
            }catch (Exception exception) {
                exception.printStackTrace(System.out);
            }
        }
    }
}
