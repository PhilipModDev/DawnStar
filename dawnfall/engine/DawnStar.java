package com.dawnfall.engine;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.dawnfall.engine.Client.ClientManager;
import com.dawnfall.engine.Client.handle.ui.*;
import com.dawnfall.engine.Client.rendering.WorldRenderer;
import com.dawnfall.engine.Client.rendering.mesh.BlockModel;
import com.dawnfall.engine.Client.rendering.mesh.VertexInfo;
import com.dawnfall.engine.Client.rendering.textureLoaders.GameAssets;
import com.dawnfall.engine.Server.entity.Player;
import com.dawnfall.engine.Server.features.GameAudioRegistry;
import com.dawnfall.engine.Server.events.Events;
import com.dawnfall.engine.Client.rendering.shaders.VoxelShaderManager;
import com.dawnfall.engine.Server.util.Constants;
import com.dawnfall.engine.Server.util.IO.OptionProvider;
import com.dawnfall.engine.Server.util.ThreadUtilsFactory;
import com.dawnfall.engine.Server.util.annotations.ContextType;
import com.dawnfall.engine.Server.world.Blocks.Blocks;
import com.google.gson.Gson;


//Main Render Loop.
@ContextType(context = Constants.DAWNSTAR_ID)
public class DawnStar implements ApplicationListener {
    private static DawnStar dawnStar;
    private GameAssets gameAssets;
    private GameAssets.TextureHolder textureHolder;
    public Constants.GameState GAME_STATE;
    public final Events EVENTS;
    private final System.Logger logger = System.getLogger("logger");
    public ClientManager clientManager;
    public ScreenViewport screenViewport;
    private MenuManager menuManager;
    public GameAudioRegistry audio;
    private Blocks blocks;
    public boolean isWorldLoaded = false;
    public int loadingPercent;
    public Player player;
    public VoxelShaderManager voxelShaderManager;
    public SpriteBatch spriteBatch;
    public ShapeRenderer shapeRenderer;
    public ModelBatch modelBatch;
    private final ThreadUtilsFactory threadUtilsFactory;

    public DawnStar() {
        EVENTS = new Events(this);
        //Load all options.
        OptionProvider optionProvider = new OptionProvider(this);
        optionProvider.loadOptions();
        dawnStar = this;
        threadUtilsFactory = OptionProvider.THREADED_LOADER ? new ThreadUtilsFactory(1,4,32,3000) : new ThreadUtilsFactory();
    }

    @Override
    public void create() {
        //Load Assets.
        gameAssets = new GameAssets();
        textureHolder = new GameAssets.TextureHolder(gameAssets);
        gameAssets.loadAsset(Constants.BLOCK_ATLAS, TextureAtlas.class);
        gameAssets.loadAsset(Constants.BLOCK_TEXTURES, Texture.class);
        blocks = new Blocks(textureHolder);
        //Load OpenGL shaders.
        voxelShaderManager = new VoxelShaderManager();
        screenViewport = new ScreenViewport();
        if (voxelShaderManager.isErrorFound()){
            System.err.println("Error loading shaders.");
            System.exit(1);
        }
        modelBatch = new ModelBatch();
        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        //Load Mods (here)


        //Load UI menus.
        audio = new GameAudioRegistry();
        player = new Player(70,Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),new Vector3(0,90,0));
        clientManager = new ClientManager(player.getCamera());
        menuManager = new MenuManager(this);
        menuManager.create();
        //Loading Game state.
        GAME_STATE = Constants.GameState.RENDER;
        Gdx.app.log("Thread:"+Thread.currentThread().getName(),"Launching application on (LWJGL) version "+Gdx.graphics.getGLVersion().getMajorVersion());
    }

    //Resize.
    @Override
    public void resize(int width, int height){
        //Set the width and the height for resizing.
        screenViewport.update(width,height);
        menuManager.resize(width, height);
    }
    //Render.
    @Override
    public void render() {
        if (gameAssets.updateAssetHolder(17)) {
            if (!blocks.isRegistered){
                blocks.registerBlocks();
                blocks.isRegistered = true;
            }
            switch (GAME_STATE) {
                case STOP:
                    Gdx.app.exit();
                    break;
                case RENDER:
                    renderOpenGLApplication();
                    updateDisplayModeListener();
                    break;
                case PAUSE:
                    pauseOpenGLApplication();
                    updateDisplayModeListener();
                    break;
                case RESUME:
                    resumeOpenGLApplication();
                    break;
            }
            audio.updateAudioRegistry();
        }
    }

    private void pauseOpenGLApplication(){
        GAME_STATE = Constants.GameState.PAUSE;
        pause();
    }

    private void resumeOpenGLApplication(){
        resume();
    }

    private void renderOpenGLApplication(){
        menuManager.render();
    }
  //Renders the current stage.
    boolean isFullscreen;
    private void updateDisplayModeListener(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.F11)){
            if (isFullscreen){
                Gdx.graphics.setWindowedMode(1080,720);
                isFullscreen = false;
            }else {
                isFullscreen = true;
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        }
    }

    public void setGPUConfigFPS(int fpsCount){
        Gdx.graphics.setForegroundFPS(fpsCount);
    }

    @Override
    public void pause() {
        menuManager.pause();
    }
    @Override
    public void resume() {
        clientManager.resume();
        menuManager.resume();
        GAME_STATE = Constants.GameState.RENDER;
    }

    @Override
    public void dispose() {
        clientManager.dispose();
        audio.dispose();
        menuManager.dispose();
        shapeRenderer.dispose();
        this.gameAssets.dispose();
        System.exit(0);
    }

    public static DawnStar getInstance() {
        return dawnStar;
    }
    public System.Logger getLogger() {
        return logger;
    }
    public GameAssets getGameAssets(){
       return this.gameAssets;
    }
    public GameAssets.TextureHolder getTextureHolder(){
        return this.textureHolder;
    }
    public MenuManager getMenuManager(){
        return this.menuManager;
    }
    public WorldRenderer getWorldRenderer(){
      return this.clientManager.worldRenderer;
    }

    public ThreadUtilsFactory getThreadFactory() {
        return threadUtilsFactory;
    }
}

