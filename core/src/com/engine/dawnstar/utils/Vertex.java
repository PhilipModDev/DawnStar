package com.engine.dawnstar.utils;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Vertex {
    public static final VertexAttributes ATTRIBUTES = new VertexAttributes(
            new VertexAttribute(VertexAttributes.Usage.Position,1,"data"),
            new VertexAttribute(VertexAttributes.Usage.TextureCoordinates,2,ShaderProgram.TEXCOORD_ATTRIBUTE),
            new VertexAttribute(VertexAttributes.Usage.Normal,1,ShaderProgram.NORMAL_ATTRIBUTE)
    );
}
