package com.dawnfall.engine.Server.world.gen;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.dawnfall.engine.Server.world.chunkUtil.ChunkColumn;
import com.dawnfall.engine.Server.world.Blocks.Blocks;
import com.dawnfall.engine.Server.world.chunkUtil.ChunkPos;
import com.dawnfall.engine.Server.world.chunkUtil.Chunk;
import com.dawnfall.engine.Server.world.chunkUtil.ChunkManager;
import com.dawnfall.engine.Server.util.math.perlinNoise.OpenSimplex2S;


public class ChunkDataGeneration implements Generation {
    public static int heightOffset = 100;
    public static final int CHUNK_HEIGHT_OFFSET = heightOffset / Chunk.CHUNK_SIZE;
    public static float heightIntensity = 15;
    public static Vector2 NoiseOffset = new Vector2(1.5f, 0);
    public static Vector2 NoiseScale = new Vector2(1.9f, 1.8f);
    public static long seed;
    public int seaLevel = 100;
    private static final double size = 150.0; // 128.0
    private static final double scale = 100.0; // 50.0
    private static final double heightScl = 0.5; // 0.8
    private static final double depth = 16.0; // 16.0

    public static final double height = 52.0; // 46.0
    private final RandomXS128 randomXS128;
    private final ChunkManager chunkManager;
    public ChunkDataGeneration(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
        randomXS128 = new RandomXS128();
        seed = randomXS128.nextLong();
    }
    public ChunkColumn generateData(ChunkPos chunkPos) {
          ChunkPos columnPos = new ChunkPos(chunkPos.x,0,chunkPos.z);
          if (chunkManager.hasNeighboursColumnes(columnPos.x, columnPos.z)) {
              chunkManager.dataSize = chunkManager.CHUNK_COLUMNS.size();
              return chunkManager.CHUNK_COLUMNS.get(columnPos);
          }
          chunkManager.dataSize = chunkManager.CHUNK_COLUMNS.size();
          //Current.
          if (!chunkManager.CHUNK_COLUMNS.containsKey(columnPos)) {
              generateColumn(columnPos);
              columnPos.set(columnPos.x,0,columnPos.z);
          }
          //North.
          columnPos.set(columnPos.x, columnPos.y, columnPos.z + 1);
          if (!chunkManager.CHUNK_COLUMNS.containsKey(columnPos)) {
              generateColumn(columnPos);
              columnPos.set(columnPos.x,columnPos.y,columnPos.z - 1);
          }
          //South.
          columnPos.set(columnPos.x, columnPos.y, columnPos.z - 1);
          if (!chunkManager.CHUNK_COLUMNS.containsKey(columnPos)) {
              generateColumn(columnPos);
              columnPos.set(columnPos.x, columnPos.y, columnPos.z + 1);
          }
          //East.
          columnPos.set(columnPos.x + 1, columnPos.y, columnPos.z);
          if (!chunkManager.CHUNK_COLUMNS.containsKey(columnPos)) {
              generateColumn(columnPos);
              columnPos.set(columnPos.x - 1, columnPos.y, columnPos.z);
          }
          //West.
          columnPos.set(columnPos.x - 1, columnPos.y, columnPos.z);
          if (!chunkManager.CHUNK_COLUMNS.containsKey(columnPos)) {
              generateColumn(columnPos);
              columnPos.set(columnPos.x + 1, columnPos.y, columnPos.z);
          }
       return chunkManager.CHUNK_COLUMNS.get(columnPos);
    }

