package com.engine.dawnstar.main.blocks;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.engine.dawnstar.client.DawnStarClient;
import com.engine.dawnstar.main.mesh.Model;
import com.engine.dawnstar.utils.io.ResourceLocation;
import java.util.Optional;

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
    }

    //Sets custom asset for the block.
    public void loadBlockAsset(BlockAsset blockAsset){
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
        public Model model;
        //Gets reference towards the block atlas.
        private final TextureAtlas textureAtlas = DawnStarClient.getInstance().gameAsset.atlas;

        //Creates a new block asset with the name as the resource identifier.
        public BlockAsset(String name){
            ResourceLocation resourceLocation = DawnStarClient.getInstance().getResourceLocation();
            Optional<BlockModel> optionalBlockModel = resourceLocation.loadVoxelData(name,Block.class);
            var blockModel = optionalBlockModel.orElseThrow();
            if (blockModel.getModel().equals("cube")){
                Blocks blocks = DawnStarClient.getInstance().blocks;
                this.model = blocks.getCube();
                var blockTexture = blockModel.getBlockTexture();
                loadTextures(blockTexture);
            }
        }

        private void loadTextures(BlockModel.BlockTexture blockTexture){
            if (blockTexture.top().contains("air")) return;
            if (blockTexture.top().contains("all/")){
                String name = blockTexture.top().substring(blockTexture.top().lastIndexOf("/") + 1);
                top = textureAtlas.findRegion(name) == null ? textureAtlas.findRegion("null")
                        : textureAtlas.findRegion(name);
                bottom = top;
                side = top;
                return;
            }
            String name = blockTexture.top().substring(blockTexture.top().lastIndexOf("/") + 1);
            top = textureAtlas.findRegion(name);
            name = blockTexture.bottom().substring(blockTexture.bottom().lastIndexOf("/") + 1);
            bottom = textureAtlas.findRegion(name);
            name = blockTexture.sides().substring(blockTexture.sides().lastIndexOf("/") + 1);
            side =  textureAtlas.findRegion(name);
        }
    }
}
