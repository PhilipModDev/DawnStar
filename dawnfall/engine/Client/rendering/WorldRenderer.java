package com.dawnfall.engine.Client.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.IndexArray;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.dawnfall.engine.Client.ClientManager;
import com.dawnfall.engine.Client.handle.ui.HotBar;
import com.dawnfall.engine.DawnStar;
import com.dawnfall.engine.Server.entity.Player;
import com.dawnfall.engine.Client.rendering.mesh.VolatileMesh;
import com.dawnfall.engine.Server.util.IO.OptionProvider;
import com.dawnfall.engine.Server.util.VoxelRayCast;
import com.dawnfall.engine.Server.world.Blocks.Blocks;
import com.dawnfall.engine.Server.world.Multithreaded.MultiBuilder;
import com.dawnfall.engine.Server.world.World;
import com.dawnfall.engine.Server.world.chunkUtil.*;
import com.dawnfall.engine.Client.rendering.mesh.ChunkMesh;
import java.util.*;

import static com.badlogic.gdx.Gdx.gl;
import static com.dawnfall.engine.DawnStar.getInstance;

public class WorldRenderer implements Disposable {
    /**
     *World Renderer renders the world.
     */
    public World world;
    public ClientManager clientManager;
    private boolean gen;
    public Array<ChunkMesh> RenderList = new Array<>();
    public Array<ChunkMesh> RenderBuffer_1 = new Array<>();
    public Array<ChunkMesh> RenderBuffer_2 = new Array<>();
    public final GridPoint3 chunkFirst = new GridPoint3();
    public final GridPoint3 chunkLast = new GridPoint3();
    public final GridPoint3 playerFirst = new GridPoint3();
    public final GridPoint3 playerLast = new GridPoint3();
    public IndexData indexData;
    public DawnStar dawnStar;
    public MultiBuilder multiBuilder;
    private ChunkManager chunkManager;
    private ChunkOcclusion chunkOcclusion;
    public VoxelRayCast rayCast;
    private HotBar hotBar;
    public Player player;

    public WorldRenderer(boolean gen, ClientManager clientManager) {
        if (gen){
            //Updates the player's current chunk positions.
            this.clientManager = clientManager;
            this.dawnStar = getInstance();
            this.chunkFirst.set((int) dawnStar.player.getPlayerPosX() >> 4, (int) dawnStar.player.getPlayerPosY() >> 4, (int) dawnStar.player.getPlayerPosZ() >> 4);
            this.playerFirst.set((int) dawnStar.player.getPlayerPosX(), (int) dawnStar.player.getPlayerPosY(),(int) dawnStar.player.getPlayerPosZ());
            this.world = new World(true);
            this.chunkManager = world.chunkManager;
            this.chunkManager.worldRenderer = this;
            this.multiBuilder = new MultiBuilder(this.world.chunkManager);
            this.rayCast = new VoxelRayCast();
            this.hotBar = this.clientManager.getClientHud().bar;
            this.player = dawnStar.player;
            generateMeshData();
        }
        this.gen = gen;
    }
    /**
     * Update loop
     */
    public void update(float deltaTime) {
        if (gen) {
            try {
                //Updates the player's current chunk positions.
                chunkFirst.set(
                        (int) dawnStar.player.getPlayerPosX() >> 4,
                        (int) dawnStar.player.getPlayerPosY() >> 4,
                        (int) dawnStar.player.getPlayerPosZ() >> 4
                );
                playerFirst.set(
                        (int) dawnStar.player.getPlayerPosX(),
                        (int) dawnStar.player.getPlayerPosY(),
                        (int) dawnStar.player.getPlayerPosZ()
                );
                world.update(deltaTime);
                //This only runs during world startup.
                if (chunkOcclusion == null){
                    //Sets the player's position for entering the world.
                    player.getPerspectiveCamera().position.set(0,150,0);
                    chunkOcclusion = new ChunkOcclusion(this.chunkManager);
                    dawnStar.isWorldLoaded = true;
                }
                updateChunkMeshes();
                multiBuilder.queMeshBuilding(chunkManager.LOAD_CHUNKS);
                //Render the meshes.
                updateRenderingMeshes();
                chunkLast.set(chunkFirst);
                if (!clientManager.inputs.noBlockPlacement) updateRayCast();
            } catch (Exception exception) {
                exception.printStackTrace();
                Gdx.app.error("World Renderer","is overloading at:"+ System.currentTimeMillis());
            }
        }
    }
    /**
     * Updates the list for
     * rendering the chunks.
     */
    private void updateChunkMeshes() {
        //update the render list.
        chunkManager.updateDirtyChunks();
        synchronized (chunkManager.CHUNK_UPDATES){
            while (!chunkManager.CHUNK_UPDATES.isEmpty()){
                VolatileMesh volatileMesh = chunkManager.CHUNK_UPDATES.poll();
                if (volatileMesh == null) continue;
                Chunk chunk = volatileMesh.chunk;
                ChunkMesh chunkMesh = volatileMesh.mesh;
                if (chunk == null || chunkMesh == null)continue;
                Array<ChunkMesh> updateBuffer = getCurrentBuffer();
                chunkManager.handleChunkMesh(volatileMesh.mesh,volatileMesh.chunk, updateBuffer);
            }
        }
    }
    public Array<ChunkMesh> getCurrentBuffer(){
        return (RenderList == RenderBuffer_1) ? RenderBuffer_2 : RenderBuffer_1;
    }
    /**
     * Binds <code>OpenGL<code/> to render the
     * current meshes in the sense.
     */
    private void updateRenderingMeshes(){
        bindOpenGLRendering();
        if (OptionProvider.OCCLUSION) updateOcclusion();
        swapBuffers();
        Array<ChunkMesh> updateBuffer = getCurrentBuffer();
        Iterator<ChunkMesh> iteratorList =  new Array.ArrayIterator<>(updateBuffer);
        while (iteratorList.hasNext()) {
            ChunkMesh chunkMesh = iteratorList.next();
            Chunk chunk = chunkMesh.getChunk();
            if (!OptionProvider.OCCLUSION) chunk.setChunkVisible(true);
            //If chunks is the player's set visible.
            if (chunk.x == chunkFirst.x && chunk.y == chunkFirst.y && chunk.z == chunkFirst.z) {
                chunk.setChunkVisible(true);
            }
            //Checks if the chunk needs to be disposed.
            disposeChunkMesh(chunkMesh, iteratorList);
            if (chunkManager.isChunkVisible(chunk) && !chunkMesh.isDisposed) {
                //If chunk is in the update tick area set visible.
                chunkManager.loadChunksAroundPlayer(chunk);
                if (chunk.isChunkVisible()) {
                    chunkMesh.render(indexData);
                }
                if (chunk.isDirty) {
                    chunkManager.DIRTY_CHUNKS.add(chunk);
                }
            }
        }
        swapBuffers();
    }
    private void disposeChunkMesh(ChunkMesh mesh, Iterator<ChunkMesh> iterator){
        Chunk chunk = mesh.getChunk();
        if (chunk.x > chunkFirst.x + world.RENDER_DISTANCE || chunk.x < chunkFirst.x - world.RENDER_DISTANCE ||
                chunk.z < chunkFirst.z - world.RENDER_DISTANCE || chunk.z > chunkFirst.z + world.RENDER_DISTANCE) {
            if (!mesh.isDisposed) {
                mesh.dispose();
                if (chunk.safe){
                    chunk.isNewChunk = false;
                    chunk.safe = false;
                    chunk.setChunkVisible(false);
                }
                iterator.remove();
            }
        }
    }
    private synchronized void swapBuffers(){
        // Empty the buffer that was just used for rendering
        RenderList.clear();
        // Swap buffers
        if (RenderList == RenderBuffer_2){
            RenderList = RenderBuffer_1;
        }else if (RenderList == RenderBuffer_1){
            RenderList = RenderBuffer_2;
        }
    }
    private void updateOcclusion(){
        chunkOcclusion.occlusionGridCheck();
    }

