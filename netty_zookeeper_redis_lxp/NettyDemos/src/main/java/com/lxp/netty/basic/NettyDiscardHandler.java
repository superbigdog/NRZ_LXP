package com.lxp.netty.basic;

import com.lxp.util.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class NettyDiscardHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        try{
            Logger.info("收到消息，丢弃如下：");
            while(in.isReadable()){
                Logger.info((char)in.readByte());
            }
            Logger.info("\n");
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
