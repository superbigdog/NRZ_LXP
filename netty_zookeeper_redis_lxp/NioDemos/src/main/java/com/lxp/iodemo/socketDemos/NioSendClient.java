package com.lxp.iodemo.socketDemos;

import com.lxp.NioDemoConfig;
import com.lxp.util.IOUtil;
import com.lxp.util.Logger;
import sun.rmi.runtime.Log;

import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class NioSendClient {
    private Charset charset = Charset.forName("UTF-8");

    public void sendFile(){
        try{
            String sourcePath = NioDemoConfig.SOCKET_SEND_FILE;
            String srcPath = IOUtil.getResourcePath(sourcePath);
            Logger.info("srcPath=" + srcPath);
            String destFile = NioDemoConfig.SOCKET_RECEIVE_FILE;
            Logger.info("destFile=" + destFile);
            File file = new File(srcPath);
            if (!file.exists()){
                Logger.info(srcPath + "文件不存在");
                return;
            }
            FileChannel fileChannel = new FileInputStream(file).getChannel();
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.socket().connect(
                    new InetSocketAddress(NioDemoConfig.SOCKET_SERVER_IP,NioDemoConfig.SOCKET_SERVER_PORT));
            socketChannel.configureBlocking(false);
            while(!socketChannel.finishConnect()){

            }
            Logger.info("Client成功连接服务器段");
            ByteBuffer fileNameByteBuffer = charset.encode(destFile);
            socketChannel.write(fileNameByteBuffer);
            ByteBuffer buffer = ByteBuffer.allocate(NioDemoConfig.SEND_BUFFER_SIZE);
            buffer.putLong(file.length());
            buffer.flip();
            socketChannel.write(buffer);
            buffer.clear();
            //发送文件内容
            Logger.info("开始传输文件");
            int length = 0;
            long progress = 0;
            while((length = fileChannel.read(buffer))>0){
                buffer.flip();
                socketChannel.write(buffer);
                buffer.clear();
                progress += length;
                Logger.info("| " + (100 * progress / file.length()) + "% |");
            }
            if(length == -1){
                IOUtil.closeQuietly(fileChannel);
                //在SocketChannel传输通道关闭前，尽量发送一个传输结束的标志
                socketChannel.shutdownOutput();
                IOUtil.closeQuietly(socketChannel);
            }
            Logger.info("==========文件传输成功============");
        }catch(Exception e){}
    }

    public static void main(String args[]){
        NioSendClient client = new NioSendClient();
        client.sendFile();
    }
}
