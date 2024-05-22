package com.engine.dawnstar.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;

public final class ConnectionManager {
    private final ServerMessageDecoder serverMessageDecoder;
    private final GameProfileHandler gameProfileHandler;
    private final WorldChunkHandler worldChunkHandler;

    public ConnectionManager(DawnStarServer dawnStarServer){
        serverMessageDecoder = new ServerMessageDecoder();
        gameProfileHandler = new GameProfileHandler();
        worldChunkHandler = new WorldChunkHandler(dawnStarServer);
    }

   public ChannelInitializer<?> registerHandlers(){
        return new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addFirst(serverMessageDecoder);
                ch.pipeline().addLast(gameProfileHandler);
                ch.pipeline().addLast(worldChunkHandler);
            }
        };
   }
}
