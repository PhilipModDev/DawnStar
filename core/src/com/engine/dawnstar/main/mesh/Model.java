package com.engine.dawnstar.main.mesh;

import com.engine.dawnstar.utils.VertexData;

public abstract class Model {
    public abstract VertexData getVertex(int index);

    public Cube.VoxelFace getVoxelFace(int x, int y, int z, int faceIndex) {
        return null;
    }
}
