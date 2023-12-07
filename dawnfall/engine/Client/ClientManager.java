package com.dawnfall.engine.Client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.dawnfall.engine.Client.handle.ui.Debug;
import com.dawnfall.engine.Client.handle.ui.Hud;
import com.dawnfall.engine.Client.handle.Inputs;
import com.dawnfall.engine.DawnStar;
import com.dawnfall.engine.Client.rendering.WorldRenderer;
import com.dawnfall.engine.Client.rendering.BoxManager;
import com.dawnfall.engine.Server.util.IO.OptionProvider;

import static com.badlogic.gdx.Gdx.gl;


/**
 * The client manager is the process manager for
 * handling rendering to the screen.
 * Such topics as meshing, inventory ui design and screen debugging.
 */
public class ClientManager extends ScreenAdapter {
    public WorldRenderer worldRenderer;
    public BoxManager boxManager;
    public Inputs inputs;
    public Debug.DebuggingTools debuggingTools;
    public final DawnStar dawnStar = DawnStar.getInstance();
    private Hud hud;
    public GLProfiler glProfiler;
    private final Camera camera;
    private boolean isDisposed;

    public ClientManager(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void show() {
        if (worldRenderer == null || isDisposed) {
            isDisposed = false;
            hud = new Hud(this);
            inputs = new Inputs(this);
            debuggingTools = new Debug.DebuggingTools();
            debuggingTools.initializeDebugTools();
            glProfiler = new GLProfiler(Gdx.graphics);
            glProfiler.enable();
            worldRenderer = new WorldRenderer(true,this);
            boxManager = new BoxManager(dawnStar.player.getCamera(), worldRenderer.world);
        }
    }
    @Override
    public void render(float delta) {
        if (OptionProvider.DEBUG_MODE) glProfiler.reset();
        drawOpenGL();
        update(delta);
    }
    private void update(float delta){
        dawnStar.setGPUConfigFPS(50000);
        worldRenderer.update(Gdx.graphics.getDeltaTime());
        if (dawnStar.isWorldLoaded){
            if (!hud.showTextBox){
                dawnStar.player.updatePlayer();
                Gdx.input.setInputProcessor(inputs);
            }
        }
        dawnStar.modelBatch.begin(worldRenderer.player.getPerspectiveCamera());
        dawnStar.modelBatch.render(debuggingTools.showXYZLine());
        dawnStar.modelBatch.end();
        hud.render(delta);
    }
    public void drawOpenGL(){
        if (!dawnStar.voxelShaderManager.terrainShader.isCompiled()) return;
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        gl.glClearColor(0.45f, 0.60f, 0.94f, 1);
        gl.glUseProgram(0);
        gl.glEnable(GL20.GL_DEPTH_TEST);
        gl.glDepthFunc(GL20.GL_LEQUAL);
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        hud.resize(width, height);
    }

    public Hud getClientHud() {
        return hud;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        // TODO: 10/31/2023 Fix memory problem.
        isDisposed = true;
        dawnStar.isWorldLoaded = false;
        if (hud != null){
            hud.dispose();
            hud = null;
        }
        if (debuggingTools != null){
            debuggingTools.dispose();
            debuggingTools = null;
        }
        if (boxManager != null){
            boxManager.dispose();
            boxManager = null;
        }
        if (worldRenderer != null){
            worldRenderer.dispose();
            worldRenderer = null;
        }
    }
}
