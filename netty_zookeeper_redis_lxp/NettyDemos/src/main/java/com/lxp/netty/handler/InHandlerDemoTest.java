package com.lxp.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

public class InHandlerDemoTest {

    @Test
    public void testInHandlerLifeTester(){
        final InHandlerDemo inHandlerDemo = new InHandlerDemo();
        //初始化处理器
        ChannelInitializer initializer = new ChannelInitializer<EmbeddedChannel>() {

            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(inHandlerDemo);
            }
        };

        EmbeddedChannel channel = new EmbeddedChannel(initializer);
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(1);
        //模拟入栈，再写一个入栈数据包
        channel.writeInbound(buf);
        channel.finish();
        //通道关闭
        try{
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
