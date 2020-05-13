package com.lxp.netty.echoServer;

import com.lxp.util.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

@ChannelHandler.Sharable
public class NettyEchoClientHandler extends ChannelInboundHandlerAdapter {
    public static final NettyEchoClientHandler INSTANCE = new NettyEchoClientHandler();

    /**
     *  出战处理方法
     * @param ctx 上下文
     * @param msg 入站数据包
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf)msg;
        int len = byteBuf.readableBytes();
        byte[] arr = new byte[len];
        byteBuf.getBytes(0, arr);
        Logger.info("client received: " + new String(arr, StandardCharsets.UTF_8));
        //释放ByteBuf的两种方法
        //方法一:
        byteBuf.release();
        //方法二：调用父类的入站方法
        //super.channelRead(ctx, msg);
    }
}
