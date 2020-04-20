package com.lxp.iodemo.NioDsiacard;

import com.lxp.NioDemoConfig;
import com.lxp.util.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioDiscardclient {
    public static void startClient() throws IOException {
        InetSocketAddress address = new InetSocketAddress(NioDemoConfig.SOCKET_SERVER_IP,NioDemoConfig.SOCKET_SERVER_PORT);
        SocketChannel socketChannel = SocketChannel.open(address);
        socketChannel.configureBlocking(false);
        while(!socketChannel.finishConnect()){

        }
        Logger.info("客户端连接成功");
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.put("Hello World!!!".getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        socketChannel.shutdownOutput();
        socketChannel.close();

    }

    public static void main(String[] args) throws IOException {
        startClient();
    }
}
