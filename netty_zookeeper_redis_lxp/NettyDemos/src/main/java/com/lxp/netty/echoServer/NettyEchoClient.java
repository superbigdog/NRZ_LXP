package com.lxp.netty.echoServer;


import com.lxp.netty.NettyDemoConfig;
import com.lxp.util.Dateutil;
import com.lxp.util.Logger;
import com.lxp.util.Print;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class NettyEchoClient {
    private int serverPort;
    private String serverIp;
    Bootstrap b = new Bootstrap();

    public NettyEchoClient(String ip, int port){
        this.serverIp = ip;
        this.serverPort = port;
    }

    private void runClient(){
        //创建反应器线程组
        EventLoopGroup workerLoopGroup = new NioEventLoopGroup();
        try{
            //1 设置反应组 线程组
            b.group(workerLoopGroup);
            //2 设置nio类型的通道
            b.channel(NioSocketChannel.class);
            //3 设置监听端口
            b.remoteAddress(serverIp, serverPort);
            //4 设置通道的参数
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            //5 装配子通道流水线
            b.handler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(NettyEchoClientHandler.INSTANCE);
                }
            });
            ChannelFuture f = b.connect();
            f.addListener((ChannelFuture futureListener) ->
            {
                if (futureListener.isSuccess()) {
                    Logger.info("EchoClient客户端连接成功!");

                } else {
                    Logger.info("EchoClient客户端连接失败!");
                }
            });
            f.sync();
            Channel channel =f.channel();
            Scanner scanner = new Scanner(System.in);
            Print.tcfo("请输入发送内容：");
            while (scanner.hasNext()){
                //获取输入的内容
                String next = scanner.next();
                byte[] bytes = (Dateutil.getNow() + " >> " + next).getBytes(StandardCharsets.UTF_8);
                //发送ByteBuf
                ByteBuf buffer = channel.alloc().buffer();
                buffer.writeBytes(bytes);
                channel.writeAndFlush(buffer);
                Print.tcfo("请输入发送内容：");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //从容关闭EventLoopGroup
            //释放掉苏哟有资源，包括创建的线程
                workerLoopGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyEchoClient(NettyDemoConfig.SOCKET_SERVER_IP, NettyDemoConfig.SOCKET_SERVER_PORT).runClient();
    }
}
