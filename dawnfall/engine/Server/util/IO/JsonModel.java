package com.dawnfall.engine.Server.util.IO;

import java.io.IOException;

public interface JsonModel {
    <T> T readJson(String filepath, Class<?> type) throws IOException;
    <T> void writeJson(T properties, String filepath) throws IOException;
    <T> T readJsonString(String jsonString,Class<?> type);
    <T> String writeJsonString(T properties);
}