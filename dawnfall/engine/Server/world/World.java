package com.dawnfall.engine.Server.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector3;
import com.dawnfall.engine.DawnStar;
import com.dawnfall.engine.Client.rendering.WorldRenderer;
import com.dawnfall.engine.Server.entity.Player;
import com.dawnfall.engine.Server.world.chunkUtil.ChunkPos;
import com.dawnfall.engine.Server.util.IO.OptionProvider;
import com.dawnfall.engine.Server.util.math.Direction;
import com.dawnfall.engine.Server.world.Blocks.Block;
import com.dawnfall.engine.Server.world.Blocks.Blocks;
import com.dawnfall.engine.Server.world.chunkUtil.Chunk;
import com.dawnfall.engine.Server.world.chunkUtil.ChunkColumn;
import com.dawnfall.engine.Server.world.chunkUtil.ChunkManager;
import com.dawnfall.engine.Server.util.BlockPos;


public class World {
    private static final int default_size = 15;
    public final static int WORLD_SIZE = default_size*16;
    public final static int WORLD_HEIGHT = OptionProvider.WORLD_HEIGHT;
    public static final int HEIGHT = WORLD_HEIGHT / Chunk.CHUNK_SIZE;//16
    public final int RENDER_DISTANCE = OptionProvider.RENDER_DISTANCE;
    public static World world;
    public ChunkManager chunkManager;


    public World(boolean generate)  {
        world = this;
        if (generate){
            Gdx.app.log("Thread:"+Thread.currentThread().getName(), "Loading world");
            chunkManager = new ChunkManager(this);
            chunkManager.generate();
            DawnStar.getInstance().player.setWorld(this);
        }
    }
    //This method is called every frame.
    public void update(float deltaTime){
        chunkManager.checkChunks();
        DawnStar.getInstance().EVENTS.WORLD_TICK.fireEvent();
    }
    public static int getTickUpdate(){
       return DawnStar.getInstance().EVENTS.WORLD_TICK.getWorldTick();
    }

    public Chunk getChunkAtDirection(int x, int y, int z, Direction direction){
        if (direction.equals(Direction.SOUTH) || direction.equals(Direction.NORTH)){
            return getChunkAt(x,y,z + direction.v);
        }
        if (direction.equals(Direction.EAST) || direction.equals(Direction.WEST)){
            return  getChunkAt(x + direction.v,y,z);
        }
        if (direction.equals(Direction.UP) || direction.equals(Direction.DOWN)){
            return  getChunkAt(x ,y + direction.v,z);
        }
        return null;
    }
    public ChunkPos getChunkPosAtDirection(int x, int y, int z, Direction direction){
        if (direction.equals(Direction.SOUTH) || direction.equals(Direction.NORTH)){
            return new ChunkPos(x,y,z + direction.v);
        }
        if (direction.equals(Direction.EAST) || direction.equals(Direction.WEST)){
            return  new ChunkPos(x + direction.v,y,z);
        }
        if (direction.equals(Direction.UP) || direction.equals(Direction.DOWN)){
            return  new ChunkPos(x ,y + direction.v,z);
        }
        return null;
    }
    public Chunk getChunkAtDirection(Chunk chunk, Direction direction){
        int x = chunk.x;
        int y = chunk.y;
        int z = chunk.z;
        if (direction.equals(Direction.SOUTH) || direction.equals(Direction.NORTH)){
            return getChunkAt(x,y,z + direction.v);
        }
        if (direction.equals(Direction.EAST) || direction.equals(Direction.WEST)){
            return  getChunkAt(x + direction.v,y,z);
        }
        if (direction.equals(Direction.UP) || direction.equals(Direction.DOWN)){
            return  getChunkAt(x ,y + direction.v,z);
        }
        return null;
    }
    public Chunk getChunkAt(int x, int yLevel, int z){
        ChunkColumn column = getChunkColumn(x, z);
        if (column == null) return null;
        return column.getChunk(yLevel);
    }
    public Chunk getChunkAt(ChunkPos chunkPos){
        ChunkColumn column = getChunkColumn(chunkPos.x, chunkPos.z);
        if (column == null) return null;
        return column.getChunk(chunkPos.y);
    }
    public Chunk getChunkAt(Vector3 chunkPos){
        ChunkColumn column = getChunkColumn((int) chunkPos.x, (int) chunkPos.z);
        if (column == null) return null;
        return column.getChunk((int) chunkPos.y);
    }
    public Chunk getChunk(int px, int py, int pz){
      return getChunkAt(px>>4, py>>4, pz>>4);
    }
    public byte getBlock(int px,int py, int pz){
        int yLevel = py>>4;
        if (yLevel < 0 || yLevel > ChunkColumn.HEIGHT) return Blocks.AIR;
        Chunk chunk = getChunk(px, py, pz);
        if (chunk == null) return Blocks.AIR;
        return chunk.getBlockData() == null ? Blocks.AIR : chunk.getBlockAt(px&15, py&15, pz&15);
    }

