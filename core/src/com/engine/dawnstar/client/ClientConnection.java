package com.engine.dawnstar.client;

import com.engine.dawnstar.main.data.Chunk;
import com.engine.dawnstar.main.data.GameProfile;
import com.engine.dawnstar.server.RequestContent;
import com.engine.dawnstar.utils.io.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class ClientConnection extends SimpleChannelInboundHandler<ByteBuf> {

    private final DawnStarClient dawnStarClient = DawnStarClient.getInstance();
    private ChannelHandlerContext connectionContext;
    private ByteBuf packet;
    private InetSocketAddress remoteAddress;
    private boolean receiveChunk = false;
    private int chunkExpectedSize = -1;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isActive()) {
            DawnStarClient.LOGGER.info("Connected to integrated server "+ dawnStarClient.clientUser);
            this.connectionContext = ctx;
            this.remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            PooledByteBufAllocator byteBufAllocator = (PooledByteBufAllocator) ctx.alloc();
//            sendGameProfile();


            ByteBuf buffer = byteBufAllocator.buffer();
            buffer.writeInt(RequestContent.CHUNK_REQUEST);
            buffer.writeInt(0);
            buffer.writeInt(0);
            buffer.writeInt(0);
            ctx.writeAndFlush(buffer);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.connectionContext = ctx;
        ReferenceCountUtil.release(packet);
        DawnStarClient.LOGGER.info("Disconnected from integrated server "+ dawnStarClient.clientUser);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
         this.connectionContext = channelHandlerContext;
         if (packet == null) packet = Unpooled.buffer();

         while (byteBuf.readableBytes() > 0) {
            if (chunkExpectedSize == -1 && byteBuf.readableBytes() >= 4) {
                chunkExpectedSize = byteBuf.readInt();
            }
            int bytesNeeded = chunkExpectedSize - packet.readableBytes();
            if (chunkExpectedSize != -1 && packet.readableBytes() + byteBuf.readableBytes() >= bytesNeeded) {
                packet.writeBytes(byteBuf, bytesNeeded);
                Chunk chunk = Codec.decodeChunk(packet);
                dawnStarClient.getChunkRender().buildChunk(chunk);
                dawnStarClient.getChunkRender().checkMeshChunks();
                // Reset for the next chunk.
                chunkExpectedSize = -1;
                packet.clear();
            } else {
                packet.writeBytes(byteBuf);
            }
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace(System.out);
    }

    public void sendGameProfile(){
        if (connectionContext.channel().isActive()){
            ByteBuf buffer = connectionContext.alloc().buffer();
            GameProfile gameProfile = new GameProfile("PhilipModDev");
            buffer.writeInt(RequestContent.USER_AUTH);
            buffer.writeBytes( gameProfile.getUsername().getBytes(StandardCharsets.UTF_8));
            connectionContext.writeAndFlush(buffer);
        }
    }

    public ChannelHandlerContext getConnectionContext() {
        return connectionContext;
    }

    public ByteBuf getPacket() {
        return packet;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public boolean isReceiveChunk() {
        return receiveChunk;
    }
}
