package com.lxp.iodemo.socketDemos;

import com.lxp.NioDemoConfig;
import com.lxp.util.IOUtil;
import com.lxp.util.Logger;
import com.lxp.util.Print;
import sun.rmi.runtime.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NioReceiveServer {
    private Charset charset = Charset.forName("UTF-8");

    static class Client{
        String fileName;
        long fileLength;
        long startTime;
        InetSocketAddress remoteAddress;
        FileChannel outChannel;
    }

    private ByteBuffer buffer = ByteBuffer.allocate(NioDemoConfig.SEND_BUFFER_SIZE);
    Map<SelectableChannel,Client> clientMap = new HashMap<SelectableChannel,Client>();

    public void startServer() throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocketChannel.configureBlocking(false);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(NioDemoConfig.SOCKET_SERVER_IP,NioDemoConfig.SOCKET_SERVER_PORT);
        serverSocket.bind(inetSocketAddress);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        Print.tcfo("serverChannel is listening...");
        while(selector.select()>0){
            Iterator<SelectionKey> selectionKeys = selector.selectedKeys().iterator();
            while (selectionKeys.hasNext()){
                SelectionKey key = selectionKeys.next();
                if (key.isAcceptable()){
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = server.accept();
                    socketChannel.configureBlocking(false);
                    SelectionKey selectionKey = socketChannel.register(selector,SelectionKey.OP_READ);
                    Client client = new Client();
                    client.remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
                    clientMap.put(socketChannel,client);
                    Logger.info(socketChannel.getRemoteAddress() + "连接成功");
                }else if (key.isReadable()){
                    processData(key);
                }
                selectionKeys.remove();
            }
        }
    }

    private  void processData(SelectionKey key){
        Client client = clientMap.get(key.channel());
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int num = 0;
        try {
            buffer.clear();
            while ((num = socketChannel.read(buffer)) > 0){
                buffer.flip();
                if (null == client.fileName) {
                    String fileName = charset.decode(buffer).toString();
                    String destPath = IOUtil.getResourcePath(NioDemoConfig.SOCKET_RECEIVE_PATH);
                    File directory = new File(destPath);
                    if (!directory.exists()) {
                        directory.mkdir();
                    }
                    client.fileName = fileName;
                    String fullName = directory.getAbsolutePath() + File.separatorChar + fileName;
                    Logger.info("NIO 传输目标文件：" + fullName);
                    File file = new File(fullName);
                    FileChannel fileChannel = new FileOutputStream(file).getChannel();
                    client.outChannel = fileChannel;
                }else if (0 == client.fileLength){
                    long fileLength = buffer.getLong();
                    client.fileLength = fileLength;
                    client.startTime = System.currentTimeMillis();
                    Logger.info("NIO 传输开始：");
                }else{
                    client.outChannel.write(buffer);
                }
                buffer.clear();
            }
            key.cancel();
        } catch (IOException e) {
            key.cancel();
            e.printStackTrace();
            return;
        }
        if(num == -1){
            IOUtil.closeQuietly(client.outChannel);
            Logger.info("上传完毕");
            key.cancel();
            Logger.info("文件接收成功，fileName:" + client.fileName);
            Logger.info("Size:" + IOUtil.getFormatFileSize(client.fileLength));
        }
    }

    public static void main(String[] args) throws IOException {
        NioReceiveServer server = new NioReceiveServer();
        server.startServer();
    }
}
