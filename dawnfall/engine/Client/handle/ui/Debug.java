package com.dawnfall.engine.Client.handle.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.dawnfall.engine.Client.ClientManager;
import com.dawnfall.engine.DawnStar;
import com.dawnfall.engine.Server.util.math.Direction;
import com.dawnfall.engine.Server.world.chunkUtil.ChunkManager;
import com.dawnfall.engine.Server.world.World;

public class Debug {
    //The Debug Class.
    public static boolean SHOW_UI_TABLES = false;

    private static ModelInstance line;
    public static void setRenderDebugMode(boolean isDebugMode, PerspectiveCamera camera, ClientManager client){
        if (isDebugMode){
            int x = Math.round(camera.position.x);
            int y = Math.round(camera.position.y);
            int z = Math.round(camera.position.z);
            DawnStar.getInstance().spriteBatch.begin();
            client.getClientHud().getTextFont().draw(DawnStar.getInstance().spriteBatch,"Coordinates:"+x+","+y+","+z+"\n:Update Tick"+
                    World.getTickUpdate()+ "\nMemory Heap:"+ (short)Gdx.app.getNativeHeap()+
                    "\nWorld Data Size:"+ client.worldRenderer.world.chunkManager.getDataSize()+"\nRender Calls:"+
                    client.glProfiler.getDrawCalls()+" FPS:"+Gdx.graphics.getFramesPerSecond()+"\nChunk Position:"
                    +client.worldRenderer.world.chunkManager.getChunkCoordinatesPositionDebug()+"\nDirty CHUNK_COLUMNS:"+ChunkManager.getDirty()
                    +"\nDirty CHUNK_COLUMNS Processed:"+ChunkManager.getDirtyChunksProcess()+"\nBuilt CHUNK_COLUMNS:"+ChunkManager.getProcessedChunks()+"\n" +
                    "Chunks loading in:"+client.worldRenderer.world.chunkManager.LOAD_CHUNKS.size()+
                    "\nRenderList Size"+client.worldRenderer.RenderList.size+"\n" +
                    "Player Facing Direction:"+ Direction.vec3ToDirection(camera.direction),0,680);
            DawnStar.getInstance().spriteBatch.end();
            client.glProfiler.reset();
        }
    }
    //Shows the UI table debugButton.
    public static void setTableDebug(boolean tableDebug, Table table){
       if (tableDebug){
           SHOW_UI_TABLES = true;
           table.setDebug(true);
       }
    }
    public static class DebuggingTools {
        //Shows the debugButton grid.
        private Model gridLine;
        private Model xyzLine;
        private Material material;
        private int attributes;
        public ModelInstance showGrid(){
            material = new Material( ColorAttribute.createDiffuse(0.2f,0.2f,0.2f,1f));
            int attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked;
            ModelBuilder builder = new ModelBuilder();
            gridLine = builder.createLineGrid(10000,1000,16,16,material,attributes);
            ModelInstance grid;
            return grid = new ModelInstance(gridLine);
        }

        public void initializeDebugTools(){
            material = new Material(new ColorAttribute(ColorAttribute.Diffuse, Color.WHITE));
            attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorPacked;
            ModelBuilder builder = new ModelBuilder();
            xyzLine = builder.createXYZCoordinates(16,material,attributes);
            line = new ModelInstance(xyzLine);
        }
        public ModelInstance showXYZLine(){
            return line;
        }
        public void dispose(){
            if (gridLine != null){
                gridLine.dispose();
            }
            if (xyzLine != null){
                xyzLine.dispose();
            }
        }
    }
}
