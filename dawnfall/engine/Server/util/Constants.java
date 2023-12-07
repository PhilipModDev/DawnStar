package com.dawnfall.engine.Server.util;

//Engine constants value class.
/**
 * By PhilipModDev.
 * Date:10/31/2023
 */

public final class Constants {
    //Game id. Tells whether if is a mod or not.
    public final static String DAWNSTAR_ID = "dawnstar";
    //The Menu ui skin json settings path.
    public static final String UI_SKIN = "ui/ui_skin.json";
    //The path to block texture atlas.
    public static final String BLOCK_ATLAS = "blocks/blocks.atlas";
    //The path to block texture image png file.
    public static final String BLOCK_TEXTURES = "blocks/blocks.png";
    //The directory external resource key for look up files.
    public static final String RESOURCE_KEY = DAWNSTAR_ID + "/engine/";
    //The menu ui background image.
    public static final String UI_DAWNSTAR_BACKGROUND_IMAGE = "ui/backgrounds/ui_dawnstar_background_image.png";
    //The default missing texture image file path.
    public static final String MISSING_TEXTURE = "blocks/missing.png";
    //The menu ui font file settings path.
    public static final String UI_FONT = "ui/fonts/arial-15.fnt";
    //Ratio for max chunks that can be created every frame.
    public static final int CHUNKS_FRAME_BASE = 100;
    //Ratio for the chunks that tick update for refreshing.
    public static final int CHUNK_UPDATE_DISTANCE = 4;
    /*
     * Game state values.
     * Tells whether if the game is (pause) (resume) (render) (stop)
     */
    public enum GameState {
        PAUSE,
        RESUME,
        STOP,
        RENDER
    }
}

