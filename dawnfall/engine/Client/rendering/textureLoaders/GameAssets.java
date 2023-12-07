package com.dawnfall.engine.Client.rendering.textureLoaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.dawnfall.engine.Server.util.Constants;
import com.dawnfall.engine.Server.world.Blocks.Block;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameAssets implements Disposable {
    //The manager for contain all assets.
    private final AssetManager assetManager;

    public GameAssets(HashMap<String,Class<?>> textureObjects) {
        assetManager = new AssetManager();
        Set<Map.Entry<String,Class<?>>> textureSet = textureObjects.entrySet();
        for (Map.Entry<String,Class<?>> entry : textureSet){
            String filename = entry.getKey();
            Class<?> classType = entry.getValue();
            loadAsset(filename,classType);
        }
    }

    public GameAssets() {
        assetManager = new AssetManager();
    }

    //Loads an asset from the filename and type.
    public void loadAsset(String filename, Class<?> classType) {
        assetManager.load(filename,classType);
    }

    //Updates the assets task queue to be loaded.
    public boolean updateAssetHolder(){
       return assetManager.update();
    }
    public boolean updateAssetHolder(int ms){
        return assetManager.update(ms);
    }

    //Returns asset object found within the manager.
    public <T> T getAssetObject(String filename, Class<T> classType) {
       return assetManager.get(filename,classType);
    }

    //Disposes all assets.
    @Override
    public void dispose() {
        assetManager.dispose();
    }
    //Asset type of TextureHolder.
    public static class TextureHolder {
        private final GameAssets assets;
        public TextureHolder(GameAssets assets){
            this.assets = assets;

        }
        /**
         * Returns a texture by the textureName and the type.
         * <code>Block.class<code/>
         */
        public TextureRegion getTextureRegion(String textureName, Class<?> classType){
            TextureAtlas textureAtlas;
            if (classType == Block.class){
                textureAtlas = assets.getAssetObject(Constants.BLOCK_ATLAS,TextureAtlas.class);
                TextureRegion region = textureAtlas.findRegion(textureName);
                if (region == null) {
                    Gdx.app.error("Atlas","Could not find texture named:"+textureName);
                    return  textureAtlas.findRegion("missing");
                }
                return region;
            }
            //Add the other types such as loading from a model.
            return null;
        }
        public Texture getTexture(String textureName, Class<?> classType){
            Texture texture;
            if (classType == Block.class){
                texture = assets.getAssetObject("blocks/"+textureName+".png",Texture.class);
                if (texture == null) {
                    Gdx.app.error("Atlas","Could not find texture named:"+textureName);
                    return  new Texture(Gdx.files.internal(Constants.MISSING_TEXTURE));
                }
                return texture;
            }
            //Add the other types such as loading from a model.
            return null;
        }
        public Texture getTexture(String textureName, Class<?> classType, boolean useMipMaps){
            Texture texture;
            if (classType == Block.class){
                texture = assets.getAssetObject("blocks/"+textureName+".png",Texture.class);
                if (texture == null) {
                    Gdx.app.error("Atlas","Could not find texture named:"+textureName);
                    return  new Texture(Gdx.files.internal(Constants.MISSING_TEXTURE),useMipMaps);
                }
                return texture;
            }
            //Add the other types such as loading from a model.
            return null;
        }
        public TextureRegion getCross() {
            return getTextureRegion("cross",Block.class);
        }
        public TextureRegion getMissing(){
            return getTextureRegion("missing",Block.class);
        }

        public TextureRegion getRegion(){
            Texture texture = getTexture("blocks", Block.class,true);
            texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.MipMap);
            return new TextureRegion(texture);
        }
    }
}