    public ChunkColumn generateData_2(ChunkPos chunkPos) {
        ChunkPos columnPos = new ChunkPos(chunkPos.x,0,chunkPos.z);
            if (chunkManager.hasNeighboursColumnes(columnPos.x, columnPos.z)) {
                chunkManager.dataSize = chunkManager.CHUNK_COLUMNS.size();
                return chunkManager.CHUNK_COLUMNS.get(columnPos);
            }else {
                if (!chunkManager.CHUNK_COLUMNS.containsKey(columnPos)){
                    ChunkColumn column = new ChunkColumn( columnPos.x,  columnPos.z);
                    generateColumn(column);
                }
                //North.
                columnPos.set(columnPos.x, columnPos.y, columnPos.z + 1);
                if (!chunkManager.CHUNK_COLUMNS.containsKey(columnPos)){
                    ChunkColumn chunkColumn = new ChunkColumn( columnPos.x, columnPos.z);
                    generateColumn(chunkColumn);
                    columnPos.set(columnPos.x,columnPos.y,columnPos.z - 1);
                }
                //South.
                columnPos.set(columnPos.x, columnPos.y, columnPos.z - 1);
                if (!chunkManager.CHUNK_COLUMNS.containsKey(columnPos)){
                    ChunkColumn chunkColumn = new ChunkColumn(columnPos.x, columnPos.z);
                    generateColumn(chunkColumn);
                    columnPos.set(columnPos.x, columnPos.y, columnPos.z + 1);
                }
                //East.
                columnPos.set(columnPos.x + 1, columnPos.y, columnPos.z);
                if (!chunkManager.CHUNK_COLUMNS.containsKey(columnPos)){
                    ChunkColumn chunkColumn = new ChunkColumn(columnPos.x, columnPos.z);
                    generateColumn(chunkColumn);
                    columnPos.set(columnPos.x - 1, columnPos.y, columnPos.z);
                }
                //West.
                columnPos.set(columnPos.x - 1, columnPos.y, columnPos.z);
                if (!chunkManager.CHUNK_COLUMNS.containsKey(columnPos)){
                    ChunkColumn chunkColumn = new ChunkColumn(columnPos.x,columnPos.z);
                    generateColumn(chunkColumn);
                    columnPos.set(columnPos.x + 1, columnPos.y, columnPos.z);
                }
            }
        return chunkManager.CHUNK_COLUMNS.get(columnPos);
    }
    public ChunkColumn generateData_3(ChunkPos chunkPos) {
        ChunkPos columnPos = new ChunkPos(chunkPos.x,0,chunkPos.z);
            if (chunkManager.hasNeighbours( chunkPos.x, chunkPos.y, chunkPos.z)) {
                chunkManager.dataSize = chunkManager.CHUNK_COLUMNS.size();
                return chunkManager.CHUNK_COLUMNS.get(chunkPos);
            }else {
                if (!chunkManager.CHUNK_COLUMNS.containsKey(columnPos)){
                    ChunkColumn column = new ChunkColumn( columnPos.x,  columnPos.z);
                    generateColumn(column);
                }
                //North.
                columnPos.set(columnPos.x, columnPos.y, columnPos.z + 1);
                if (!chunkManager.CHUNK_COLUMNS.containsKey(columnPos)){
                    ChunkColumn chunkColumn = new ChunkColumn( columnPos.x, columnPos.z);
                    generateColumn(chunkColumn);
                    columnPos.set(columnPos.x,columnPos.y,columnPos.z - 1);
                }
                //South.
                columnPos.set(columnPos.x, columnPos.y, columnPos.z - 1);
                if (!chunkManager.CHUNK_COLUMNS.containsKey(columnPos)){
                    ChunkColumn chunkColumn = new ChunkColumn(columnPos.x, columnPos.z);
                    generateColumn(chunkColumn);
                    columnPos.set(columnPos.x, columnPos.y, columnPos.z + 1);
                }
                //East.
                columnPos.set(columnPos.x + 1, columnPos.y, columnPos.z);
                if (!chunkManager.CHUNK_COLUMNS.containsKey(columnPos)){
                    ChunkColumn chunkColumn = new ChunkColumn(columnPos.x, columnPos.z);
                    generateColumn(chunkColumn);
                    columnPos.set(columnPos.x - 1, columnPos.y, columnPos.z);
                }
                //West.
                columnPos.set(columnPos.x - 1, columnPos.y, columnPos.z);
                if (!chunkManager.CHUNK_COLUMNS.containsKey(columnPos)){
                    ChunkColumn chunkColumn = new ChunkColumn(columnPos.x,columnPos.z);
                    generateColumn(chunkColumn);
                    columnPos.set(columnPos.x + 1, columnPos.y, columnPos.z);
                }
            }
        return chunkManager.CHUNK_COLUMNS.get(columnPos);
    }

    @Override
    public void generateColumn(ChunkPos offset) {
        ChunkColumn column = new ChunkColumn(offset.x,offset.z);
        int noise_1;
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            float PerlinX = NoiseOffset.x + (x + offset.x * 16f) / Chunk.CHUNK_SIZE * NoiseScale.x;
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                float PerlinY = NoiseOffset.y + (z + offset.z * 16f) / Chunk.CHUNK_SIZE * NoiseScale.y;
                noise_1 = Math.round(OpenSimplex2S.noise2_ImproveX(seed, PerlinX / 7.2f, PerlinY / 9.0f) * heightIntensity + heightOffset);
                //Fixed to flat.
                int heightGen = noise_1;
                for (int y = heightGen; y >= 0; y--) {
                     int yLevel = y >>>4;
                    //Air.
                    column.chunks[yLevel].setBlockAt(x&15,y&15,z&15,Blocks.AIR);
                    //Grass.
                    if (y == heightGen) {
                        if (randomXS128.nextInt(10) > noise_1){
                            column.chunks[yLevel].setBlockAt(x&15,y&15,z&15,Blocks.DIRT);
                        }else {
                            column.chunks[yLevel].setBlockAt(x&15,y&15,z&15,Blocks.GRASS);
                        }
                        column.chunks[yLevel].generated = true;
                    }
                    //Dirt.
                    if (y < heightGen && y >= heightGen - 8) {
                        column.chunks[yLevel].setBlockAt(x&15,y&15,z&15,Blocks.DIRT);
                        column.chunks[yLevel].generated = true;
                    }
                    //Stone.
                    if (y <= heightGen - 8 && y > 0) {
                        column.chunks[yLevel].setBlockAt(x&15,y&15,z&15,Blocks.STONE);;
                        column.chunks[yLevel].generated = true;
                    }
                    //BedRock.
                    if (y  < 2) {
                        column.chunks[yLevel].setBlockAt(x&15,y&15,z&15,Blocks.BEDROCK);
                        column.chunks[yLevel].generated = true;
                    }
                }
            }
        }
        generateTerrainFeatures(column);
        caveNoise(column);
