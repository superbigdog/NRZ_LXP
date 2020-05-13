package com.lxp.ReactorModel;

import com.lxp.util.Logger;
import com.lxp.util.ReflectionUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class EchoHedlor implements Runnable{
    final SocketChannel channel;
    final SelectionKey sk;
    final ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
    static final int RECIEVING = 0, SENDING = 1;
    int state = RECIEVING;

    public EchoHedlor(Selector selector,SocketChannel channel) throws IOException {
        this.channel = channel;
        channel.configureBlocking(false);
        this.sk = channel.register(selector,0);
        sk.attach(this);
        sk.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    @Override
    public void run() {
        try{
            if(state == RECIEVING){
                //从通道读
                int length = 0;
                while ((length = channel.read(byteBuffer)) > 0){
                    Logger.info("client msg:" + new String(byteBuffer.array(),0,length));
                }
                //读完后，准备开始写入通道，byteBuffer切换成读取模式
                byteBuffer.flip();
                //读完后注册write事件
                sk.interestOps(SelectionKey.OP_WRITE);
                //读完后，进入发送额状态
                state = SENDING;
            }
            if (state == SENDING){
                //写入通道
                channel.write(byteBuffer);
                //写完后，准备开始从通道读，beteBuffer切换写入模式
                byteBuffer.clear();
                //写完后，注册read就绪事件
                sk.interestOps(SelectionKey.OP_READ);
                state = RECIEVING;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
