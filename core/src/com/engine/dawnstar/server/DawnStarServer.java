package com.engine.dawnstar.server;

import com.engine.dawnstar.main.data.World;
import com.engine.dawnstar.utils.infrastructure.UserList;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import java.net.InetSocketAddress;
import java.util.function.Supplier;

public class DawnStarServer {
    public static final Logger LOGGER = LogManager.getLogger("["+ DawnStarServer.class.getName()+"]");
    public final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public final UserList userList;
    public World world;
    private Thread thread;
    private final ConnectionManager connectionManager;

    public DawnStarServer(){
        Configurator.setRootLevel(Level.INFO);
        userList = new UserList(this);
        connectionManager = new ConnectionManager(this);
    }

    public DawnStarServer(World world){
        this.world = world;
        connectionManager = new ConnectionManager(this);
        Configurator.setRootLevel(Level.INFO);
        userList = new UserList(this);
        Thread serverThread = new Thread(() -> {
            try(EventLoopGroup eventExecutors = new NioEventLoopGroup(1)) {
                InetSocketAddress address = new InetSocketAddress(3868);
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(eventExecutors);
                serverBootstrap.channel(NioServerSocketChannel.class);
                serverBootstrap.localAddress(address);
                serverBootstrap.childHandler(connectionManager.registerHandlers());
                LOGGER.info("DawnStarServer open on port: "+address.getPort());
                ChannelFuture channelFuture = serverBootstrap.bind().sync();
                channelFuture.channel().closeFuture().sync();
                LOGGER.info("DawnStarServer stopped");
            }catch (Exception exception){
                exception.printStackTrace(System.out);
            }
        });
        serverThread.setDaemon(true);
        if (Runtime.getRuntime().availableProcessors() >= 4) {
            LOGGER.info("Server thread running on high priority.");
            serverThread.setPriority(8);
        }
        serverThread.start();
        this.thread = serverThread;
    }

    public static <S extends DawnStarServer> S create(Supplier<S> supplier){
        return supplier.get();
    }

    public Thread getThread() {
        return thread;
    }

}
