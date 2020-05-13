package com.lxp.netty.echoServer;

import com.lxp.netty.NettyDemoConfig;
import com.lxp.util.Logger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyEchoServer {
    public void runServer() throws InterruptedException {
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerLoopGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossLoopGroup,workerLoopGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(NettyDemoConfig.SOCKET_SERVER_PORT)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        b.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                //流水线管理子通道中的handler处理器
                //向子通道柳树西安提娜佳一个handler业务处理器
                socketChannel.pipeline().addLast(NettyEchoServerHandler.INSTANCE);
            }
        });
        ChannelFuture channelFuture = b.bind();
        channelFuture.addListener((ChannelFuture futureListener) ->{
            if(futureListener.isSuccess()){
                Logger.info("EchoServer服务端启动成功过!");
            }else{
                Logger.info("EchoServer服务端启动失败!");
            }
        });
        channelFuture.sync();
        workerLoopGroup.shutdownGracefully();
        bossLoopGroup.shutdownGracefully();
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyEchoServer().runServer();
    }
}
