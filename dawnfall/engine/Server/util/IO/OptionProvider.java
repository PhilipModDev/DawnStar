package com.dawnfall.engine.Server.util.IO;

import com.badlogic.gdx.files.FileHandle;
import com.dawnfall.engine.DawnStar;
import com.dawnfall.engine.Server.util.Constants;

import java.nio.file.Files;
import java.nio.file.Path;

public final class OptionProvider {
    public static float MUSIC_VOLUME;
    public static float AUDIO_DELAY;
    public static int RENDER_DISTANCE;
    public static int WORLD_HEIGHT;
    public static boolean GL_PROFILER;
    public static boolean GL32;
    public static boolean SERVER_PORTED;
    public static boolean DEBUG_MODE;
    public static boolean OCCLUSION;
    public static boolean THREADED_LOADER;
    private final JsonAccessor jsonAccessor;
    private final DawnStar dawnStar;
    public OptionProvider(DawnStar dawnStar){
        this.dawnStar = dawnStar;
        jsonAccessor = new JsonAccessor();
        createDirectories();
    }
    private <T> void writeOptions(String filename,T type){
        try {
            jsonAccessor.writeJson(type, Constants.RESOURCE_KEY +filename+".json");
        }catch (Exception exception){
            exception.printStackTrace();
            System.exit(2);
        }
    }

    private void createDirectories(){
        try {
            Path path = Path.of(Constants.RESOURCE_KEY);
            if (Files.exists(path)) return;
            Files.createDirectories(path);
        }catch (Exception exception){
            exception.printStackTrace();
            System.exit(2);
        }
    }

    public void loadOptions(){
        Options options = new Options();
        String jsonFileName = "options";
        try {
            options = jsonAccessor.readJson(Constants.RESOURCE_KEY+jsonFileName+".json",Options.class);
            dawnStar.getLogger().log(System.Logger.Level.INFO,"Loaded options file:"+Constants.RESOURCE_KEY+jsonFileName);
        } catch (Exception exception) {
            dawnStar.getLogger().log(System.Logger.Level.INFO,"Could not find file:"+Constants.RESOURCE_KEY+jsonFileName);
           writeOptions(jsonFileName,options);
        }
        MUSIC_VOLUME = options.musicVolume;
        AUDIO_DELAY = options.audioDelay;
        RENDER_DISTANCE = options.renderDistance;
        WORLD_HEIGHT = options.worldHeight;
        GL_PROFILER = options.GLProfiler;
        GL32 = options.GL32;
        SERVER_PORTED = options.isServerPorted;
        DEBUG_MODE = options.isDebugging;
        OCCLUSION = options.isOcclusion;
        THREADED_LOADER = options.threadedLoader;
    }
    public static class Options {
       public float musicVolume = 0.1f;
       public float audioDelay = 50.2f;
       public int renderDistance = 20;
       public int worldHeight = 256;
       public boolean GLProfiler = false;
       public boolean GL32 = true;
       public boolean isServerPorted = false;
       public boolean isDebugging = false;
       public boolean isOcclusion = true;
       public boolean threadedLoader = false;
    }
}
