package com.engine.dawnstar.server;

import com.engine.dawnstar.client.DawnStarClient;
import com.engine.dawnstar.main.data.Chunk;
import com.engine.dawnstar.main.data.ChunkPos;
import com.engine.dawnstar.utils.io.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.List;

@ChannelHandler.Sharable
public class WorldChunkHandler extends SimpleChannelInboundHandler<ChunkPos> {

    private final DawnStarServer server;

    public WorldChunkHandler(DawnStarServer dawnStarServer){
        this.server = dawnStarServer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChunkPos msg) throws Exception {
//        sendChunkResponse(ctx, msg);
        sendChunkRadiusResponse(ctx, msg);
    }


    private void sendChunkResponse(ChannelHandlerContext ctx, ChunkPos chunkPos){
        Chunk chunk = server.world.getChunkAt(chunkPos);
        if (chunk == null) return;
        ByteBuf chunkBuffer = ctx.alloc().buffer();
        Codec.encodeChunk(chunk,chunkBuffer);
        ctx.writeAndFlush(chunkBuffer);
    }

    private void sendChunkRadiusResponse(ChannelHandlerContext ctx, ChunkPos chunkPos){
        List<Chunk> chunkList = server.world.fetchChunksInView(0,0,0, DawnStarClient.RENDER_DISTANCE);
        for (Chunk chunk : chunkList){
            ByteBuf buffer = ctx.alloc().buffer();
            Codec.encodeChunk(chunk,buffer);
            ctx.writeAndFlush(buffer);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace(System.out);
    }
}
