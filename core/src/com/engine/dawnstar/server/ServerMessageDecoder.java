package com.engine.dawnstar.server;

import com.engine.dawnstar.main.data.ChunkPos;
import com.engine.dawnstar.main.data.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ServerMessageDecoder extends ByteToMessageDecoder {
    private int messageCode;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (messageCode == 0) messageCode = byteBuf.readInt();

        if (messageCode == RequestContent.USER_AUTH){
            byte[] profileData = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(profileData);
            String data = new String(profileData, StandardCharsets.UTF_8);
            GameProfile gameProfile = new GameProfile(data);
            list.add(gameProfile);
            messageCode = 0;
        }
        if (messageCode == RequestContent.CHUNK_REQUEST){

            int x = byteBuf.readInt();
            int y = byteBuf.readInt();
            int z = byteBuf.readInt();
            ChunkPos chunkPos = new ChunkPos(x,y,z);
            list.add(chunkPos);
            messageCode = 0;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace(System.out);
    }
}
