package com.dawnfall.engine.Server.world.chunkUtil;

import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.RandomXS128;
import com.dawnfall.engine.Server.world.Blocks.Block;
import com.dawnfall.engine.Server.world.Blocks.Blocks;
import com.dawnfall.engine.Server.world.World;

public class Chunk {
    public static final int CHUNK_SIZE = 16;
    public int x;
    public int y;
    public int z;
    private byte[] blockData;
    private final byte[] lightData;
    /** Is this chunk needs update their mesh. */
    public boolean isDirty;
    /** Is this a new unloaded chunk. Then build the chunk model when player  */
    public boolean isNewChunk = true;
    /** Is this chunk safe to modify blocks. */
    public boolean generated;
    //Tells whether if this chunks is actively being rendered.
    public boolean safe;
    //Tels whether if to render the chunk or not.
    private boolean visible;
    public boolean checked;
    public boolean meshBuilt;
    public boolean isSolid = true;
    public SingleChunkData singleChunkData;
    public ChunkOcclusion.VisibilityInfo visibilityInfo;
    public Chunk(int x, int y, int z){
        int length = CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE;
        this.x = x;
        this.y = y;
        this.z = z;
        //Builds the voxel chunk.
        blockData = new byte[length];
        lightData = new byte[length];
    }
    public synchronized void setChunkVisible(boolean value){
        visible = value;
    }
    public synchronized boolean isChunkVisible() {
        return visible;
    }
    public byte getBlockAt(int x,int y, int z){
        if (x < 0 || x >= CHUNK_SIZE) return Blocks.AIR;
        if (y < 0 || y >= CHUNK_SIZE) return Blocks.AIR;
        if (z < 0 || z >= CHUNK_SIZE) return Blocks.AIR;
        if (singleChunkData != null) return singleChunkData.getBlockAt(x, y, z);
        return blockData[x | y << 8 | z << 4];
    }
    public void setBlockAt(int x, int y, int z,byte id){
        if (x < 0 || x >= CHUNK_SIZE) return;
        if (y < 0 || y >= CHUNK_SIZE) return;
        if (z < 0 || z >= CHUNK_SIZE) return;
        if (singleChunkData != null) {
                singleChunkData.setBlockAt(x, y, z, id);
        }
        blockData[x | y << 8 | z << 4] = id;
    }
    public byte[] getBlockData() {
        return blockData;
    }
    public byte[] getLightData() {
        return lightData;
    }
    /** &15 (mod) will be applied in this method. */
    public void editBlockAt(int x, int y, int z, Block block) {
        final int xFix   = x&15, yFix   = y&15, zFix   = z&15;
        blockData[xFix | yFix << 8 | zFix << 4] = block.getId();
        isDirty = true;
        Chunk chunk;
        final World world = World.getWorld();
        world.chunkManager.DIRTY_CHUNKS.add(this);
        if (yFix == 0) {
            chunk = world.getChunk(x, y-1, z);
            if (chunk != null){
                chunk.isDirty = true;
                world.chunkManager.DIRTY_CHUNKS.add(chunk);
            }
        }
        if (yFix == 15) {
            chunk = world.getChunk(x, y+1, z);
            if (chunk != null){
                chunk.isDirty = true;
                world.chunkManager.DIRTY_CHUNKS.add(chunk);
            }
        }
        if (xFix == 0) {
            chunk = world.getChunk(x-1, y, z);
            if (chunk != null){
                chunk.isDirty = true;
                world.chunkManager.DIRTY_CHUNKS.add(chunk);
            }
        }
        if (xFix == 15) {
            chunk = world.getChunk(x+1, y, z);
            if (chunk != null){
                chunk.isDirty = true;
                world.chunkManager.DIRTY_CHUNKS.add(chunk);
            }
        }
        if (zFix == 0) {
            chunk = world.getChunk(x, y, z-1);
            if (chunk != null){
                chunk.isDirty = true;
                world.chunkManager.DIRTY_CHUNKS.add(chunk);
            }
        }
        if (zFix == 15) {
            chunk = world.getChunk(x, y, z+1);
            if (chunk != null){
                chunk.isDirty = true;
                world.chunkManager.DIRTY_CHUNKS.add(chunk);
            }
        }
    }
    public byte getBlockSmart(int x, int y, int z) {
        return World.world.getBlock(x, y, z);
    }

    public void clear(){
        RandomXS128 randomXS128 = new RandomXS128();
        for (int i = 0; i < CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE; i++) {
            if (randomXS128.nextBoolean()){
                blockData[i] = Blocks.AIR;
            }
        }
    }
    public boolean equals(ChunkPos chunkPos) {
        return x == chunkPos.x && y == chunkPos.y && z == chunkPos.z;
    }
    public boolean equals(GridPoint3 chunkPos) {
        return x == chunkPos.x && y == chunkPos.y && z == chunkPos.z;
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Chunk)) return false;
        Chunk compareChunk = (Chunk) obj;
        return x == compareChunk.x && y == compareChunk.y && z == compareChunk.z;
    }

    @Override
    public String toString() {
        return "X:"+x+":Y:"+y+":Z:"+z;
    }

    public void setSingleChunkData(){
        byte firstId = blockData[0];
        for (byte blockId : blockData) {
            if (blockId != firstId) return;
        }
        blockData = null;
        singleChunkData = new SingleChunkData(this,firstId);
    }
}
