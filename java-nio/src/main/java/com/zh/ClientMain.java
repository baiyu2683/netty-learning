package com.zh;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientMain {

  public static void main(String[] args) throws InterruptedException, IOException {
    ExecutorService executorService = Executors.newFixedThreadPool(20);

    Selector selector = Selector.open();

    for (int i = 0 ; i < 1 ; i++) {
      try {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        // 注册当前socketchannel
        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        // 连接服务端
        socketChannel.connect(new InetSocketAddress(10091));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    while (true) {
      int num = selector.select(5000);
      System.out.println("事件数量: " + num);
      if (num < 0) {
        continue;
      }

      Set<SelectionKey> keySet = selector.selectedKeys();
      Iterator<SelectionKey> iterator = keySet.iterator();
      while (iterator.hasNext()) {
        SelectionKey key = iterator.next();
        iterator.remove();
        SocketChannel socketChannel = (SocketChannel) key.channel();
        if (key.isConnectable()) {
            while (true) {
              try {
                if (!socketChannel.finishConnect()) {
                  continue;
                }
                System.out.println("连接成功了" + socketChannel.isConnected());
              } catch (ConnectException e) {
                System.out.println("连接失败");
              }
              break;
            }
            try {
              ByteBuffer buffer = ByteBuffer.wrap("hello, I am client".getBytes(StandardCharsets.UTF_8));
              socketChannel.write(buffer);
              socketChannel.close();
            } catch (ClosedChannelException e) {
              // 服务端关闭
              e.printStackTrace();
            } catch (IOException e) {
              e.printStackTrace();
            }
        } else if (key.isReadable()) {
          System.out.println("读事件, 也就是对面的写");
        } else if (key.isWritable()) {
          System.out.println("写事件，也就是对面的读");
        }
      }
    }
  }
}
