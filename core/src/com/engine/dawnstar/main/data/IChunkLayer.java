package com.engine.dawnstar.main.data;

import java.util.Arrays;

public final class IChunkLayer {
    //the layer data in array.
    public byte[] data;
    public byte block = -1;
    //the local block coordinates.
    public int bx;
    public int bz;
    public final int level;
    //Checks if single data layer and rest is same.
    public boolean isSingle = false;

    public IChunkLayer(int level){
        this.level = level;
    }

    public IChunkLayer(int level,byte block){
        this.level = level;
        isSingle = true;
        this.block = block;
    }
   //Sets the data in the layer at the coordinates.
    public void setData(int x,int z,byte block){
        //If single a block is same. Update coordinates.
        if (isSingle && block == this.block){
            bx = x;
            bz = z;
            return;
        }
        //If the first data set the layer type and single.
        if (this.block == -1){
            isSingle = true;
            this.block = block;
            bx = x;
            bz = z;
            return;
        }
        //If the is not the same and previous is single then set the new data and fill around.
        if (this.block != block && isSingle){
            if (data == null){
                data = new byte[Chunk.SIZE * Chunk.SIZE];
            }
           isSingle = false;
           Arrays.fill(data,this.block);
           data[x | z << 5] = block;
           return;
        }
        //Sets the new data at the coordinates.
        if (this.block != block){
            data[x | z << 5] = block;
        }
    }
   //Gets the data at the level with the coordinates.
    public byte getData(int x,int z){
         if (x < 0 || x >= Chunk.SIZE) return 0;
         if (z < 0 || z >= Chunk.SIZE) return 0;
         if (block == -1) return 0;
         if (isSingle) return this.block;
         return data[x | z << 5];
    }
}
