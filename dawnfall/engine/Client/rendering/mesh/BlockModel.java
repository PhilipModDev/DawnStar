package com.dawnfall.engine.Client.rendering.mesh;

public class BlockModel extends Model {
    public BlockModel() {
        this.v1 = new VertexInfo();
        this.v2 = new VertexInfo();
        this.v3 = new VertexInfo();
        this.v4 = new VertexInfo();
        v1.uv.set(0f, 1f);
        v2.uv.set(1f, 1f);
        v3.uv.set(1f, 0f);
        v4.uv.set(0f, 0f);
    }


//    public VoxelFace up;
//    public VoxelFace down;
//    public VoxelFace left;
//    public VoxelFace right;
//    public VoxelFace top;
//    public VoxelFace bottom;
//
//    public static class VoxelFace extends Model {
//        public VoxelFace(){
//            this.v1 = new VertexInfo();
//            this.v2 = new VertexInfo();
//            this.v3 = new VertexInfo();
//            this.v4 = new VertexInfo();
//            v1.uv.set(0f, 1f);
//            v2.uv.set(1f, 1f);
//            v3.uv.set(1f, 0f);
//            v4.uv.set(0f, 0f);
//        }
//    }

}
