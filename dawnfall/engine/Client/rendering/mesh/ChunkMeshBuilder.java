package com.dawnfall.engine.Client.rendering.mesh;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.FloatArray;
import com.dawnfall.engine.DawnStar;
import com.dawnfall.engine.Server.util.glutils.VertContext;
import com.dawnfall.engine.Server.world.Blocks.Block;
import com.dawnfall.engine.Server.world.chunkUtil.Chunk;

import static com.dawnfall.engine.Server.world.Blocks.Blocks.blocks;

public class ChunkMeshBuilder {
    private final DawnStar main = DawnStar.getInstance();
    protected final FloatArray vertices = new FloatArray(64);
    public boolean isBuilding;
    protected float uOffset = 0f, vOffset = 0f, uScale = 1f, vScale = 1f;
    private final VertContext context = new VertContext() {
        public ShaderProgram getShader() {
            return main.voxelShaderManager.terrainShader;
        }
        public VertexAttributes getAttrs() {
            return main.voxelShaderManager.attributes;
        }
        public int getLocation(int i) {
            //returns an attributes from the list.
            return main.voxelShaderManager.locations[i];
        }
    };

    public static float  lightHigh = 1.0f; // old: 1.0f  new: 1.0f
    public static float lightMed = 0.86f;// old: 0.82f new: 0.86f
    public static float lightLow = 0.75f; // old: 0.68f new: 0.75f
    public static float lightDim = 0.69f; // old: 0.6f  new: 0.65f
    public static float power = 0.55f; // 0.75f
    public ChunkMeshBuilder(){
    
    }
    public void buildNorth(Block block, Chunk chunk, int x, int y, int z) {
        ++z;
        BlockModel model = block.getModel();
        model.v1.pos.set(x, y, z);
        model.v2.pos.set(x+1, y, z);
        model.v3.pos.set(x+1, y+1, z);
        model.v4.pos.set(x, y+1, z);
        setLight(lightMed,block);
        // lighting
        if (blocks[chunk.getBlockSmart(x+1, y, z)].isSolid) {
            model.v3.light *= power;
            model.v2.light *= power;
        }
        if (blocks[chunk.getBlockSmart(x, y+1, z)].isSolid) {
            model.v3.light *= power;
            model.v4.light *= power;
        }
        if (blocks[chunk.getBlockSmart(x-1, y, z)].isSolid) {
            model.v4.light *= power;
            model.v1.light *= power;
        }
        if (blocks[chunk.getBlockSmart(x, y-1, z)].isSolid) {
            model.v1.light *= power;
            model.v2.light *= power;
        }
        if (model.v3.light == lightMed && blocks[chunk.getBlockSmart(x+1, y+1, z)].isSolid) {
            model.v3.light *= power;
        }
        if (model.v4.light == lightMed && blocks[chunk.getBlockSmart(x-1, y+1, z)].isSolid) {
            model.v4.light *= power;
        }
        if (model.v1.light == lightMed && blocks[chunk.getBlockSmart(x-1, y-1, z)].isSolid) {
            model.v1.light *= power;
        }
        if (model.v2.light == lightMed && blocks[chunk.getBlockSmart(x+1, y-1, z)].isSolid) {
            model.v2.light *= power;
        }
        rect(block.textures.side,block);
    }

    public void buildEast(Block block, Chunk chunk, int x, int y, int z) {
        ++x;
        BlockModel model = block.getModel();
        model.v1.pos.set(x, y, z+1);
        model.v2.pos.set(x, y, z);
        model.v3.pos.set(x, y+1, z);
        model.v4.pos.set(x, y+1, z+1);
        setLight(lightLow,block);
        // lighting
        if (blocks[chunk.getBlockSmart(x, y, z+1)].isSolid) {
            model.v4.light *= power;
            model.v1.light *= power;
        }
        if (blocks[chunk.getBlockSmart(x, y+1, z)].isSolid) {
            model.v3.light *= power;
            model.v4.light *= power;
        }
        if (blocks[chunk.getBlockSmart(x, y, z-1)].isSolid) {
            model.v3.light *= power;
            model.v2.light *= power;
        }
        if (blocks[chunk.getBlockSmart(x, y-1, z)].isSolid) {
            model.v1.light *= power;
            model.v2.light *= power;
        }
        if (model.v3.light == lightLow && blocks[chunk.getBlockSmart(x, y+1, z-1)].isSolid) {
            model.v3.light *= power;
        }
        if (model.v4.light == lightLow && blocks[chunk.getBlockSmart(x, y+1, z+1)].isSolid) {
            model.v4.light *= power;
        }
        if (model.v1.light == lightLow && blocks[chunk.getBlockSmart(x, y-1, z+1)].isSolid) {
            model.v1.light *= power;
        }
        if (model.v2.light == lightLow && blocks[chunk.getBlockSmart(x, y-1, z-1)].isSolid) {
            model.v2.light *= power;
        }
        rect(block.textures.side,block);
    }

