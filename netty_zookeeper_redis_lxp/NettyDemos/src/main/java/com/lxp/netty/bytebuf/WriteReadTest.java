package com.lxp.netty.bytebuf;

import com.lxp.util.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import org.junit.Test;

import java.nio.Buffer;

public class WriteReadTest {

    @Test
    public void testWriteRead(){
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(9,100);
        Logger.info("动作： 分配ByteBuf(9, 100)", buffer);
        buffer.writeBytes(new byte[]{1, 2, 3, 4});
        Logger.info("动作： 写入4个字节（1， 2，3， 4）", buffer);
        Logger.info("==========get===========");
        getByteBuf(buffer);
        Logger.info("===========read=========");
        readByteBuf(buffer);
    }

    //取字节
    private void readByteBuf(ByteBuf buffer){
        while (buffer.isReadable()){
            Logger.info("取一个字节：" + buffer.readByte());
        }
    }

    //读字节，不改变指针
    private void getByteBuf(ByteBuf buffer){
        for (int i = 0; i < buffer.readableBytes(); i++){
            Logger.info("读一个字节：" + buffer.getByte(i));
        }
    }
}
