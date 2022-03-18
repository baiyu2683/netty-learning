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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientMain {

  public static void main(String[] args) throws IOException {
    ExecutorService executorService = Executors.newFixedThreadPool(20);

    Selector selector = Selector.open();

    for (int i = 0 ; i < 1 ; i++) {
      try {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        // 注册当前socketchannel
        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        // 连接服务端
        socketChannel.connect(new InetSocketAddress(10090));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    AtomicBoolean connected = new AtomicBoolean(false);
    Object connectedLock = new Object();
    while (true) {
      // 这里为什么一直直接返回。。
      int num = selector.select(5000);
      if (num <= 0) {
        continue;
      }

      Set<SelectionKey> keySet = selector.selectedKeys();
      Iterator<SelectionKey> iterator = keySet.iterator();
      while (iterator.hasNext()) {
        SelectionKey key = iterator.next();
        iterator.remove();
        SocketChannel socketChannel = (SocketChannel) key.channel();
        if (key.isConnectable()) {
          // 多线程情况下，由于是异步执行，可能在线程中未注册其他事件之前，外部的循环就重新执行了此任务了。
          // 会出现多个线程处理人物的情况。
          executorService.execute(() -> {
            synchronized (connectedLock) {
              if (connected.get()) {
                return;
              }
              extracted(selector, key, connected);
            }
          });
        } else if (key.isReadable()) {
          System.out.println("读事件, 缓冲区有数据可读");
          ByteBuffer buffer = ByteBuffer.allocate(1024);
          socketChannel.read(buffer);
          buffer.flip();
          System.out.println("客户端收到数据: " + new String(buffer.array(), buffer.position(), buffer.limit(), StandardCharsets.UTF_8));
        } else if (key.isWritable()) {
          System.out.println("写事件，写缓冲区空闲，可以写数据");
          handWrite(selector, key);
        }
      }
    }
  }

  private static void extracted(Selector selector, SelectionKey key, AtomicBoolean connected) {
    while (true) {
      try {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        if (!socketChannel.finishConnect()) {
          continue;
        }
        // 连接标记
        connected.set(true);
        System.out.println("连接成功了" + socketChannel.isConnected() + "-" + Thread.currentThread().getId());
        ByteBuffer buffer = ByteBuffer.wrap(("客户端打招呼" + Thread.currentThread().getId()).getBytes(StandardCharsets.UTF_8));
        socketChannel.register(selector, SelectionKey.OP_WRITE, buffer);
      } catch (ConnectException e) {
        System.out.println("连接失败");
      } catch (ClosedChannelException e) {
        e.printStackTrace();
        System.out.println("连接已关闭");
      } catch (IOException e) {
        e.printStackTrace();
      }
      break;
    }
  }

  private static void handWrite(Selector selector, SelectionKey key) {
    try {
      SocketChannel socketChannel = (SocketChannel) key.channel();
      ByteBuffer buffer = (ByteBuffer) key.attachment();
      socketChannel.write(buffer);
      key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
      socketChannel.register(selector, SelectionKey.OP_READ);
    } catch (ClosedChannelException e) {
      // 服务端关闭
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {

    }
  }
}
