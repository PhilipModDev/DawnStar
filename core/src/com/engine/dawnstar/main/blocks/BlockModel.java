package com.engine.dawnstar.main.blocks;

public class BlockModel {
    private final String model;
    private final BlockTexture textures;
    public BlockModel(String model,BlockTexture blockTexture ) {
        this.model = model;
        this.textures = blockTexture;
    }
    public String getModel() {
        return model;
    }
    public BlockTexture getBlockTexture() {
        return textures;
    }
    public record BlockTexture(String top, String bottom, String sides) {
    }
}
