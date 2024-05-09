package com.engine.dawnstar.main.blocks;

import com.engine.dawnstar.utils.Registry;
import java.util.ArrayList;

import static com.engine.dawnstar.DawnStar.LOGGER;

public final class Blocks {
    //Registers all blocks and textures.
    public static final Registry<Block> AIR = Registry.create(()-> new Block("air",0,false));
    public static final Registry<Block> STONE = Registry.create(()-> new Block("stone",1,true));
    public static final Registry<Block> GRASS = Registry.create(()-> new Block("grass",2,true));
    public static final Registry<Block> DIRT = Registry.create(()-> new Block("dirt",3,true));
    public static final Registry<Block> WATER = Registry.create(()-> new Block("water",4,false));
    public static final Registry<Block> BLOCK = Registry.create(() -> new Block("block",5,true));
    //List of registered blocks.
    private final ArrayList<Block> allRegisteredBlocks;

    public Blocks(){
        allRegisteredBlocks = new ArrayList<>();
    }
    //The load method for loading all blocks.
    public void registerBlocks(){
        LOGGER.debug("Loading Registered Blocks.");
        allRegisteredBlocks.add(AIR.get());
        allRegisteredBlocks.add(STONE.get());
        allRegisteredBlocks.add(GRASS.get());
        allRegisteredBlocks.add(DIRT.get());
        allRegisteredBlocks.add(WATER.get());
        allRegisteredBlocks.add(BLOCK.get());
        LOGGER.debug("Loading Registered Blocks Complete.");
    }

    public Block getBlockById(int id){
        if (id >= allRegisteredBlocks.size())
            throw new RuntimeException("Block could not be found by id:"+ id);
        return allRegisteredBlocks.get(id);
    }
}