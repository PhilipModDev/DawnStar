package com.engine.dawnstar.utils.io;

import com.engine.dawnstar.main.data.Chunk;
import com.engine.dawnstar.main.data.IChunkLayer;
import com.engine.dawnstar.main.data.ISingleChunk;
import com.engine.dawnstar.main.data.World;
import com.engine.dawnstar.utils.infrastructure.User;
import com.engine.dawnstar.server.RequestContent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


public final class Codec  {

    public static User decodeUser(ByteBuf buffer){
        if (buffer.readableBytes() <= 0) throw new RuntimeException("Can't decode user because buffer dose not contain data.");
        byte[] data = new byte[buffer.readableBytes()];
        buffer.readBytes(data);
        User user = new User();
        String format = new String(data, StandardCharsets.UTF_8);
        user.setUsername(format);
        return user;
    }

    public static ByteBuf encodeUser(User user){
        if (user == null) throw new NullPointerException("Can't encode user because user is null");
        ByteBuf buffer = Unpooled.buffer();
        String format = user.getUsername();
        byte[] data = format.getBytes(StandardCharsets.UTF_8);
        buffer.writeInt(RequestContent.USER_AUTH);
        buffer.writeBytes(data);
        return buffer;
    }

    //Faster encoder.
    public static ByteBuf encodeUser(User user, ByteBuf buffer){
        if (user == null) throw new NullPointerException("Can't encode user because user is null");;
        String format = user.getUsername();
        byte[] data = format.getBytes(StandardCharsets.UTF_8);
        buffer.writeBytes(data);
        return buffer;
    }

    //Correct Algorithm.
    public static void encodeChunk(Chunk chunk, ByteBuf buffer){
        if (buffer == null) throw new NullPointerException("Can't encode chunk because buffer is null");
        ByteBuf tempBuffer = Unpooled.buffer();
        tempBuffer.writeInt(chunk.localX);
        tempBuffer.writeInt(chunk.localY);
        tempBuffer.writeInt(chunk.localZ);
        if (chunk.singleChunk.isSingle){
            //Marks the chunk as for single type.
            tempBuffer.writeBoolean(true);
            tempBuffer.writeByte(chunk.singleChunk.getData());
        }else {
            tempBuffer.writeBoolean(false);
            tempBuffer.writeByte(chunk.singleChunk.getData());
            int size = Chunk.SIZE * Chunk.SIZE;
            for (int i = 0; i < chunk.chunkLayers.length; i++) {
                IChunkLayer layer = chunk.chunkLayers[i];
                if (layer == null) {
                    tempBuffer.writeBoolean(true);
                } else {
                    tempBuffer.writeBoolean(false);
                    byte[] data = layer.data == null ? new byte[size] : layer.data;
                    tempBuffer.writeBytes(data);
                    tempBuffer.writeByte(chunk.singleChunk.getData());
                    tempBuffer.writeBoolean(layer.isSingle);
                }
            }
        }
        buffer.writeInt(tempBuffer.readableBytes());
        buffer.writeBytes(tempBuffer);
    }

    public static Chunk decodeChunk(ByteBuf buffer){
        if (buffer == null) throw new NullPointerException("Can't decode chunk because buffer is null");
        int x = buffer.readInt();
        int y = buffer.readInt();
        int z = buffer.readInt();
        boolean isSingle = buffer.readBoolean();
        Chunk chunk;
        ISingleChunk singleChunk = new ISingleChunk(x,y,z);
        if (isSingle){
            byte data = buffer.readByte();
            singleChunk.setData(data);
            chunk = new Chunk(x,y,z,singleChunk);
            return chunk;
        }
        IChunkLayer[] layers = new IChunkLayer[Chunk.SIZE];
        singleChunk.setData(buffer.readByte());
        singleChunk.isSingle = false;
        int size = Chunk.SIZE * Chunk.SIZE;
        for (int i = 0; i < layers.length; i++) {
            if (buffer.readBoolean()) {
                layers[i] = null;
            }else {
                byte[] data = new byte[size];
                buffer.readBytes(data);
                byte block = buffer.readByte();
                boolean single = buffer.readBoolean();
                IChunkLayer chunkLayer = new IChunkLayer(i,block);
                chunkLayer.data = data;
                chunkLayer.block = block;
                chunkLayer.isSingle = single;
                layers[i] = chunkLayer;
            }
        }
        chunk = new Chunk(x,y,z,layers);
        chunk.singleChunk = singleChunk;
        return chunk;
    }
}
