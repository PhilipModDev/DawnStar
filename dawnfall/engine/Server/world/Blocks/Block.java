package com.dawnfall.engine.Server.world.Blocks;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dawnfall.engine.Client.rendering.mesh.BlockModel;

public class Block {
    private final BlockType type;
    public final byte id;
    public final String name;
    public final boolean isSolid;
    public final boolean isTrans;
    public final boolean collision;
    public BlockTexture textures;
    private final BlockModel model;
    public Block(byte id, String name, boolean isSoildNcollision, BlockType type) {
        this(id, name, isSoildNcollision, isSoildNcollision, type);
    }
    public Block(byte id, String name, boolean isSolid, boolean collision, BlockType type) {
        this(id, name, isSolid, !isSolid, collision, type);
    }
    public Block(byte id, String name, boolean isSolid, boolean isTrans, boolean collision, BlockType type) {
        this.id = id;
        this.name = name;
        this.isSolid = isSolid;
        this.isTrans = isTrans;
        this.collision = collision;
        this.type = type;
        model = new BlockModel();
    }
    public Block texture(TextureRegion all) {
        textures = new BlockTexture(all, all, all);
        return this;
    }
    public Block texture(TextureRegion topBottom, TextureRegion side) {
        textures = new BlockTexture(topBottom, side, topBottom);
        return this;
    }
    public Block texture(TextureRegion top, TextureRegion side, TextureRegion bottom) {
        textures = new BlockTexture(top, side, bottom);
        return this;
    }
    public static final class BlockTexture {
        public final TextureRegion top, side, bottom;
        public BlockTexture(TextureRegion top, TextureRegion side, TextureRegion bottom) {
            this.top = top;
            this.side = side;
            this.bottom = bottom;
        }
    }
    public BlockType getBlockType(){
        return type;
    }
    public byte getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public BlockModel getModel(){
        return this.model;
    }
}