    public void setBlock(int px,int py, int pz,byte block) {
        int yLevel = py>>4;
        if (yLevel < 0 || yLevel > ChunkColumn.HEIGHT) return;
        Chunk chunk = getChunkAt(px, py, pz);
        if (chunk == null) return;
        chunk.setBlockAt(px, py, pz, block);
    }
    public ChunkColumn getChunkColumn(int x, int z){
        ChunkPos chunkPos = new ChunkPos(x,0,z);
        return chunkManager.CHUNK_COLUMNS.get(chunkPos);
    }
   public boolean isWorldSize(int x, int y){
       if (x <= WORLD_SIZE && y <= WORLD_SIZE){
           return x >= -WORLD_SIZE && y >= -WORLD_SIZE;
       }
       return false;
   }
    public static World getWorld() {
        return world;
    }

    public Chunk getChunkAtPlayer(){
        Player player = DawnStar.getInstance().getWorldRenderer().player;
        int x = (int) player.getPlayerPosX() >>4;
        int y = (int) player.getPlayerPosY() >>4;
        int z = (int) player.getPlayerPosZ() >>4;
        return getChunkAt(x,y,z);
    }

    public Vector3 getPlayersCoordinatesInChunk(){
        Player player = DawnStar.getInstance().getWorldRenderer().player;
        Chunk chunk = getChunkAtPlayer();
        if (chunk == null) return null;
        int size = Chunk.CHUNK_SIZE-1;
        int px = ((int) player.getPlayerPosX()) & size;
        int py = ((int) player.getPlayerPosY()) & size;
        int pz = ((int) player.getPlayerPosZ()) & size;
        return new Vector3(px,py,pz);
    }

    public Vector3 getChunkCoordinatesAtPlayer(){
        Player player = DawnStar.getInstance().getWorldRenderer().player;
        int ChunkX = (int) player.getPlayerPosX();
        int ChunkY = (int) player.getPlayerPosY();
        int ChunkZ = (int) player.getPlayerPosZ();
        return new Vector3(ChunkX>>4,ChunkY>>4,ChunkZ>>4);
    }
    public void breakBlockAt(final BlockPos in, final Block block) {
        final Chunk chunk = getChunk(in.x,in.y,in.z);
        if (chunk != null){
            chunk.editBlockAt(in.x,in.y,in.z,block);
            ChunkManager.dirty++;
        }
    }
    public void placeBlockAt(final BlockPos out, final Block block) {
        final Chunk chunk = getChunk(out.x,out.y,out.z);
        if (chunk != null){
            chunk.editBlockAt(out.x,out.y,out.z,block);
            ChunkManager.dirty++;
        }
    }

    public BlockPos getHighestBlockPosAt(int px,int pz){
        for (int y = 0; y < WORLD_HEIGHT; y++) {
            if (getBlock(px,y,pz) > 1 && getBlock(px,y+1,pz) == 0){
               return new BlockPos(px,y,pz);
            }
        }
        return null;
    }

    public boolean isPlayerMoving(){
        WorldRenderer worldRenderer = DawnStar.getInstance().getWorldRenderer();
        GridPoint3 pFirst = worldRenderer.playerFirst;
        GridPoint3 pLast = worldRenderer.playerLast;
        if (pLast.x != pFirst.x && pLast.y != pFirst.y && pLast.z != pFirst.z){
            pLast.set(pFirst);
            return true;
        }
        return false;
    }
}
