package com.lxp.iodemo.NioDsiacard;

import com.lxp.NioDemoConfig;
import com.lxp.util.Logger;
import com.lxp.util.Print;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioDiscardServer {

    public static void startServer() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(NioDemoConfig.SOCKET_SERVER_IP,NioDemoConfig.SOCKET_SERVER_PORT));
        Logger.info("服务器启动成功");
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (selector.select()>0){
            Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();
            while (selectionKeys.hasNext()){
                SelectionKey selectionKey = selectionKeys.next();
                if (selectionKey.isAcceptable()){
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector,SelectionKey.OP_READ);
                }else if (selectionKey.isReadable()){
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    int lenth = 0;
                    while((lenth = socketChannel.read(byteBuffer)) > 0){
                        byteBuffer.flip();
                        Logger.info(new String(byteBuffer.array(),0,lenth));
                        byteBuffer.clear();
                    }
                    socketChannel.close();
                }
                selectionKeys.remove();
            }
        }
        serverSocketChannel.close();
    }

    public static void main(String[] args) throws IOException {
        startServer();
    }

}
