package com.engine.dawnstar.main.mesh;

import com.engine.dawnstar.DawnStar;
import com.engine.dawnstar.main.ChunkBuilder;
import com.engine.dawnstar.main.blocks.Block;
import com.engine.dawnstar.main.blocks.Blocks;
import com.engine.dawnstar.main.data.Chunk;
import com.engine.dawnstar.main.data.MeshData;
import com.engine.dawnstar.utils.VertexData;
import com.engine.dawnstar.utils.math.Direction;

public class TerrainBuilder extends MeshData {
    private final ChunkBuilder chunkBuilder;
    private final Blocks blocks = DawnStar.getInstance().blocks;
    private final float lightPower = 0.2f;

    public TerrainBuilder(ChunkBuilder chunkBuilder){
        this.chunkBuilder = chunkBuilder;

    }

    public void addFace(Block block,Chunk chunk,int x, int y, int z, int index){
        if (block.getBlockAsset().model instanceof Cube){
            quad(x, y, z, index,block,chunk);
        }
    }

    private void quad(int x, int y, int z,int index, Block block, Chunk chunk){
        Cube.VoxelFace voxelFace = block.getBlockAsset().model.getVoxelFace(x,y,z,index);
        if (index >= 0 && index <= 3){
            mapTextureUV(block.getBlockAsset().side);
        }
        if (index == 4){
            mapTextureUV(block.getBlockAsset().top);
        }
        if (index == 5){
            mapTextureUV(block.getBlockAsset().bottom);
        }
        quadLighting(x, y, z, index,block,chunk);
        addVertex(voxelFace.vertex1);
        addVertex(voxelFace.vertex2);
        addVertex(voxelFace.vertex3);
        addVertex(voxelFace.vertex4);
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

    private void quadLighting(int x, int y, int z,int index,Block block,Chunk chunk){
        Cube.VoxelFace voxelFace = block.getBlockAsset().model.getVoxelFace(x,y,z,index);
        voxelFace.vertex1.light = 1f;
        voxelFace.vertex2.light = 1f;
        voxelFace.vertex3.light = 1f;
        voxelFace.vertex4.light = 1f;
        if (index == Direction.SOUTH.value){

            if (getBlock(x, y + 1, z - 1, chunk)){
                voxelFace.vertex2.light = 0.5f; // Top right
                voxelFace.vertex3.light = 0.5f; // Top left
            }
            if (getBlock(x, y - 1, z - 1, chunk)){
                voxelFace.vertex1.light = 0.5f; // Bottom right
                voxelFace.vertex4.light = 0.5f; // Bottom left
            }
            if (getBlock(x + 1, y + 1, z - 1, chunk)){
                voxelFace.vertex3.light -= lightPower; // Top left
            }
            if (getBlock(x - 1, y + 1, z - 1, chunk)){
                voxelFace.vertex2.light -= lightPower; // Top right
            }
            if (getBlock(x + 1, y - 1, z - 1, chunk)){
                voxelFace.vertex4.light -= lightPower; // Bottom left
            }
            if (getBlock(x - 1, y - 1, z - 1, chunk)){
                voxelFace.vertex1.light -= lightPower; // Bottom right
            }
        }
        if (index == Direction.NORTH.value){

            if (getBlock(x , y - 1, z + 1, chunk)){
                voxelFace.vertex3.light = 0.5f; // Bottom left
                voxelFace.vertex4.light = 0.5f; // Bottom right
            }
            if (getBlock(x , y + 1, z + 1, chunk)){
                voxelFace.vertex1.light = 0.5f; // Top right
                voxelFace.vertex2.light = 0.5f; // Top left
            }
            if (getBlock(x - 1, y + 1, z + 1, chunk)){
                voxelFace.vertex2.light -= lightPower; // Top left
            }
            if (getBlock(x + 1, y + 1, z + 1, chunk)){
                voxelFace.vertex1.light -= lightPower; // Top right
            }
            if (getBlock(x - 1, y - 1, z + 1, chunk)){
                voxelFace.vertex3.light -= lightPower; // Bottom left
            }
            if (getBlock(x + 1, y - 1, z + 1, chunk)){
                voxelFace.vertex4.light -= lightPower; // Bottom right
            }
        }
        if (index == Direction.WEST.value){

            if (getBlock(x - 1, y - 1, z, chunk)){
                voxelFace.vertex1.light = 0.5f; // Bottom left
                voxelFace.vertex2.light = 0.5f; // Bottom right
            }
            if (getBlock(x - 1, y + 1, z, chunk)){
                voxelFace.vertex3.light = 0.5f; // Top right
                voxelFace.vertex4.light = 0.5f; // Top left
            }

            if (getBlock(x - 1, y - 1, z - 1, chunk)){
                voxelFace.vertex1.light -= lightPower; // Bottom left
            }
            if (getBlock(x - 1, y - 1, z + 1, chunk)){
                voxelFace.vertex2.light -= lightPower; // Bottom right
            }
            if (getBlock(x - 1, y + 1, z - 1, chunk)){
                voxelFace.vertex4.light -= lightPower; // Top left
            }
            if (getBlock(x - 1, y + 1, z + 1, chunk)){
                voxelFace.vertex3.light -= lightPower; // Top right
            }
        }
        if (index == Direction.EAST.value){

            if (getBlock(x + 1, y + 1, z, chunk)){
                voxelFace.vertex1.light = 0.5f;// right
                voxelFace.vertex2.light = 0.5f; // left
            }
            if (getBlock(x + 1, y - 1, z, chunk)){
                voxelFace.vertex3.light = 0.5f; // left
                voxelFace.vertex4.light = 0.5f; // right
            }
            if (getBlock(x + 1, y + 1, z + 1, chunk)){
                voxelFace.vertex2.light -= lightPower; // left
            }
            if (getBlock(x + 1, y + 1, z - 1, chunk)){
                voxelFace.vertex1.light -= lightPower; // right
            }
            if (getBlock(x + 1, y - 1, z - 1, chunk)){
                voxelFace.vertex4.light -= lightPower; // right
            }
            if (getBlock(x + 1, y - 1, z + 1, chunk)){
                voxelFace.vertex3.light -= lightPower; // left
            }
        }
        if (index == Direction.UP.value){

            if (getBlock(x, y + 1, z - 1, chunk)){
                voxelFace.vertex1.light = 0.5f;//left
                voxelFace.vertex4.light = 0.5f;//right
            }
            if (getBlock(x, y + 1, z + 1, chunk)){
                voxelFace.vertex3.light = 0.5f; // left
                voxelFace.vertex2.light = 0.5f; // right
            }
            if (getBlock(x - 1, y + 1 , z - 1, chunk)){
                voxelFace.vertex1.light -= lightPower;//left
            }
            if (getBlock(x + 1, y + 1 , z - 1, chunk)){
                voxelFace.vertex4.light -= lightPower;//left
            }
            if (getBlock(x - 1, y + 1 , z + 1, chunk)){
                voxelFace.vertex2.light -= lightPower; // right
            }
            if (getBlock(x + 1, y + 1 , z + 1, chunk)){
                voxelFace.vertex3.light -= lightPower; // left
            }

        }
        if (index == Direction.DOWN.value){
            //East
            if (getBlock(x + 1, y - 1 , z, chunk)) {
                voxelFace.vertex1.light = 0.5f; //left
                voxelFace.vertex2.light = 0.5f; //right
            }
            //West
            if (getBlock(x - 1, y - 1 , z, chunk)) {
                voxelFace.vertex3.light = 0.5f;//right
                voxelFace.vertex4.light = 0.5f;//left
            }

            if (getBlock(x - 1, y - 1 , z - 1, chunk)) {
                voxelFace.vertex4.light -= lightPower;//left
            }
            if (getBlock(x - 1, y - 1 , z + 1, chunk)) {
                voxelFace.vertex3.light -= lightPower;//right
            }
            if (getBlock(x - 1, y - 1 , z - 1, chunk)) {
                voxelFace.vertex1.light -= lightPower;//left
            }
            if (getBlock(x - 1, y - 1 , z + 1, chunk)) {
                voxelFace.vertex2.light -= lightPower;//right
            }
        }
    }

    private boolean getBlock(int x,int y, int z, Chunk chunk){
        return blocks.getBlockById(chunk.getBlock(x , y , z )).getBlockState().isSolid;
    }

    public ChunkMesh create(Chunk chunk) {
        if (vertices.isEmpty())  return null;
        ChunkMesh chunkMesh = new ChunkMesh(vertices,chunkBuilder.indicesData, chunk);
        vertices.clear();
        return chunkMesh;
    }
}
