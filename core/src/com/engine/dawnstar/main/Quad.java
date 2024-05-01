package com.engine.dawnstar.main;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;
import com.engine.dawnstar.DawnStar;
import com.engine.dawnstar.utils.VertexBufferObject;

import static com.badlogic.gdx.Gdx.gl32;

public class Quad implements Disposable {

    public final Vector3 position = new Vector3();
    private final TextureAtlas.AtlasRegion region;
    private final VertexBufferObject vertexBufferObject;

    public Quad(TextureAtlas.AtlasRegion region,int x, int y, int z){
        this.region  = region;
        position.set(x,y,z);
        FloatArray array = new FloatArray();
        array.addAll(getVertices());
        vertexBufferObject = new VertexBufferObject(array);
    }

    public void render(Camera camera){
        vertexBufferObject.bind(camera, DawnStar.getInstance().indexData);

        gl32.glEnable(GL32.GL_DEPTH_TEST);
        gl32.glDepthFunc(GL32.GL_LEQUAL);
        gl32.glEnable(GL32.GL_CULL_FACE);

        gl32.glDrawElements(GL32.GL_TRIANGLES,DawnStar.getInstance().indexData.getNumIndices(),GL32.GL_UNSIGNED_SHORT,0);

        vertexBufferObject.unbind();
    }


   private float[] getVertices(){
        float uScale = region.getU2() - region.getU();
        float vScale = region.getV2() - region.getV();
        float uOffset = region.getU();
        float vOffset = region.getV();
        return new float[]{
          0 + position.x,1 + position.y,0 + position.z, uOffset+uScale*0,vOffset+vScale*0, 1,
          0 + position.x,0 + position.y,0 + position.z, uOffset+uScale*0,vOffset+vScale*1, 1,
          1 + position.x,0 + position.y,0 + position.z, uOffset+uScale*1,vOffset+vScale*1, 1,
          1 + position.x,1 + position.y,0 + position.z, uOffset+uScale*1,vOffset+vScale*0, 1,
        };
   }



    @Override
    public void dispose() {
       vertexBufferObject.dispose();
    }
}
