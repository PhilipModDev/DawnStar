package com.engine.dawnstar.main.data;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.FloatArray;

public abstract class MeshData {
    public FloatArray vertices = new FloatArray();
    public float scaleU;
    public float scaleV;
    public float offsetU;
    public float offsetV;

    public void setUVRange(float u,float v,float u2,float v2){
        offsetU = u;
        offsetV = v;
        scaleU = u2 - u;
        scaleV = v2 - v;
    }
    //Sets the UV
    public void mapTextureUV(TextureRegion region){
        if (region == null) throw new RuntimeException("Texture for voxel data is null.");
        setUVRange(region.getU(), region.getV(), region.getU2(),region.getV2());
    }
}
