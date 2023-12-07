package com.dawnfall.engine.Client.rendering.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.dawnfall.engine.Server.util.Utils;

import static com.badlogic.gdx.graphics.glutils.ShaderProgram.POSITION_ATTRIBUTE;
import static com.badlogic.gdx.graphics.glutils.ShaderProgram.TEXCOORD_ATTRIBUTE;

public class VoxelShaderManager {
    /** 3 Position, 1 Data and 2 TextureCoordinates [v,y,z,d,u,v] */
    public final VertexAttributes attributes = new VertexAttributes (
            new VertexAttribute(VertexAttributes.Usage.Position, 3, POSITION_ATTRIBUTE),//0.
            new VertexAttribute(VertexAttributes.Usage.Normal, 1, "a_light"),//1.
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, TEXCOORD_ATTRIBUTE)//2.
    );
    /** 24 bytes in a single vertex with 6 float components. */
    public final int byteSize = attributes.vertexSize;
    /** 6 floats in a single vertex. */
    public final String projTrans = "u_projTrans";
    public int[] locations;
    public ShaderProgram terrainShader;
    public ShaderProgram plantShader;
    public ShaderProgram waterShader;
    private boolean errorFound;
    public VoxelShaderManager(){
        try {
            terrainShader = new ShaderProgram(
                    Utils.getFile("shaders/terrainVertex.glsl"),
                    Utils.getFile("shaders/terrainFragment.glsl")
            );
        } catch (Exception exception) {
            exception.printStackTrace();
            errorFound = loadFromFiles();
        }
        String shaderLog = "Shader log: \n";
        if (!terrainShader.isCompiled()) {
            errorFound = true;
            shaderLog += terrainShader.getLog() + "\n";
        }
        if (errorFound) {
            System.out.println(shaderLog);
        } else {
            locations = locateAttributes(terrainShader,attributes);
        }
    }

    public void bindTerrain(Matrix4 combine) {
        terrainShader.bind();
        terrainShader.setUniformMatrix(projTrans, combine);
    }

    private float wavePlant;
    public void bindPlant(Matrix4 combine) {
        wavePlant += 0.04f; // 0.06f
        if (wavePlant > MathUtils.PI2) {
            wavePlant -= MathUtils.PI2;
        }
        plantShader.bind();
        plantShader.setUniformMatrix(projTrans, combine);
        plantShader.setUniformf("u_wave", wavePlant);
    }

    public void bindWater(Matrix4 combine) {
        plantShader.bind();
        plantShader.setUniformMatrix(projTrans, combine);
    }
    public ShaderProgram getShader(String path) {
        return new ShaderProgram(
                Gdx.files.internal(path + ".vert"),
                Gdx.files.internal(path + ".frag")
        );
    }
    public int[] locateAttributes(final ShaderProgram shader, final VertexAttributes attributes) {
        final int s = attributes.size();
        final int[] locations = new int[s];
        for (int i = 0; i < s; i++) {
            final VertexAttribute attribute = attributes.get(i);
            locations[i] = shader.getAttributeLocation(attribute.alias);
        }
        return locations;
    }

    public void getSource(ShaderProgram shader) {
        System.out.println("Vertex:\n" + shader.getVertexShaderSource() + "\nFragment:\n" + shader.getFragmentShaderSource());
    }
    public boolean isErrorFound() {
        return errorFound;
    }
    public FileHandle getTerrainFragmentShader(){
        FileHandle fileHandle = Gdx.files.local("shaders/terrain.frag");
        if (fileHandle.exists()){
            //Loads the files.
            return fileHandle;
        }else {
            //Generate the files and load again.
            final String fragment =
                    "#ifdef GL_ES\n" +
                            "precision mediump float;\n" +
                            "#endif\n" +
                            "\n" +
                            "varying float v_light;\n" +
                            "varying vec2 v_texCoords;\n" +
                            "\n" +
                            "uniform sampler2D u_texture;\n" +
                            "\n" +
                            "void dawnStar() {\n" +
                            "\tvec4 pixel = texture2D(u_texture, v_texCoords);\n" +
                            "\tif (pixel.a <= 0.0) discard;\n" +
                            "\tgl_FragColor = pixel * v_light;\n" +
                            "}";
            fileHandle.writeString(fragment,false);
            return getTerrainFragmentShader();
        }
    }
    public FileHandle getTerrainVertexShader(){
        FileHandle fileHandle = Gdx.files.local("shaders/terrain.vert");
        if (fileHandle.exists()){
            //Loads the files.
            return fileHandle;
        }else {
            //Generate the files and load again.
            final String vertex = "" +
                    "attribute vec4 a_position;\n" +
                    "attribute float a_light;\n" +
                    "attribute vec2 a_texCoord;\n" +
                    "\n" +
                    "uniform mat4 u_projTrans;\n" +
                    "\n" +
                    "varying float v_light;\n" +
                    "varying vec2 v_texCoords;\n" +
                    "\n" +
                    "void dawnStar() {\n" +
                    "\tv_light = a_light;\n" +
                    "\tv_texCoords = a_texCoord;\n" +
                    "\tgl_Position = u_projTrans * a_position;\n" +
                    "}";
            fileHandle.writeString(vertex,false);
            return getTerrainVertexShader();
        }
    }
    private boolean loadFromFiles(){
        terrainShader = new ShaderProgram(getTerrainVertexShader(),getTerrainFragmentShader());
        String log = "Shader log: \n";
        boolean error = false;
        if (!terrainShader.isCompiled()) {
            error = true;
            log += terrainShader.getLog() + "\n";
        }
        if (error) {
            System.out.println(log);
        } else {
            locations = locateAttributes(terrainShader,attributes);
        }
        return !error;
    }
}
