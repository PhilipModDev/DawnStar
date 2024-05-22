package com.engine.dawnstar.main.mesh;

import com.engine.dawnstar.utils.VertexData;

public class Cube extends Model {

    private final VoxelFace cubuFace = new VoxelFace();

    public Cube(){
        cubuFace.vertex1.uv.set(0,0);
        cubuFace.vertex2.uv.set(0,1);
        cubuFace.vertex3.uv.set(1,1);
        cubuFace.vertex4.uv.set(1,0);
    }

    public VoxelFace getVoxelFace(int x,int y, int z,int faceIndex){
        if (faceIndex > 5 || faceIndex < 0) throw new IndexOutOfBoundsException("Can't get voxel face.");
        //North.
        if (faceIndex == 0) {
            cubuFace.vertex1.pos.set(x + 1,y + 1,z + 1);
            cubuFace.vertex2.pos.set(x,y + 1,z + 1);
            cubuFace.vertex3.pos.set(x ,y ,z + 1);
            cubuFace.vertex4.pos.set(x + 1,y ,z + 1);
        }
        //South.
        if (faceIndex == 1) {
            cubuFace.vertex1.pos.set(x,y,z);
            cubuFace.vertex2.pos.set(x ,y + 1 ,z);
            cubuFace.vertex3.pos.set(x + 1 ,y + 1 ,z);
            cubuFace.vertex4.pos.set(x + 1 ,y  ,z);
        }
        //East.
        if (faceIndex == 2){
            cubuFace.vertex1.pos.set(x + 1  ,y +1 ,z );
            cubuFace.vertex2.pos.set(x + 1 ,y + 1 ,z + 1);
            cubuFace.vertex3.pos.set(x + 1  ,y ,z +1 );
            cubuFace.vertex4.pos.set(x + 1 ,y  ,z );
        }
        //West.
        if (faceIndex == 3){
            cubuFace.vertex1.pos.set(x ,y ,z);
            cubuFace.vertex2.pos.set(x ,y ,z + 1);
            cubuFace.vertex3.pos.set(x  ,y + 1,z + 1);
            cubuFace.vertex4.pos.set(x ,y + 1 ,z );
        }
        //Up.
        if (faceIndex == 4){
            cubuFace.vertex1.pos.set(x,y + 1,z );
            cubuFace.vertex2.pos.set(x,y+ 1,z + 1);
            cubuFace.vertex3.pos.set(x + 1 ,y+ 1,z +1 );
            cubuFace.vertex4.pos.set(x + 1 ,y+ 1,z );
        }
        //Down.
        if (faceIndex == 5){
            cubuFace.vertex1.pos.set(x + 1,y ,z );
            cubuFace.vertex2.pos.set(x + 1,y,z + 1);
            cubuFace.vertex3.pos.set(x,y,z +1);
            cubuFace.vertex4.pos.set(x,y,z );
        }
        return cubuFace;
    }

    @Override
    public VertexData getVertex(int index) {
        if (index > 3 || index < 0) throw new IndexOutOfBoundsException("Can't get vertex data.");
        return switch (index) {
            case 0 -> cubuFace.vertex1;
            case 1 -> cubuFace.vertex2;
            case 2 -> cubuFace.vertex3;
            default -> cubuFace.vertex4;
        };
    }

    public final static class VoxelFace {
        public final VertexData vertex1 = new VertexData();
        public final VertexData vertex2 = new VertexData();
        public final VertexData vertex3 = new VertexData();
        public final VertexData vertex4 = new VertexData();
    }
}
