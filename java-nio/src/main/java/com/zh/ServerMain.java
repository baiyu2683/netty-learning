package com.zh;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;

/**
 * java nio server端例子
 */
public class ServerMain {

  private static final ThreadPoolExecutor exector = new ThreadPoolExecutor(10, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

  public static void main(String[] args) throws IOException {
    // 创建selector
    Selector selector = Selector.open();

    // 创建serversocketChannel
    ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
    serverSocketChannel.configureBlocking(false);
    // 绑定端口
    ServerSocket serverSocket = serverSocketChannel.socket();
    serverSocket.bind(new InetSocketAddress(10090));

    // 将serversocketchannel注册到selector中，监听accept事件
    // ServerScoketChannel只能监听到Accept事件
    // ScoketChannel可以监听Connect   read   write事件
    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

    while (true) {
      // select方法仅仅当有事件发生的时候才会返回。
      // 设置超时时间1s
      int num = selector.select(5000);
      System.out.println("事件个数: " + num);
      if (num <= 0) {
        continue;
      }
      Set<SelectionKey> keySet = selector.selectedKeys();
      System.out.println("有事件了: " + keySet.size());
      Iterator<SelectionKey> iterator = keySet.iterator();
      while (iterator.hasNext()) {
        SelectionKey key = iterator.next();
        // 移除当前key
        iterator.remove();
        if (key.isAcceptable()) {
          // 处理accept事件，ServerSocket第一次肯定是accept，之后才会有socket, socket可以监听读写事件。
          System.out.println("accept");
          key.attach("serverSocketChannel的附带内容");

          hanleAccept(selector, key);
        } else if (key.isConnectable()) {
          // 处理connect事件
          System.out.println("connect");
        } else  if (key.isWritable()) {
          // 监听到写事件, 也就是对面的读事件
          System.out.println("write");

        } else if (key.isReadable()) {
          // 监听到读事件。也就是对面的写事件
          System.out.println("read");
          handleRead(selector, key);
        }
      }
    }
  }

  private static void hanleAccept(Selector selector, SelectionKey key) throws IOException {
    // 获得一个客户端连接
    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
    // accept client socket
    SocketChannel socketChannel = serverSocketChannel.accept();
    socketChannel.configureBlocking(false);
    // 客户端连接监听读事件
    socketChannel.register(selector, SelectionKey.OP_READ, "附带内容");
  }

  private static void handleRead(Selector selector, SelectionKey key) throws IOException {
    // 获得一个客户端连接
    SocketChannel socketChannel = (SocketChannel) key.channel();

    // TODO 如何判断客户端异常关闭，在客户端异常关闭之后，下面这些方法都是true
    System.out.println(socketChannel.isConnected() + "-" + socketChannel.isOpen() + "-" + key.isReadable() + "-" + key.isValid());

    System.out.println("attachment: " + key.attachment());
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    socketChannel.read(buffer);
    buffer.flip();
    System.out.println("收到数据: " + new String(buffer.array(), 0, buffer.limit(), StandardCharsets.UTF_8));
  }
}
