package com.dawnfall.engine.Server.world.chunkUtil;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.dawnfall.engine.Client.rendering.mesh.VolatileMesh;
import com.dawnfall.engine.DawnStar;
import com.dawnfall.engine.Server.util.Constants;
import com.dawnfall.engine.Server.util.ThreadUtilsFactory;
import com.dawnfall.engine.Server.util.math.Direction;
import com.dawnfall.engine.Server.util.math.Sphere;
import com.dawnfall.engine.Server.world.Multithreaded.ChunkBuilder;
import com.dawnfall.engine.Server.world.World;
import com.dawnfall.engine.Server.world.gen.ChunkDataGeneration;
import com.dawnfall.engine.Client.rendering.WorldRenderer;
import com.dawnfall.engine.Client.rendering.mesh.ChunkMesh;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
public class ChunkManager implements Disposable {
    public final ConcurrentHashMap<ChunkPos, ChunkColumn> CHUNK_COLUMNS;
    public final Queue<ChunkPos> LOAD_CHUNKS;
    public final Queue<Chunk> DIRTY_CHUNKS;
    public final Queue<VolatileMesh> CHUNK_UPDATES;
    public int dataSize;
    public World world;
    public WorldRenderer worldRenderer;
    public GridPoint3 playerChunk;
    //Gets the center of the chunk.
    public final Vector3 chunkCenter = new Vector3();
    //Holds the chunk sizes.
    private final Vector3 chunkDimensions = new Vector3(Chunk.CHUNK_SIZE,Chunk.CHUNK_SIZE,Chunk.CHUNK_SIZE);
    //Contains the chunk half dimension, which is 8.
    private final int chunkHalfDimension = (Chunk.CHUNK_SIZE/2);
    public final int MAX_CHUNKS_PER_FRAME;
    public final int CHUNK_UPDATE_SIZE;
    private static int processedChunks = 0;
    public static int dirty = 0;
    public static int dirtyChunksProcess = 0;
    public final ChunkDataGeneration dataGeneration;
    public final ChunkBuilder chunkBuilder;
    public final Sphere sphere;
    public final ThreadUtilsFactory threadUtilsFactory;
    public ChunkManager(World world) {
        //Create a new instance of the collections.
       CHUNK_COLUMNS = new ConcurrentHashMap<>();
       LOAD_CHUNKS = new ArrayDeque<>(32);
       DIRTY_CHUNKS = new ArrayDeque<>();
       CHUNK_UPDATES = new ArrayDeque<>();
        this.world = world;
        this.sphere = new Sphere(world.RENDER_DISTANCE);
        dataGeneration = new ChunkDataGeneration(
                this);
        chunkBuilder = new ChunkBuilder(world);
        MAX_CHUNKS_PER_FRAME = Constants.CHUNKS_FRAME_BASE;
        CHUNK_UPDATE_SIZE = Constants.CHUNK_UPDATE_DISTANCE;
        threadUtilsFactory = DawnStar.getInstance().getThreadFactory();
    }
    public void generate(){
        //Gets the players chunk coordinates position.
        int playerChunkX = (int) (DawnStar.getInstance().player.getPlayerPosX() / Chunk.CHUNK_SIZE);
        int playerChunkY = (int) (DawnStar.getInstance().player.getPlayerPosY() / Chunk.CHUNK_SIZE);
        int playerChunkZ = (int) (DawnStar.getInstance().player.getPlayerPosZ() / Chunk.CHUNK_SIZE);

        sphere.setPositions(playerChunkX,playerChunkY,playerChunkZ);
        for (int x = playerChunkX - world.RENDER_DISTANCE; x <= playerChunkX + world.RENDER_DISTANCE; x++) {
            for (int z = playerChunkZ - world.RENDER_DISTANCE; z <= playerChunkZ + world.RENDER_DISTANCE; z++) {
                for (int y = ChunkColumn.HEIGHT-1; y >= 0; y--) {
                    //Check if it is a loaded chunk.
                    if (!sphere.isPointInSphere(x,y,z)) continue;
                    ChunkPos chunkPos = new ChunkPos(x, y, z);
                    LOAD_CHUNKS.add(chunkPos);
                }
            }
        }
    }
    /**
     * Updates the <code>LOAD_CHUNKS Queue<code>
     */
    private void updateLoadChunks() {
        //Gets the players chunk coordinates position.
        int playerChunkX = worldRenderer.chunkFirst.x;
        int playerChunkY = worldRenderer.chunkFirst.y;
        int playerChunkZ = worldRenderer.chunkFirst.z;
        sphere.setPositions(playerChunkX,playerChunkY,playerChunkZ);
        playerChunk = worldRenderer.chunkFirst;
        //Is used to create new chunks before removal.

        if (LOAD_CHUNKS.size() >= 32) return;
        for (int x = playerChunkX - world.RENDER_DISTANCE; x <= playerChunkX + world.RENDER_DISTANCE; x++) {
            for (int z = playerChunkZ - world.RENDER_DISTANCE; z <= playerChunkZ + world.RENDER_DISTANCE; z++) {
                for (int y = ChunkColumn.HEIGHT-1; y >= 0; y--) {
                    //Check if it is a loaded chunk.
                    if (!sphere.isPointInSphere(x,y,z)) continue;
                    Chunk chunk = world.getChunkAt(x,y,z);
                    ChunkPos chunkPos = new ChunkPos(x, y, z);
                    if (chunk == null) {
                        LOAD_CHUNKS.add(chunkPos);
                        continue;
                    }
                    unloadWorldHeightChunks(playerChunkY,chunk,chunkPos);
                    if (!chunk.safe){
                        setChunkSafe(chunk);
                        LOAD_CHUNKS.add(chunkPos);
                    }
                }
            }
        }
    }
    public ArrayList<ChunkPos> generateSpherePoints(int renderDistance){
        //Gets the players chunk coordinates position.
        int playerChunkX = worldRenderer.chunkFirst.x;
        int playerChunkY = worldRenderer.chunkFirst.y;
        int playerChunkZ = worldRenderer.chunkFirst.z;
        sphere.setPositions(playerChunkX,playerChunkY,playerChunkZ);
        ArrayList<ChunkPos> list = new ArrayList<>();
        //Is used to create new chunks before removal.
        for (int x = playerChunkX -renderDistance; x <= playerChunkX + renderDistance; x++) {
            for (int z = playerChunkZ - renderDistance; z <= playerChunkZ + renderDistance; z++) {
                for (int y = ChunkColumn.HEIGHT-1; y >= 0; y--) {
                    if (sphere.isAtEdgePoint(x,y,z)) list.add(new ChunkPos(x,y,z));
                }
            }
        }
        return list;
    }
    private void unloadWorldHeightChunks(int playerChunkY, Chunk chunk,ChunkPos chunkPos){
        if (playerChunkY > ChunkDataGeneration.CHUNK_HEIGHT_OFFSET  && chunk.y >= ChunkDataGeneration.CHUNK_HEIGHT_OFFSET ){
            if (!chunk.isChunkVisible()){
                LOAD_CHUNKS.add(chunkPos);
            }
            chunk.setChunkVisible(true);
        }
    }
    private boolean chunkContainsValue(byte value,Chunk chunk){
        byte[] data = chunk.getBlockData();
        for (byte datum : data) {
            if (datum == value) return true;
        }
        return false;
    }
    public boolean chunkContainsValue(byte valueA, byte valueB,Chunk chunk){
        byte[] data = chunk.getBlockData();
        for (byte datum : data) {
            if (datum == valueA || datum == valueB) return true;
        }
        return false;
    }
    public void loadChunksAroundPlayer(Chunk chunk){
        if (chunk.x > playerChunk.x - CHUNK_UPDATE_SIZE && chunk.x < playerChunk.x + CHUNK_UPDATE_SIZE) {
            if (chunk.y > playerChunk.y - CHUNK_UPDATE_SIZE && chunk.y < playerChunk.y + CHUNK_UPDATE_SIZE) {
                if (chunk.z > playerChunk.z - CHUNK_UPDATE_SIZE && chunk.z < playerChunk.z + CHUNK_UPDATE_SIZE) {
                    if (!chunk.meshBuilt){
                        ChunkPos chunkPos = new ChunkPos(chunk.x,chunk.y,chunk.z);
                        LOAD_CHUNKS.add(chunkPos);
                        return;
                    }
                    chunk.setChunkVisible(true);
                }
            }
        }
    }
    /**
     * Creates a new chunk mesh and returns it.
     */
    public VolatileMesh createChunkAt(ChunkPos chunkPos){
        ChunkColumn column = dataGeneration.generateData(chunkPos);
        if (column != null) {
            Chunk chunk = column.getChunk(chunkPos.y);
            if (chunk == null) return null;
            chunk.generated = true;
            chunk.isNewChunk = true;
            return chunkBuilder.buildVolatileMesh(chunk);
        }
        return null;
    }
    /**
     * updates a chunk and returns a new volatile mesh.
     */
    public void updateDirtyChunks(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            Chunk chunk = world.getChunkAtPlayer();
            chunk.clear();
            chunk.isDirty = true;
            DIRTY_CHUNKS.add(chunk);

        }
        while (!DIRTY_CHUNKS.isEmpty()) {
            Chunk chunk = DIRTY_CHUNKS.poll();
            if (chunk != null && chunk.isDirty) {
                chunk.isDirty = false;
                VolatileMesh volatileMesh = chunkBuilder.buildVolatileMesh(chunk);
                if (volatileMesh == null || volatileMesh.mesh == null) return;
                Array<ChunkMesh> updateBuffer = worldRenderer.getCurrentBuffer();
                handleChunkMesh(volatileMesh.mesh, chunk, updateBuffer);
            }
        }
    }
    public ArrayList<Chunk> getAllChunks(){
        ArrayList<Chunk> chunks = new ArrayList<>();
       Set<Map.Entry<ChunkPos,ChunkColumn>> set = CHUNK_COLUMNS.entrySet();
        for (Map.Entry<ChunkPos,ChunkColumn> entry: set) {
            for (int i = 0; i < World.HEIGHT; i++) {
                chunks.add(entry.getValue().getChunk(i));
            }
        }
        return chunks;
    }
    public void setChunkVisibleAndSafe(Chunk chunk){
        chunk.setChunkVisible(true);
        chunk.safe = false;
    }
    public void setChunkSafe(Chunk chunk){
            chunk.safe = true;
    }
    public void buildChunkAt(ChunkPos chunkPos){
        final Chunk chunk = world.getChunkAt(chunkPos.x,chunkPos.y,chunkPos.z);
        if (chunk == null) return;
        VolatileMesh volatileMesh = chunkBuilder.buildVolatileMesh(chunk);
        if (volatileMesh == null) return;
        handleChunkMesh(volatileMesh.mesh, chunk, worldRenderer.RenderList);
    }
    /**Checks whether if the play's position moved to another chunk. */
    public void checkChunks() {
        if (isPlayerChangeChunk()) {
            //Call to generate new chunks.
            threadUtilsFactory.execute(this::updateLoadChunks);
        }
    }
    /**
     * Returns true if the chunk at <code>v y z</code>
     * contains in <code>ChunkHolder</code>
     */
    public boolean hasNeighbours(int x, int y, int z) {
        final Chunk chunkN = world.getChunkAt(x, y, z + Direction.NORTH.v);
        final Chunk chunkS = world.getChunkAt(x, y, z + Direction.SOUTH.v);
        final Chunk chunkE = world.getChunkAt(x + Direction.EAST.v, y, z);
        final Chunk chunkW = world.getChunkAt(x + Direction.WEST.v, y, z);

        if (chunkN == null) return false;
        if (chunkS == null) return false;
        if (chunkE == null) return false;
        return chunkW != null;
    }
    public boolean hasSixNeighbours(Chunk chunk){
        if (chunk == null) return false;
        int x = chunk.x;
        int y = chunk.y;
        int z = chunk.z;
        Chunk chunkN = world.getChunkAt(x, y, z + Direction.NORTH.v);
        if (chunkN == null) return false;
        Chunk chunkS = world.getChunkAt(x, y, z + Direction.SOUTH.v);
        if (chunkS == null) return false;
        Chunk chunkE = world.getChunkAt(x + Direction.EAST.v, y, z);
        if (chunkE == null) return false;
        Chunk chunkW = world.getChunkAt(x + Direction.WEST.v, y, z);
        if (chunkW == null)return false;
        Chunk chunkU = world.getChunkAt(x, y+ Direction.UP.v, z);
        if (chunkU == null) return false;
        Chunk chunkD = world.getChunkAt(x, y + Direction.DOWN.v, z);
        return chunkD != null;
    }

    public boolean hasNeighboursColumnes(int x, int z) {
        ChunkColumn chunkN = world.getChunkColumn(x, (z + Direction.NORTH.v));
        ChunkColumn chunkS = world.getChunkColumn(x, (z + Direction.SOUTH.v));
        ChunkColumn chunkE = world.getChunkColumn((x + Direction.EAST.v), z);
        ChunkColumn chunkW = world.getChunkColumn((Direction.WEST.v), z);
        if (chunkN == null) return false;
        if (chunkS == null) return false;
        if (chunkE == null) return false;
        return chunkW != null;
    }

    /**Checks whether if the mesh contains null data.
     * If <code>newChunkMesh</code> is null then it
     * will dispose it.
     * */
    public void handleChunkMesh(ChunkMesh newChunkMesh, Chunk chunk, Array<ChunkMesh> meshes){
        if (newChunkMesh == null){
            for (int i = 0; i < meshes.size; i++) {
                ChunkMesh chunkMesh = meshes.get(i);
                if (chunkMesh == null) continue;
                if (chunkMesh.getChunk().equals(chunk)){
                    chunkMesh.dispose();
                    meshes.removeIndex(i);
                }
            }
        }else {
            for (int i = 0; i < meshes.size; i++) {
                ChunkMesh chunkMesh = meshes.get(i);
                if (chunkMesh.getChunk().equals(newChunkMesh.getChunk())){
                    chunkMesh.dispose();
                    meshes.removeIndex(i);
                }
            }
            meshes.add(newChunkMesh);
        }
    }

    public boolean isPlayerChangeChunk(){
        return !worldRenderer.chunkLast.equals(worldRenderer.chunkFirst);
    }
    /**
     * Checks whether if the current chunks specifically
     * <code>chunkMesh</code> is within the viewing range
     * of the player. Or in simpler terms frustum culling.
     */
    public boolean isChunkVisible(Chunk chunk){
        chunkCenter.set(
                chunk.x*Chunk.CHUNK_SIZE+chunkHalfDimension,
                chunk.y*Chunk.CHUNK_SIZE+chunkHalfDimension,
                chunk.z*Chunk.CHUNK_SIZE+chunkHalfDimension
        );
        return DawnStar.getInstance().getWorldRenderer().player.getCamera().frustum.boundsInFrustum(chunkCenter,chunkDimensions);
    }
    public boolean isChunkVisible(ChunkPos chunkPos){
        chunkCenter.set(
                chunkPos.x*Chunk.CHUNK_SIZE+chunkHalfDimension,
                chunkPos.y*Chunk.CHUNK_SIZE+chunkHalfDimension,
                chunkPos.z*Chunk.CHUNK_SIZE+chunkHalfDimension
        );
        return DawnStar.getInstance().getWorldRenderer().player.getCamera().frustum.boundsInFrustum(chunkCenter,chunkDimensions);
    }
    public boolean isChunkVisible(Vector3 chunkPos){
        chunkCenter.set(
                chunkPos.x*Chunk.CHUNK_SIZE+chunkHalfDimension,
                chunkPos.y*Chunk.CHUNK_SIZE+chunkHalfDimension,
                chunkPos.z*Chunk.CHUNK_SIZE+chunkHalfDimension
        );
        return DawnStar.getInstance().getWorldRenderer().player.getCamera().frustum.boundsInFrustum(chunkCenter,chunkDimensions);
    }
    public GridPoint3 getChunkCoordinatesPositionDebug() {
        return playerChunk;
    }

    public int getDataSize() {
        return dataSize;
    }
    public static int getDirty() {
        if (dirty >= 10) {
            dirty = 0;
        }
        return dirty;
    }
    public static int getDirtyChunksProcess() {
        if (dirtyChunksProcess >= 10) {
            dirtyChunksProcess = 0;
        }
        return dirtyChunksProcess;
    }
    public static int getProcessedChunks() {
        if (processedChunks >= 100) {
            processedChunks = 0;
        }
        return processedChunks;
    }

    @Override
    public void dispose() {
        LOAD_CHUNKS.clear();
        CHUNK_COLUMNS.clear();
    }
}