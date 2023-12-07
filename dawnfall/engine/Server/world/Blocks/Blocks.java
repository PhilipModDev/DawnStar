package com.dawnfall.engine.Server.world.Blocks;

import com.badlogic.gdx.Gdx;
import com.dawnfall.engine.Client.rendering.textureLoaders.GameAssets;

import java.util.function.Supplier;


public class Blocks {
    private static byte i = 0;
    public static final byte
            AIR = i++,
            DEFAULT = i++,
            CAVE_AIR =i++,
            DIRT = i++, STONE = i++,
            BEDROCK =i++, WATER = i++,
            SAND = i++, GRASS = i++,
            LIGHT = i++;
    public static final int size = i;
    public static final Block[] blocks = new Block[size];
    public final GameAssets.TextureHolder textureHolder;
    public boolean isRegistered;
    /** Make sure the EngineAssetLoader is called first */

    public Blocks(GameAssets.TextureHolder textureHolder){
        this.textureHolder = textureHolder;
    }
    public void registerBlocks() {
       try {
           Gdx.app.log("Thread:"+Thread.currentThread().getName(),"Loading block types");
           registerBlock(new Block(DEFAULT, "default", false, BlockType.AIR));
           registerBlock(new Block(AIR, "air", false, BlockType.AIR));
           Class<?> block = Block.class;
           registerBlock(new Block(GRASS, "grass", true, BlockType.SOIL),() -> new Block.BlockTexture(
                   textureHolder.getTextureRegion("grass_top",block),
                   textureHolder.getTextureRegion("grass_side",block),
                   textureHolder.getTextureRegion("dirt",block)));
           registerBlock(new Block(DIRT, "dirt", true, BlockType.SOIL));
           registerBlock(new Block(STONE, "stone", true, BlockType.STONE));
           registerBlock(new Block(BEDROCK, "bedrock", true, BlockType.BED_ROCK));
           registerBlock(new Block(WATER,"water",false,true,true,BlockType.LIQUID));
           registerBlock(new Block(SAND,"sand",true, BlockType.SOIL));
           registerBlock(new Block(CAVE_AIR,"cave_air",false,BlockType.AIR));
           Gdx.app.log("Thread:"+Thread.currentThread().getName(),"Loading block types complete");
       }catch (Exception exception){
           exception.printStackTrace();
       }
    }

    private void registerBlock(Block block){
        blocks[block.id] = block;
        if (block.getBlockType() == BlockType.AIR) return;
        if (block.name.equals("water")){
            blocks[block.id].texture(textureHolder.getTextureRegion("water1",Block.class));
            return;
        }
        blocks[block.id].texture(textureHolder.getTextureRegion(block.name,Block.class));
    }
    private <T extends Block.BlockTexture> void registerBlock(Block block, Supplier<T> supplier){
        blocks[block.id] = block;
        if (block.getBlockType() == BlockType.AIR) return;
        if (block.name.equals("water")){
            blocks[block.id].texture(textureHolder.getTextureRegion("water1",Block.class));
            return;
        }
        blocks[block.id].textures = supplier.get();
    }
    public static Block getBlock(int id){
        return blocks[id];
    }

}