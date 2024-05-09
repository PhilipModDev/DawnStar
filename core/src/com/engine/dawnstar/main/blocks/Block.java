package com.engine.dawnstar.main.blocks;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.engine.dawnstar.DawnStar;

public class Block {
    //Defines the block properties.
    private final BlockState blockState;
    //Defines the block asset data.
    private BlockAsset blockAsset;
    //The name of the block used to register.
    public final String name;
    //The id of the block.
    public final byte id;
    //Creates the default block.
    public Block(String name,int id,boolean isSolid) {
        this(name,(byte) id,isSolid,true);
    }

    public Block(String name,byte id,boolean isSolid, boolean isCollision) {
        this(name, id, isSolid,isCollision,false,false,false,false);
    }

    public Block(String name,byte id,boolean isSolid, boolean isCollision,
                 boolean isTransparent,boolean isLiquid,boolean isLight,boolean isVoid){
        blockState = new BlockState();
        blockState.isSolid = isSolid;
        blockState.isCollision = isCollision;
        blockState.isTransparent = isTransparent;
        blockState.isLiquid = isLiquid;
        blockState.isLight = isLight;
        blockState.isVoid = isVoid;
        this.id = id;
        this.name = name;
        blockAsset = new BlockAsset(name);
    }

    //Sets custom asset for the block.
    public void setBlockAsset(BlockAsset blockAsset){
        this.blockAsset = blockAsset;
    }

    //Returns the block state.
    public BlockState getBlockState() {
        return blockState;
    }
    //Returns the block asset.
    public BlockAsset getBlockAsset() {
        return blockAsset;
    }

    //The block asset for the registered block.
    public final static class BlockAsset {
        //Defines the textures for the block.
        public TextureRegion top;
        public TextureRegion bottom;
        public TextureRegion side;
        //Gets reference towards the block atlas.
        private final TextureAtlas textureAtlas = DawnStar.getInstance().gameAsset.atlas;

        public BlockAsset(){}
        //Creates a new block asset with the name as the resource identifier.
        public BlockAsset(String name){
            TextureRegion region = textureAtlas.findRegion(name);
            top = region != null ? region : textureAtlas.findRegion("null");
            bottom = top;
            side = top;
        }

        public BlockAsset(String topRegion,String bottomRegion,String sideRegion){
            this.top = textureAtlas.findRegion(topRegion);
            if (top == null) top = textureAtlas.findRegion("null");
            this.bottom = textureAtlas.findRegion(bottomRegion);
            if (bottom == null) bottom = textureAtlas.findRegion("null");
            this.side = textureAtlas.findRegion(sideRegion);
            if (side == null) side = textureAtlas.findRegion("null");
        }
    }
}
