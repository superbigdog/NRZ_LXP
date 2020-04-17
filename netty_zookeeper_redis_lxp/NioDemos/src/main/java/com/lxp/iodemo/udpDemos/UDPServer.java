package com.lxp.iodemo.udpDemos;

import com.lxp.NioDemoConfig;
import com.lxp.util.Print;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class UDPServer {
    public void receive(){
       try{
           //获取DatagramChannel数据报通道
           DatagramChannel datagramChannel = DatagramChannel.open();
           //设置为非阻塞
           datagramChannel.configureBlocking(false);
           //绑定监听地址，这是与client相区别的地方
           datagramChannel.bind(
                   new InetSocketAddress(NioDemoConfig.SOCKET_SERVER_IP,NioDemoConfig.SOCKET_SERVER_PORT));
           //开启一个通道选择器
           Selector selector = Selector.open();
           datagramChannel.register(selector, SelectionKey.OP_READ);
           //通过选择器，查询IO事件
           while(selector.select()>0){
               Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
               ByteBuffer buffer = ByteBuffer.allocate(NioDemoConfig.SEND_BUFFER_SIZE);
               //迭代IO事件
               while(iterator.hasNext()){
                   SelectionKey selectionKey = iterator.next();
                   //可读事件，有数据到来
                   if(selectionKey.isReadable()) {
                       //读取DatagramChannel数据报通道的数据
                       SocketAddress client = datagramChannel.receive(buffer);
                       buffer.flip();
                       Print.tcfo("来自" + client.toString() + "的信息： "
                               + new String(buffer.array(),0,buffer.limit()));
                       buffer.clear();
                   }
               }
               iterator.remove();
           }
           //关闭选择器和通道
           selector.close();
           datagramChannel.close();
       } catch (IOException e) {
           e.printStackTrace();
       }
    }

    public static void main(String[] args) {
        new UDPServer().receive();
    }
}
