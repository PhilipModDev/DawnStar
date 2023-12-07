package com.dawnfall.engine.Server.util.IO;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class JsonAccessor implements JsonModel {
    private final Gson gson;
    public JsonAccessor(){
        gson = new Gson();
    }
    @Override
    public <T> T readJson(String filepath, Class<?> type) throws IOException {
        BufferedReader reader = Files.newBufferedReader(Path.of(filepath));
        Object object = gson.fromJson(reader,type);
        reader.close();
        return (T) object;
    }
    @Override
    public <T> void writeJson(T properties, String filepath) throws IOException {
        Path path = Path.of(filepath);
        boolean isNewFile = checkIsNew(path);
        BufferedWriter writer;
        if (isNewFile){
            writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        }else {
            writer = Files.newBufferedWriter(path,StandardOpenOption.WRITE);
        }
        gson.toJson(properties,writer);
        writer.close();
    }
    @Override
    public <T> T readJsonString(String jsonString,Class<?> type) {
        Object object = gson.fromJson(jsonString,type);
        return (T) object;
    }

    @Override
    public String writeJsonString(Object properties){
        return gson.toJson(properties,properties.getClass());
    }

    private boolean checkIsNew(Path path){
        return Files.notExists(path);
    }
}
