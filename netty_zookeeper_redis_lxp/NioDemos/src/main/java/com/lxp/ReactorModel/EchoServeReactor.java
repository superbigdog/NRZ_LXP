package com.lxp.ReactorModel;

import com.lxp.NioDemoConfig;
import com.lxp.util.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class EchoServeReactor implements Runnable{
    Selector selector ;
    ServerSocketChannel serverSocket;
    EchoServeReactor() throws IOException {
        selector = Selector.open();
        serverSocket = ServerSocketChannel.open();
        InetSocketAddress address = new InetSocketAddress(NioDemoConfig.SOCKET_SERVER_PORT);
        serverSocket.bind(address);
        serverSocket.configureBlocking(false);
        SelectionKey sk = serverSocket.register(selector,SelectionKey.OP_ACCEPT);
        sk.attach(new AcceptorHandler());
    }

    @Override
    public void run() {
        try{
            int n = 0;
            while (!Thread.interrupted()){
                Logger.info(n++);
                //这里除过返回一个int值之外，还要生成SelectionKey的set
                if (selector.select()>0){
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> it = selectionKeys.iterator();
                    while(it.hasNext()){
                        //Reactor负责dispatch收到的事件
                        SelectionKey key = it.next();
                        dispatch(key);
                    }
                    selectionKeys.clear();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void dispatch(SelectionKey key){
        Runnable handlor = (Runnable) key.attachment();
        if (handlor != null){
            handlor.run();
        }
    }

    class AcceptorHandler implements Runnable{

        @Override
        public void run() {
            try{
                SocketChannel socket = serverSocket.accept();
                new EchoHedlor(selector,socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Thread(new EchoServeReactor()).start();
    }
}
