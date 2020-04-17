package com.lxp.iodemo.udpDemos;

import com.lxp.NioDemoConfig;
import com.lxp.util.Dateutil;
import com.lxp.util.Print;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;

public class UDPClient {

    public void send(){
        try{//获取DatagramChannel数据报通报
            DatagramChannel dChannel = DatagramChannel.open();
            //设置为非阻塞
            dChannel.configureBlocking(false);
            ByteBuffer buffer = ByteBuffer.allocate(NioDemoConfig.SEND_BUFFER_SIZE);
            Scanner scanner = new Scanner(System.in);
            Print.tcfo("UDP客户端启动成功！");
            Print.tcfo("请输入发送内容：");
            while(scanner.hasNext()){
                String next = scanner.next();
                buffer.put((Dateutil.getNow() + ">>" + next).getBytes());
                buffer.flip();
                dChannel.send(buffer,
                        new InetSocketAddress(NioDemoConfig.SOCKET_SERVER_IP,NioDemoConfig.SOCKET_SERVER_PORT));
                buffer.clear();
            }
            dChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new UDPClient().send();
    }
}