    public void buildSouth(Block block, Chunk chunk, int x, int y, int z) {
        BlockModel model = block.getModel();
        model.v1.pos.set(x+1, y, z);
        model.v2.pos.set(x, y, z);
        model.v3.pos.set(x, y+1, z);
        model.v4.pos.set(x+1, y+1, z);
        setLight(lightMed,block);
        // lighting
        --z;
        if (blocks[chunk.getBlockSmart(x+1, y, z)].isSolid) {
            model.v4.light *= power;
            model.v1.light *= power;
        }
        if (blocks[chunk.getBlockSmart(x, y+1, z)].isSolid) {
            model.v3.light *= power;
            model.v4.light *= power;
        }
        if (blocks[chunk.getBlockSmart(x-1, y, z)].isSolid) {
            model.v3.light *= power;
            model.v2.light *= power;
        }
        if (blocks[chunk.getBlockSmart(x, y-1, z)].isSolid) {
            model.v1.light *= power;
            model.v2.light *= power;
        }
        if (model.v3.light == lightMed && blocks[chunk.getBlockSmart(x-1, y+1, z)].isSolid) {
            model.v3.light *= power;
        }
        if (model.v4.light == lightMed && blocks[chunk.getBlockSmart(x+1, y+1, z)].isSolid) {
            model.v4.light *= power;
        }
        if (model.v1.light == lightMed && blocks[chunk.getBlockSmart(x+1, y-1, z)].isSolid) {
            model.v1.light *= power;
        }
        if (model.v2.light == lightMed && blocks[chunk.getBlockSmart(x-1, y-1, z)].isSolid) {
            model.v2.light *= power;
        }
        rect(block.textures.side,block);
    }

    public void buildWest(Block block, Chunk chunk, int x, int y, int z) {
        BlockModel model = block.getModel();
        model.v1.pos.set(x, y, z);
        model.v2.pos.set(x, y, z+1);
        model.v3.pos.set(x, y+1, z+1);
        model.v4.pos.set(x, y+1, z);
        setLight(lightLow,block);
        --x;
        // lighting
        if (blocks[chunk.getBlockSmart(x, y, z+1)].isSolid) {
            model.v3.light *= power;
            model.v2.light *= power;
        }
        if (blocks[chunk.getBlockSmart(x, y+1, z)].isSolid) {
            model.v3.light *= power;
            model.v4.light *= power;
        }
        if (blocks[chunk.getBlockSmart(x, y, z-1)].isSolid) {
            model.v4.light *= power;
            model.v1.light *= power;
        }
        if (blocks[chunk.getBlockSmart(x, y-1, z)].isSolid) {
            model.v1.light *= power;
            model.v2.light *= power;
        }
        if (model.v3.light == lightLow && blocks[chunk.getBlockSmart(x, y+1, z+1)].isSolid) {
            model.v3.light *= power;
        }
        if (model.v4.light == lightLow && blocks[chunk.getBlockSmart(x, y+1, z-1)].isSolid) {
            model.v4.light *= power;
        }
        if (  model.v1.light == lightLow && blocks[chunk.getBlockSmart(x, y-1, z-1)].isSolid) {
            model.v1.light *= power;
        }
        if (  model.v2.light == lightLow && blocks[chunk.getBlockSmart(x, y-1, z+1)].isSolid) {
            model.v2.light *= power;
        }
        rect(block.textures.side,block);
    }

