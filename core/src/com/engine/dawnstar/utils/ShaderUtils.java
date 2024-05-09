package com.engine.dawnstar.utils;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

public class ShaderUtils implements Disposable {

    private final ShaderProgram voxelTerrain;
    private final CameraUtils cameraUtils;

    public ShaderUtils(CameraUtils cameraUtils){
        FileHandle vertex = Gdx.files.internal("shaders/vertex.glsl");
        FileHandle fragment = Gdx.files.internal("shaders/fragment.glsl");
        voxelTerrain = new ShaderProgram(vertex,fragment);
        if (!voxelTerrain.isCompiled()) throw new RuntimeException("Shaders could not complete during load.");
        this.cameraUtils = cameraUtils;
    }
    //Set up the basic rendering matrix.
    public void bindTerrain(){
        voxelTerrain.bind();
        voxelTerrain.setUniformMatrix("combined",cameraUtils.getCamera3D().combined);
    }

    public ShaderProgram getVoxelTerrain() {
        return voxelTerrain;
    }

    @Override
    public void dispose() {
        voxelTerrain.dispose();
    }
}
