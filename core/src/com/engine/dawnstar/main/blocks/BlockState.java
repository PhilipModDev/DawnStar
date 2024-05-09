package com.engine.dawnstar.main.blocks;

import java.util.Objects;

public class BlockState {
    //The hashcode used for the pallet.
    private int hashCode;
    //Defines if the block is solid.
    public boolean isSolid = false;
    //Defines if the block has collision
    public boolean isCollision = false;
    //If the block is a liquid.
    public boolean isLiquid = false;
    //The block is a light source.
    public boolean isLight = false;
    //The block is void.
    public boolean isVoid = false;
    //If the block is transparent.
    public boolean isTransparent = false;

    //Sets the states in the block pallet use by the block position.
    public void setBlockStateAt(int bx,int by,int bz){
        hashCode = Integer.MAX_VALUE ^ 2 * bx + Integer.MAX_VALUE * by + bz;
    }
    //Returns the current block state.
    public BlockState getCurrentState(){
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BlockState)) return false;
        BlockState that = (BlockState) o;
        return hashCode == that.hashCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashCode);
    }
}