    public void buildTop(Block block, Chunk chunk, int x, int y, int z) {
        ++y;
        BlockModel model = block.getModel();
        model.v1.pos.set(x+1, y, z);
        model.v2.pos.set(x, y, z);
        model.v3.pos.set(x, y, z+1);
        model.v4.pos.set(x+1, y, z+1);
        setLight(lightHigh,block);
        // lighting
        if (blocks[chunk.getBlockSmart(x+1, y, z)].isSolid) {
            model.v4.light *= power-0.f;
            model.v1.light *= power-0.f;
        }
        if (blocks[chunk.getBlockSmart(x, y, z+1)].isSolid) {
            model.v3.light *= power-0.f;
            model.v4.light *= power-0.f;
        }
        if (blocks[chunk.getBlockSmart(x-1, y, z)].isSolid) {
            model.v3.light *= power-0.f;
            model.v2.light *= power-0.f;
        }
        if (blocks[chunk.getBlockSmart(x, y, z-1)].isSolid) {
            model.v1.light *= power-0.f;
            model.v2.light *= power-0.f;
        }
        if (model.v3.light == lightHigh && blocks[chunk.getBlockSmart(x-1, y, z+1)].isSolid) {
            model.v3.light *= power-0.f;
        }
        if (model.v4.light == lightHigh && blocks[chunk.getBlockSmart(x+1, y, z+1)].isSolid) {
            model.v4.light *= power-0.f;
        }
        if (model.v1.light == lightHigh && blocks[chunk.getBlockSmart(x+1, y, z-1)].isSolid) {
            model.v1.light *= power-0.f;
        }
        if (model.v2.light == lightHigh && blocks[chunk.getBlockSmart(x-1, y, z-1)].isSolid) {
            model.v2.light *= power-0.f;
        }
        rect(block.textures.top,block);
    }

    public void buildDown(Block block, Chunk chunk, int x, int y, int z) {
        BlockModel model = block.getModel();
        model.v1.pos.set(x+1, y, z);
        model.v2.pos.set(x+1, y, z+1);
        model.v3.pos.set(x, y, z+1);
        model.v4.pos.set(x, y, z);
        setLight(lightDim,block);
        --y;
        // lighting
        if (blocks[chunk.getBlockSmart(x+1, y, z)].isSolid) {
            model.v1.light *= power;
            model. v2.light *= power;
        }
        if (blocks[chunk.getBlockSmart(x, y, z+1)].isSolid) {
            model.v2.light *= power;
            model.v3.light *= power;
        }
        if (blocks[chunk.getBlockSmart(x-1, y, z)].isSolid) {
            model.v3.light *= power;
            model.v4.light *= power;
        }
        if (blocks[chunk.getBlockSmart(x, y, z-1)].isSolid) {
            model.v1.light *= power;
            model.v4.light *= power;
        }
        if (model.v3.light == lightDim && blocks[chunk.getBlockSmart(x-1, y, z+1)].isSolid) {
            model.v3.light *= power;
        }
        if (model.v2.light == lightDim && blocks[chunk.getBlockSmart(x+1, y, z+1)].isSolid) {
            model.v2.light *= power;
        }
        if (model.v1.light == lightDim && blocks[chunk.getBlockSmart(x+1, y, z-1)].isSolid) {
            model.v1.light *= power;
        }
        if (model.v4.light == lightDim && blocks[chunk.getBlockSmart(x-1, y, z-1)].isSolid) {
            model.v4.light *= power;
        }
        rect(block.textures.bottom,block);
    }

    public ChunkMesh create(Chunk chunk) {
        if (!isBuilding) return null;
        isBuilding = false;
        return new ChunkMesh(chunk, vertices, context);
    }
    private void setLight(float light,Block block) {
        BlockModel model = block.getModel();
        model.v1.light = light;
        model.v2.light = light;
        model.v3.light = light;
        model.v4.light = light;
    }
    private void rect(TextureRegion region, Block block) {
        begin();
        setUVRange(region,block);
        BlockModel model = block.getModel();
        vertex(model.v1.pos, model.v1.light, model.v1.uv);
        vertex(model.v2.pos, model.v2.light, model.v2.uv);
        vertex(model.v3.pos, model.v3.light, model.v3.uv);
        vertex(model.v4.pos, model.v4.light, model.v4.uv);
    }
    private void vertex(final Vector3 pos, final float lit, final Vector2 uv) {
        vertices.add(pos.x, pos.y, pos.z, lit);
        vertices.add(uOffset+uScale*uv.x, vOffset+vScale*uv.y);
    }
    private void setUVRange (float u1, float v1, float u2, float v2) {
        uOffset = u1;
        vOffset = v1;
        uScale = u2 - u1;
        vScale = v2 - v1;
    }
    protected void setUVRange (TextureRegion region, Block block) {
        setUVRange(region.getU(), region.getV(), region.getU2(), region.getV2());
    }
    protected void begin() {
        if (isBuilding) return;
        isBuilding = true;
        vertices.clear();
    }
}