//        caveNoise2(column);

//        for (int i = 0; i < ChunkColumn.HEIGHT; i++) {
//            Chunk chunk = column.chunks[i];
//            chunk.setSingleChunkData();
//        }
        ChunkPos columPos = new ChunkPos(column.xC,0,column.zC);
        chunkManager.CHUNK_COLUMNS.put(columPos,column);
    }
    @Override
    public void generateColumn(ChunkColumn column) {
        final int xOffset = column.xC *Chunk.CHUNK_SIZE;
        final int zOffset = column.zC *Chunk.CHUNK_SIZE;
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            int realX = x+xOffset;
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                int realZ = z+zOffset;
                double lump = OpenSimplex2S.noise2(seed,realX/32d, realZ/32d)*4d;
                double raw = OpenSimplex2S.noise2(seed,realX/size, realZ/size);
                double preScl = ((OpenSimplex2S.noise2(seed,realX/size, realZ/size)+1d)*0.5)* MathUtils.clamp(raw, 0d, 1d);
                double scl = preScl*scale;
                for (int y = 0; y < 96; y++) {
                    //double noise3d = scl < 0.01 ? 0d : fast.GetPerlin(realX/26f, y/18f, realZ/26f);
                    double noise3d = scl < 0.01 ? 0d : OpenSimplex2S.noise3_ImproveXZ(seed,realX/32d, y/24d, realZ/32d);
                    noise3d *= scl;
                    noise3d += lump+height+(scl*heightScl)+(MathUtils.clamp(raw, -1d, 0d)*depth);
                    if (noise3d < y) continue;
                    final int yLevel = y >>>4;
                    final Chunk chunk = column.chunks[yLevel];
                    column.chunks[yLevel].setBlockAt(x&15,y&15,z&15,Blocks.STONE);
                    chunk.generated = true;
                    if (yLevel <= noise3d && y >= noise3d-1){
                        column.chunks[yLevel].setBlockAt(x&15,y&15,z&15,Blocks.GRASS);;
                    }
                }
            }
        }
        ChunkPos columPos = new ChunkPos(column.xC,0,column.zC);
        chunkManager.CHUNK_COLUMNS.put(columPos,column);
    }

    public void generateTerrainFeatures(ChunkColumn column){
        for (int x = 0; x < Chunk.CHUNK_SIZE;x++){
            for (int y = 0; y < seaLevel; y++) {

                for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                    int yLevel = y>>>4;
                    final Chunk chunk = column.getChunk(yLevel);
                    if (chunk == null) continue;
                    byte blockId =  chunk.getBlockAt(x&15,y&15,z&15);
                    if (y <= seaLevel && blockId == Blocks.GRASS){
                        chunk.setBlockAt(x&15,y&15,z&15,Blocks.SAND);
                        chunk.setBlockAt(x&15,y+1&15,z&15,Blocks.SAND);
                    }
                    if (y <= seaLevel && blockId == Blocks.AIR){
                        chunk.setBlockAt(x&15,y&15,z&15,Blocks.WATER);
                    }
                }
            }
        }
    }

    private void caveNoise(ChunkColumn column){
        final int xOffset = column.xC *Chunk.CHUNK_SIZE;
        final int zOffset = column.zC *Chunk.CHUNK_SIZE;
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            int realX = x+xOffset;
            for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                int realZ = z+zOffset;
                for (int y = 150; y > 2; y--) {
                    final int yLevel = y >>>4;
                    double noise_3d = OpenSimplex2S.noise3_ImproveXZ(seed,realX/32d, y/24d, realZ/32d);
                    if (noise_3d > -0.54) continue;
                    Chunk chunk = column.chunks[yLevel];
                    if (chunk.getBlockAt(x,y&15,z) == Blocks.WATER) continue;
                    if (chunk.getBlockAt(x,y&15,z) == Blocks.BEDROCK) continue;
                    if (chunk.getBlockAt(x,y&15,z) == Blocks.AIR) continue;
                    chunk.setBlockAt(x,y&15,z,Blocks.CAVE_AIR);
                }
            }
        }
    }
}
