package com.lxp.netty.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class IntegerAddDecoder extends ReplayingDecoder<IntegerAddDecoder.Status> {

    enum Status{
        Parse_1,Parse_2
    }

    private int first;
    private int second;

    public IntegerAddDecoder(){
        super(Status.Parse_1);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        switch (state()){
            case Parse_1:
                //从装饰器ByteBuf读取数据
                first = byteBuf.readInt();
                //第一步解析成功
                //进入第二步，并且设置“读断点指针”为当前的读取位置
                checkpoint(Status.Parse_2);
                break;
            case Parse_2:
                second = byteBuf.readInt();
                Integer sum = first + second;
                list.add(sum);
                checkpoint(Status.Parse_1);
                break;
            default:
                break;
        }
    }
}
