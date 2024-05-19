package com.engine.dawnstar.main;


import com.badlogic.gdx.utils.BufferUtils;
import com.engine.dawnstar.DawnStar;
import com.engine.dawnstar.main.blocks.Block;
import com.engine.dawnstar.main.blocks.Blocks;
import com.engine.dawnstar.main.data.Chunk;
import com.engine.dawnstar.main.data.ChunkPos;
import com.engine.dawnstar.main.mesh.ChunkMesh;
import com.engine.dawnstar.main.mesh.TerrainBuilder;
import com.engine.dawnstar.utils.math.Direction;
import java.nio.IntBuffer;
import java.util.Hashtable;
import java.util.Optional;

public class ChunkBuilder {
    public static final int SQUARED_CHUNK = (int) Math.pow(Chunk.SIZE,2) - 1;
    private final TerrainBuilder terrainBuilder;
    private final Blocks blocks = DawnStar.getInstance().blocks;
    public final IntBuffer indicesData;
    private final Hashtable<ChunkPos,Chunk> chunks;

    public ChunkBuilder(Hashtable<ChunkPos,Chunk> chunks) {
        this.chunks = chunks;
        terrainBuilder = new TerrainBuilder(this);

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

    public Optional<ChunkMesh> create(Chunk chunk) {
        Chunk north = chunks.get(new ChunkPos(chunk.localX,chunk.localY,chunk.localZ + 1));
        Chunk south = chunks.get(new ChunkPos(chunk.localX,chunk.localY,chunk.localZ - 1));
        Chunk east = chunks.get(new ChunkPos(chunk.localX + 1,chunk.localY,chunk.localZ));
        Chunk west = chunks.get(new ChunkPos(chunk.localX - 1,chunk.localY,chunk.localZ));
        Chunk top = chunks.get(new ChunkPos(chunk.localX,chunk.localY + 1,chunk.localZ));
        Chunk down = chunks.get(new ChunkPos(chunk.localX,chunk.localY -1,chunk.localZ));

        int mask = Chunk.SIZE - 1;
        for (int x = 0; x < Chunk.SIZE; x++) {
            for (int y = 0; y < Chunk.SIZE; y++) {
                for (int z = 0; z < Chunk.SIZE; z++) {
                    byte id = chunk.getBlock(x, y, z);
                    Block block = blocks.getBlockById(id);
                    if (block == Blocks.AIR.get()) continue;
                    //Up.
                    if (y + 1 == Chunk.SIZE){
                        if (checkChunk(x,0,z,top)) {
                            terrainBuilder.addFace(Blocks.BLOCK.get(),chunk, x, y, z, Direction.UP.value);
                        }
                    } else if (chunk.getBlock(x, y + 1, z) == 0){
                        terrainBuilder.addFace(Blocks.BLOCK.get(),chunk, x, y, z, Direction.UP.value);
                    }
                    //Down.
                    if (y - 1 == -1){
                        if (checkChunk(x,y + mask,z,down)) {
                            terrainBuilder.addFace(Blocks.BLOCK.get(),chunk, x, y, z, Direction.DOWN.value);
                        }
                    } else if (chunk.getBlock(x, y - 1, z) == 0){
                        terrainBuilder.addFace(Blocks.BLOCK.get(),chunk, x, y, z, Direction.DOWN.value);
                    }

                    //North.
                    if (z + 1 == Chunk.SIZE){
                        if (checkChunk(x,y,0,north)) {
                            terrainBuilder.addFace(Blocks.BLOCK.get(),chunk, x, y, z, Direction.NORTH.value);
                        }
                    } else if (chunk.getBlock(x, y , z + 1) == 0){
                        terrainBuilder.addFace(Blocks.BLOCK.get(),chunk, x, y, z, Direction.NORTH.value);
                    }
                    //South.
                    if (z - 1 == -1){
                        if (checkChunk(x,y ,z + mask,south)) {
                            terrainBuilder.addFace(Blocks.BLOCK.get(),chunk, x, y, z, Direction.SOUTH.value);
                        }
                    } else if (chunk.getBlock(x, y, z - 1) == 0){
                        terrainBuilder.addFace(Blocks.BLOCK.get(),chunk, x, y, z, Direction.SOUTH.value);
                    }

                    //East.
                    if (x + 1 == Chunk.SIZE){
                        if (checkChunk(0,y,z,east)) {
                            terrainBuilder.addFace(Blocks.BLOCK.get(),chunk, x, y, z, Direction.EAST.value);
                        }
                    } else if (chunk.getBlock(x + 1, y , z) == 0){
                        terrainBuilder.addFace(Blocks.BLOCK.get(),chunk, x, y, z, Direction.EAST.value);
                    }
                    //West.
                    if (x - 1 == -1){
                        if (checkChunk(x + mask,y ,z,west)) {
                            terrainBuilder.addFace(Blocks.BLOCK.get(),chunk, x, y, z, Direction.WEST.value);
                        }
                    } else if (chunk.getBlock(x - 1, y, z) == 0){
                        terrainBuilder.addFace(Blocks.BLOCK.get(),chunk, x, y, z, Direction.WEST.value);
                    }
                }
            }
        }
        return Optional.of(terrainBuilder.create(chunk));
    }

    private boolean checkChunk(int x, int y, int z,Chunk chunk){
        if (chunk == null) return true;
        return  (chunk.getBlock(x, y, z) == Blocks.AIR.get().id);
    }

    private ChunkMesh binaryGreedyMeshAlgorithm(Chunk chunk){
//        for (int axis = 0; axis < Chunk.SIZE; axis++){
//            if (!checkData(0,axis,0,binaryMap)) continue;
//            int w = 0;
//            int h = 0;
//
//            if (checkData(1,axis,0,binaryMap)){
//                for (int i = 1; i < Chunk.SIZE; i++) {
//                    if (!checkData(i,axis,0,binaryMap)){
//                        break;
//                    }
//                    setData(i,axis,0,binaryMap,false);
//                    w++;
//                }
//                if (checkData(0,axis + 1,0,binaryMap)){
//                    for (int i = axis + 1; i < Chunk.SIZE; i++) {
//                        if (!checkData(0,i,0,binaryMap)){
//                            break;
//                        }
//                        setData(0,i,0,binaryMap,false);
//                        h++;
//                    }
//                }
//            }
//            terrainBuilder.addFace(Blocks.BLOCK.get(),chunk, 0, axis, 0,w,h, Direction.SOUTH.value);
//        }
        return terrainBuilder.create(chunk);
    }

}
