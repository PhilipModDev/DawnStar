package com.dawnfall.engine.test;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.dawnfall.engine.Client.rendering.textureLoaders.GameAssets;
import com.dawnfall.engine.Server.world.Blocks.Block;
import java.util.HashMap;

public class AssetMangerTest implements ApplicationListener {
    private SpriteBatch batch;
    private Texture region;
    private GameAssets assetHolder;
    private GameAssets.TextureHolder textureHolder;
    @Override
    public void create() {
        batch = new SpriteBatch();
        HashMap<String,Class<?>> map  = new HashMap<>();
        map.put("blocks/blocks.atlas",TextureAtlas.class);
        map.put("blocks/grass_side.png",Texture.class);
        assetHolder = new GameAssets(map);
        textureHolder = new GameAssets.TextureHolder(assetHolder);
    }

    @Override
    public void resize(int width, int height) {
         batch.getProjectionMatrix().setToOrtho2D(0,0,width,height);
    }

    boolean isGotTexture;
    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (assetHolder.updateAssetHolder()) {
            if (!isGotTexture){
                region = textureHolder.getTexture("grass_side", Block.class);
                isGotTexture = true;
            }
            batch.begin();
            //Render code.
            batch.draw(region,Gdx.graphics.getWidth() * 0.5f,Gdx.graphics.getHeight() * 0.5f,100,100);
            batch.end();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
       batch.dispose();
       assetHolder.dispose();
    }
}
