package com.lxp.netty.echoServer;

import com.lxp.util.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

public class NettyEchoServerHandler extends ChannelInboundHandlerAdapter {
    public static final NettyEchoServerHandler INSTANCE = new NettyEchoServerHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf)msg;
        Logger.info("msg type: " + (in.hasArray() ? "堆内存" : "直接内存"));
        int len = in.readableBytes();
        byte[] arr = new byte[len];
        in.getBytes(0,arr);
        Logger.info("server received: " + new String(arr, StandardCharsets.UTF_8));
        Logger.info("写回前， msg.refCnt: " + ((ByteBuf)msg).refCnt());
        //写回数据，异步任务
        ChannelFuture f = ctx.writeAndFlush(msg);
        f.addListener((ChannelFutureListener) ->{
            Logger.info("写回后，msg.refCnt: " + ((ByteBuf)msg).refCnt());
        });
    }
}
