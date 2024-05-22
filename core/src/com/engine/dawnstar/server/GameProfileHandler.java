package com.engine.dawnstar.server;

import com.engine.dawnstar.main.data.GameProfile;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@ChannelHandler.Sharable
public class GameProfileHandler extends SimpleChannelInboundHandler<GameProfile> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GameProfile gameProfile) throws Exception {
        System.out.println(gameProfile.getUsername());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace(System.out);
        ctx.disconnect();
    }
}
