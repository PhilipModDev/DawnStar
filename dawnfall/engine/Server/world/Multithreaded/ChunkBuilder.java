package com.dawnfall.engine.Server.world.Multithreaded;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.dawnfall.engine.Client.rendering.mesh.ChunkMesh;
import com.dawnfall.engine.Client.rendering.mesh.ChunkMeshBuilder;
import com.dawnfall.engine.Client.rendering.mesh.VolatileMesh;
import com.dawnfall.engine.Client.rendering.textureLoaders.GameAssets;
import com.dawnfall.engine.DawnStar;
import com.dawnfall.engine.Server.world.World;
import com.dawnfall.engine.Server.world.chunkUtil.Chunk;
import com.dawnfall.engine.Server.world.Blocks.Block;
import com.dawnfall.engine.Server.world.Blocks.Blocks;
import com.dawnfall.engine.Server.world.chunkUtil.VisibleGrid;

import static com.dawnfall.engine.Server.world.Blocks.Blocks.*;

public class ChunkBuilder {
    public final Material material;
    private final World world;
    private final ChunkMeshBuilder faceBuilder;
    public ChunkBuilder(World world){
        this.world = world;
        GameAssets.TextureHolder textureHolder = DawnStar.getInstance().getTextureHolder();
        material = new Material(new TextureAttribute(TextureAttribute.createDiffuse(textureHolder.getRegion())));
        faceBuilder = new ChunkMeshBuilder();
    }
    public synchronized VolatileMesh buildVolatileMesh(Chunk chunk) {
        final Chunk tempNorth = world.getChunkAt(chunk.x, chunk.y,chunk.z+1);
        final Chunk tempSouth = world.getChunkAt(chunk.x, chunk.y,chunk.z-1);
        final Chunk tempEast = world.getChunkAt(chunk.x+1,chunk.y, chunk.z);
        final Chunk tempWest = world.getChunkAt(chunk.x-1,chunk.y, chunk.z);
        if (tempWest == null || tempEast == null || tempNorth == null || tempSouth == null)  {
            chunk.safe = false;
            chunk.setChunkVisible(false);
            return null;
        }
        VisibleGrid visibleGrid = new VisibleGrid(this.world.chunkManager);
        final int size = Chunk.CHUNK_SIZE;
        final int maskSize = size-1;
        final int chunkX = chunk.x*Chunk.CHUNK_SIZE;
        final int chunkY = chunk.y*Chunk.CHUNK_SIZE;
        final int chunkZ = chunk.z*Chunk.CHUNK_SIZE;
        for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
                for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                    final byte id = chunk.getBlockAt(x,y,z);
                    if (id == Blocks.AIR || id == DEFAULT || id == CAVE_AIR) {
                     chunk.isSolid = false;
                     continue;
                    }

                    final Block block = Blocks.blocks[id];
                    if (block.isSolid) visibleGrid.setSolidBlock(x,y,z);
                    // check south Z-
                    if (z-1 == -1) {
                        if (canAddFace(block, tempSouth.getBlockAt(x,y,z + maskSize))) {
                            faceBuilder.buildSouth(block,chunk,x+chunkX,y+chunkY,z+chunkZ);
                        }
                    }  else {
                        if (canAddFace(block, chunk.getBlockAt(x,y,z-1))) {
                            faceBuilder.buildSouth(block,chunk,x+chunkX,y+chunkY,z+chunkZ);
                        }
                    }
                    // check north Z+
                    if (z+1 == size) {
                        if (canAddFace(block, tempNorth.getBlockAt(x,y, 0))) {
                            faceBuilder.buildNorth(block, chunk, x + chunkX, y + chunkY, z + chunkZ);
                        }
                    } else if (canAddFace(block, chunk.getBlockAt(x,y,z+1))) {
                        faceBuilder.buildNorth(block, chunk, x + chunkX, y + chunkY, z + chunkZ);
                    }
                    // check west X-
                    if (x-1 == -1) {
                        if (canAddFace(block, tempWest.getBlockAt(x + maskSize,y,z))) {
                            faceBuilder.buildWest(block, chunk, x + chunkX, y + chunkY, z + chunkZ);
                        }
                    } else if (canAddFace(block, chunk.getBlockAt(x-1,y,z))) {
                        faceBuilder.buildWest(block, chunk, x + chunkX, y + chunkY, z + chunkZ);
                    }
                    // check east X+
                    if (x+1 == size) {
                        if (canAddFace(block, tempEast.getBlockAt(0,y,z))) {
                            faceBuilder.buildEast(block, chunk, x + chunkX, y + chunkY, z + chunkZ);
                        }
                    } else if (canAddFace(block, chunk.getBlockAt(x+1,y,z))) {
                        faceBuilder.buildEast(block, chunk, x + chunkX, y + chunkY, z + chunkZ);
                    }
                    // check up Y+
                    if (y+1 == size) {
                        Chunk chunk1 = world.getChunkAt(chunk.x,chunk.y+1,chunk.z);
                        if (chunk1 != null) {
                            if (canAddFace(block, chunk1.getBlockAt(x, 0,z))) {
                                faceBuilder.buildTop(block, chunk, x + chunkX, y + chunkY, z + chunkZ);
                            }
                        }
                    } else if (canAddFace(block, chunk.getBlockAt(x,y+1,z))) {
                            faceBuilder.buildTop(block,chunk,x+chunkX,y+chunkY,z+chunkZ);
                    }
                    // check down Y-
                    if (y-1 == -1) {
                        Chunk chunk1 = world.getChunkAt(chunk.x,chunk.y-1,chunk.z);
                        if (chunk1 != null) {
                            if (canAddFace(block, chunk1.getBlockAt(x,y + maskSize,z))) {
                                faceBuilder.buildDown(block,chunk,x+chunkX,y+chunkY,z+chunkZ);
                            }
                        }
                    } else if (canAddFace(block, chunk.getBlockAt(x,y-1,z))) {
                            faceBuilder.buildDown(block,chunk,x+chunkX,y+chunkY,z+chunkZ);
                    }
                }
            }
        }
        chunk.visibilityInfo = visibleGrid.floodFillChunk();
        chunk.meshBuilt = true;
        ChunkMesh mesh = faceBuilder.create(chunk);
        return new VolatileMesh(mesh, chunk);
    }
    public boolean canAddFace(Block block, int id) {
        if (id == Blocks.AIR || id ==  DEFAULT || id == CAVE_AIR) return true;
        final Block secondary = blocks[id];
        if (block.isSolid == secondary.isSolid)
            return false;
        return block.isSolid; // primary is solid and secondary is trans.
    }
}
