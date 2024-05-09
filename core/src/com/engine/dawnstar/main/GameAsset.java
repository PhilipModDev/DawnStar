package com.engine.dawnstar.main;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import java.util.Optional;

public class GameAsset implements Disposable {

    public final AssetManager assetManager;
    private BitmapFont mainFont;
    private Texture badLogicGames;
    public TextureAtlas atlas;

    public GameAsset(){
        assetManager = new AssetManager();
    }

    //Loads all assets.
    public void loadAssets(boolean block){
        assetManager.load("fonts/main.fnt", BitmapFont.class);
        assetManager.load("badlogic.jpg", Texture.class);
        assetManager.load("textures/blocks.atlas",TextureAtlas.class);
        if (block) {
            assetManager.finishLoading();
            init();
        }
    }
    //Calls to load default assets that are needed for the application.
    public void init(){
        if (!assetManager.isFinished()) return;
        mainFont = assetManager.get("fonts/main.fnt",BitmapFont.class);
        badLogicGames = assetManager.get("badlogic.jpg",Texture.class);
        atlas = assetManager.get("textures/blocks.atlas",TextureAtlas.class);
    }

    public Optional<TextureAtlas.AtlasRegion> findRegion(String name){
        if (atlas == null) return Optional.empty();
        TextureAtlas.AtlasRegion region = atlas.findRegion(name);
        return region == null ? Optional.empty() : Optional.of(region);
    }
    public Texture getAtlasTexture(){
        if (atlas == null) return null;
        TextureRegion region = atlas.findRegion("null");
        return region.getTexture();
    }

    public BitmapFont getMainFont() {
        return mainFont;
    }

    public Texture getBadLogicGames() {
        return badLogicGames;
    }
    //Disposes all assets.
    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
