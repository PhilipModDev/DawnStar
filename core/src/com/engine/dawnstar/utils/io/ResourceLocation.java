package com.engine.dawnstar.utils.io;

import com.badlogic.gdx.utils.Null;
import com.engine.dawnstar.Constants;
import com.engine.dawnstar.DawnStar;
import com.engine.dawnstar.main.blocks.Block;
import com.engine.dawnstar.main.blocks.BlockModel;
import com.google.gson.Gson;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public final class ResourceLocation {

    private final Gson gson;

    public ResourceLocation(){
        gson = new Gson();
        try {
            Path resource = Path.of(Constants.RESOURCE_LOCATION);
            if (Files.notExists(resource)){
                DawnStar.LOGGER.info("Creating resource directories.");
                Files.createDirectories(resource);
            }
        }catch (Exception exception){
            exception.printStackTrace(System.out);
        }
    }

    @Null
    public Optional<BlockModel> loadVoxelData(String filename, Class<?> type){
        if (type == Block.class){
            try(FileReader fileReader = new FileReader(Constants.RESOURCE_LOCATION.concat(filename+".json"))) {
                BlockModel data = gson.fromJson(fileReader,BlockModel.class);
                if (data == null) throw new RuntimeException("Block model could name be loaded: "+ filename);
                DawnStar.LOGGER.info("Loading "+filename+".json model.");
                return Optional.of(data);
            }catch (Exception exception){
                exception.printStackTrace(System.out);
            }
        }
        return Optional.empty();
    }
}
