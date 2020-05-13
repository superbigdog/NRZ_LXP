package com.lxp.ReactorModel;

import com.lxp.NioDemoConfig;
import com.lxp.util.Logger;
import com.lxp.util.Print;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class EchoClient {

    public void start() throws IOException {
        InetSocketAddress address =
                new InetSocketAddress(NioDemoConfig.SOCKET_SERVER_IP,
                        NioDemoConfig.SOCKET_SERVER_PORT);
        //1.获取通道（channel）
        SocketChannel socketChannel = SocketChannel.open(address);
        //2.切换成非阻塞模式
        socketChannel.configureBlocking(false);
        //3.自旋等待连接完成
        while (!socketChannel.finishConnect()){

        }
        Logger.info("客户端启动成功");
        //启动接受线程
        Processer processer = new Processer(socketChannel);
        new Thread(processer).start();
    }

    static class Processer implements Runnable{
        final Selector selector;
        final SocketChannel channel;

        Processer(SocketChannel channel) throws IOException {
            selector = Selector.open();
            this.channel = channel;
            channel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
        }

        @Override
        public void run() {
            try{
                while(!Thread.interrupted()){
                    selector.select();
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> it = selectionKeys.iterator();
                    while (it.hasNext()){
                        SelectionKey key = it.next();

                        if (key.isWritable()){
                            ByteBuffer buffer = ByteBuffer.allocate(NioDemoConfig.SEND_BUFFER_SIZE);
                            Scanner scanner = new Scanner(System.in);
                            Print.tcfo("请输入发送内容：");
                            if (scanner.hasNext()){
                                SocketChannel socketChannel = (SocketChannel) key.channel();
                                String next = scanner.next();
                                buffer.put(next.getBytes());
                                //发送数据
                                buffer.flip();
                                socketChannel.write(buffer);
                                buffer.clear();
                            }
                        }
                        if (key.isReadable()){
                            SocketChannel socketChannel = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int length = 0;
                            while ((length = socketChannel.read(buffer)) > 0){
                                buffer.flip();
                                Logger.info("server echo:" + new String(buffer.array(),0,length));
                                buffer.clear();
                            }
                        }
                    }
                    selectionKeys.clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new EchoClient().start();
    }
}
