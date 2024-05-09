package com.engine.dawnstar.main.mesh;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.engine.dawnstar.main.ChunkBuilder;
import com.engine.dawnstar.main.blocks.Block;
import com.engine.dawnstar.main.data.Chunk;
import com.engine.dawnstar.main.data.MeshData;
import com.engine.dawnstar.utils.VertexData;
import com.engine.dawnstar.utils.math.Direction;

public class TerrainBuilder extends MeshData {
    private final ChunkBuilder chunkBuilder;
    private final VertexData vertex1 = new VertexData();
    private final VertexData vertex2 = new VertexData();
    private final VertexData vertex3 = new VertexData();
    private final VertexData vertex4 = new VertexData();

    public TerrainBuilder(ChunkBuilder chunkBuilder){
        this.chunkBuilder = chunkBuilder;

        vertex1.uv.set(0,0);
        vertex2.uv.set(0,1);
        vertex3.uv.set(1,1);
        vertex4.uv.set(1,0);
        vertex1.light = 1f;
        vertex2.light = 0.5f;
        vertex3.light = 1f;
        vertex4.light = 0.5f;
    }


    public void addFace(Block block,int x, int y, int z, int index){

        if (index == Direction.SOUTH.value){
            vertex1.pos.set(x,y,z);
            vertex2.pos.set(x,y + 1,z);
            vertex3.pos.set(x + 1,y + 1,z);
            vertex4.pos.set(x + 1,y ,z);
        }
        if (index == Direction.NORTH.value){
            vertex1.pos.set(x + 1,y + 1,z + 1);
            vertex2.pos.set(x,y + 1,z + 1);
            vertex3.pos.set(x ,y ,z + 1);
            vertex4.pos.set(x + 1,y ,z + 1);
        }
        if (index == Direction.WEST.value){
            vertex1.pos.set(x ,y ,z);
            vertex2.pos.set(x ,y ,z + 1);
            vertex3.pos.set(x  ,y + 1,z + 1);
            vertex4.pos.set(x ,y + 1 ,z );
        }
        if (index == Direction.EAST.value){
            vertex1.pos.set(x + 1  ,y +1 ,z );
            vertex2.pos.set(x + 1 ,y + 1 ,z + 1);
            vertex3.pos.set(x + 1  ,y ,z +1 );
            vertex4.pos.set(x + 1 ,y  ,z );
        }
        if (index == Direction.UP.value){
            vertex1.pos.set(x,y + 1,z );
            vertex2.pos.set(x,y+ 1,z + 1);
            vertex3.pos.set(x + 1 ,y+ 1,z +1 );
            vertex4.pos.set(x + 1 ,y+ 1,z );
        }
        if (index == Direction.DOWN.value){
            vertex1.pos.set(x + 1,y ,z );
            vertex2.pos.set(x + 1,y,z + 1);
            vertex3.pos.set(x,y,z +1);
            vertex4.pos.set(x,y,z );
        }
        rect(block.getBlockAsset().top);
    }
    public void addFace(Block block,int x, int y, int z,int width, int height, int index){

        if (index == Direction.SOUTH.value){
            //Set vertices.
            vertex1.pos.set(x,y,z);
            //Expand vertices.
            vertex2.pos.set(x,y + 1 + height,z);
            vertex3.pos.set(x + 1 + width,y + 1 + height,z);
            vertex4.pos.set(x + 1 + width,y ,z);
        }
        if (index == Direction.NORTH.value){
            //Expand vertices.
            vertex1.pos.set(x + 1 + width,y + 1 + height,z + 1);
            vertex2.pos.set(x,y + 1 + height,z + 1);
            //Set vertices.
            vertex3.pos.set(x ,y ,z + 1);
            //Expand vertices.
            vertex4.pos.set(x + 1 + width,y ,z + 1);
        }
        if (index == Direction.WEST.value){
            //Set vertices.
            vertex1.pos.set(x ,y ,z);
            //Expand vertices.
            vertex2.pos.set(x ,y ,z + 1 + width);
            vertex3.pos.set(x  ,y + 1 + height,z + 1 + width);
            vertex4.pos.set(x ,y + 1 + height ,z );
        }
        if (index == Direction.EAST.value){
            //Expand vertices.
            vertex1.pos.set(x + 1,y,z);
            vertex2.pos.set(x + 1,y + 1 + height,z);
            vertex3.pos.set(x+1,y+1 + height,z+1 + width);
            vertex4.pos.set(x+1,y,z + 1 + width);
        }
        if (index == Direction.UP.value){
            //Set vertices.
            vertex1.pos.set(x,y + 1,z);
            vertex2.pos.set(x,y+1,z + 1 + width);
            vertex3.pos.set(x + 1 + height,y+1,z + 1 + width);
            vertex4.pos.set(x+1+ height,y+1,z);
        }
        if (index == Direction.DOWN.value){
            vertex1.pos.set(x,y ,z);
            vertex2.pos.set(x + 1 + width,y,z);
            vertex3.pos.set(x + 1 + width,y,z + 1 + height);
            vertex4.pos.set(x,y,z + 1 + height);
        }
        rect(block.getBlockAsset().top);
    }

    public void rect(TextureRegion region){
        mapTextureUV(region);
        addVertex(vertex1);
        addVertex(vertex2);
        addVertex(vertex3);
        addVertex(vertex4);
    }

    public void addVertex(VertexData data){
        int x = (int) data.pos.x;
        int y = (int) data.pos.y;
        int z = (int) data.pos.z;
        //Implement the masking for the rendering.
        int mask = (x & ChunkBuilder.SQUARED_CHUNK) | (y & ChunkBuilder.SQUARED_CHUNK) << 10 | (z & ChunkBuilder.SQUARED_CHUNK) << 20;
        vertices.add(Float.intBitsToFloat(mask));
        vertices.add(offsetU+scaleU*data.uv.x,offsetV+scaleV*data.uv.y);
        vertices.add(data.light);
    }

    public ChunkMesh create(Chunk chunk) {
        if (vertices.isEmpty())  return null;
        ChunkMesh chunkMesh = new ChunkMesh(vertices,chunkBuilder.indicesData, chunk);
        vertices.clear();
        return chunkMesh;
    }
}
