package com.dawnfall.engine.Server.world.chunkUtil;

public class ChunkPos  {
   //Coordinates.
    public int x;

    public int y;

    public int z;
    private int hashCode;
    public ChunkPos(){
        hashCode = 31;
    }
    public ChunkPos(int x,int y,int z){
        this.x = x;
        this.y = y;
        this.z = z;
        this.hashCode = 31 * x + y + z;
    }
    //Adds chunk positions.
    public void addPos(int x,int y,int z){
        this.x = this.x + x;
        this.y = this.y + y;
        this.z = this.z + z;
        this.hashCode = 31 * x + y + z;
    }
    public void addPos(ChunkPos coordinates){
        this.x = this.x + coordinates.x;
        this.y = this.y + coordinates.y;
        this.z = this.z + coordinates.z;
        this.hashCode = 31 * x + y + z;
    }
    //Subs chunk positions.
    public void subPos(int x,int y,int z){
        this.x = this.x - x;
        this.y = this.y - y;
        this.z = this.z - z;
        this.hashCode = 31 * x + y + z;
    }
    public void subPos(ChunkPos coordinates){
        this.x = this.x - coordinates.x;
        this.y = this.y - coordinates.y;
        this.z = this.z - coordinates.z;
        this.hashCode = 31 * x + y + z;
    }
    public ChunkPos set(int x,int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
        this.hashCode = 31 * x + y + z;
        return this;
    }
    //Copy the ChunkPos
    public ChunkPos copy(){
        return this;
    }
    @Override
    public int hashCode() {
       return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChunkPos)) return false;
        ChunkPos compare = (ChunkPos) obj;
        return x == compare.x&&y == compare.y&&z== compare.z;
    }

    @Override
    public String toString() {
        return "x:"+x+":y:"+y+":z:"+z;
    }
}