    /**
     * Generates the mesh indices for the world.
     */
    private void generateMeshData(){
        final int MAX_INDICES = 98304;
        final short[] indices = new short[MAX_INDICES];
        indexData = new IndexArray(MAX_INDICES);
        for (int i = 0, v = 0; i < MAX_INDICES; i += 6, v += 4) {
            indices[i] = (short)v;
            indices[i+1] = (short)(v+1);
            indices[i+2] = (short)(v+2);
            indices[i+3] = (short)(v+2);
            indices[i+4] = (short)(v+3);
            indices[i+5] = (short) v;
        }
        indexData.setIndices(indices,0,indices.length);
    }

    /**
     * Binds the OpenGL rendering assets for the current
     * meshes being rendered.
     */
    private void bindOpenGLRendering(){
        DawnStar.getInstance().getTextureHolder().getMissing().getTexture().bind();
        DawnStar.getInstance().voxelShaderManager.bindTerrain(player.getCamera().combined);
        gl.glEnable(GL20.GL_CULL_FACE);
        gl.glCullFace(GL20.GL_BACK);
    }


    public void updateRayCast() {
        VoxelRayCast.RayInfo blockInfoRay = rayCast.ray(player);
        boolean breakBlock;
        boolean placeBlock;
        if (blockInfoRay != null) {
            //Feature controls options.
            breakBlock = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
            placeBlock = Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT);
            if (breakBlock) {
                world.breakBlockAt(blockInfoRay.in, Blocks.blocks[Blocks.AIR]);
                rayCast.ray(player);
            }
            if (placeBlock) {
                world.placeBlockAt(blockInfoRay.out, Blocks.blocks[hotBar.getItemId()]);
                rayCast.ray(player);
            }
            //manager.clientManager.boxManager.render(blockInfoRay);
        }
//manager.clientManager.boxManager.renderChunkBorder();
    }
    /**
     * Disposes the current resources to
     * free up memory.
     */
    @Override
    public void dispose() {
        gen = false;
        for (int i = 0; i < RenderList.size; i++) {
            ChunkMesh mesh  = RenderList.get(i);
            if (!mesh.isDisposed){
                mesh.dispose();
            }
        }
        for (int i = 0; i < RenderBuffer_2.size; i++) {
            ChunkMesh mesh  = RenderBuffer_2.get(i);
            if (!mesh.isDisposed){
                mesh.dispose();
            }
        }
        for (int i = 0; i < RenderBuffer_1.size; i++) {
            ChunkMesh mesh  = RenderBuffer_1.get(i);
            if (!mesh.isDisposed){
                mesh.dispose();
            }
        }
        RenderList.clear();
        RenderBuffer_1.clear();
        RenderBuffer_2.clear();
        chunkOcclusion = null;
        indexData.dispose();
        world.chunkManager.dispose();
        chunkManager = null;
    }
}
