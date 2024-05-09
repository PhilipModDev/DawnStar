package com.engine.dawnstar.main;


import com.badlogic.gdx.utils.BufferUtils;
import com.engine.dawnstar.DawnStar;
import com.engine.dawnstar.main.blocks.Block;
import com.engine.dawnstar.main.blocks.Blocks;
import com.engine.dawnstar.main.data.Chunk;
import com.engine.dawnstar.main.mesh.ChunkMesh;
import com.engine.dawnstar.main.mesh.TerrainBuilder;
import com.engine.dawnstar.utils.math.Direction;
import java.nio.IntBuffer;

public class ChunkBuilder {
    public static final int SQUARED_CHUNK = (int) Math.pow(Chunk.SIZE,3) - 1;
    private final TerrainBuilder terrainBuilder;
    private final Blocks blocks = DawnStar.getInstance().blocks;
    public IntBuffer indicesData;

    public ChunkBuilder() {
        terrainBuilder = new TerrainBuilder(this);
        generateIndices();
    }
    private void generateIndices(){
        int maxIndices = 196608 * 6; // 32^3 * 6 * 6
        indicesData = BufferUtils.newIntBuffer(maxIndices);
        int[] indices = new int[maxIndices];
        for (int i = 0, v = 0; i < maxIndices; i += 6, v += 4) {
            indices[i] = v;
            indices[i+1] = (v+1);
            indices[i+2] = (v+2);
            indices[i+3] = (v+2);
            indices[i+4] = (v+3);
            indices[i+5] =  v;
        }
        indicesData.put(indices);
        indicesData.flip();
    }

    public ChunkMesh create(Chunk chunk) {
        for (int x = 0; x < Chunk.SIZE; x++) {
            for (int y = 0; y < Chunk.SIZE; y++) {
                for (int z = 0; z < Chunk.SIZE; z++) {
                    byte id = chunk.getBlock(x, y, z);
                    Block block = blocks.getBlockById(id);
                    if (block == Blocks.AIR.get()) continue;
                    checkVoxel(x, y, z, chunk);
                }
            }
        }
        return terrainBuilder.create(chunk);
    }

    private void checkVoxel(int x, int y, int z, Chunk chunk) {
        if (chunk.getBlock(x, y + 1, z) == 0) {
            terrainBuilder.addFace(Blocks.BLOCK.get(), x, y, z, Direction.UP.value);
        }
        if (chunk.getBlock(x, y - 1, z) == 0) {
            terrainBuilder.addFace(Blocks.BLOCK.get(), x, y, z, Direction.DOWN.value);
        }
        if (chunk.getBlock(x, y, z + 1) == 0) {
            terrainBuilder.addFace(Blocks.BLOCK.get(), x, y, z, Direction.NORTH.value);
        }
        if (chunk.getBlock(x, y, z - 1) == 0) {
            terrainBuilder.addFace(Blocks.BLOCK.get(), x, y, z, Direction.SOUTH.value);
        }
        if (chunk.getBlock(x + 1, y, z) == 0) {
            terrainBuilder.addFace(Blocks.BLOCK.get(), x, y, z, Direction.EAST.value);
        }
        if (chunk.getBlock(x - 1, y, z) == 0) {
            terrainBuilder.addFace(Blocks.BLOCK.get(), x, y, z, Direction.WEST.value);
        }
    }
}
