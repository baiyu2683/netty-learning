package com.zh;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * java nio server端例子
 */
public class ServerMain {

  private static final ThreadPoolExecutor exector = new ThreadPoolExecutor(10, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10));

  private static Selector selector;
  private static AtomicInteger bugLoop = new AtomicInteger(0);

  public static void main(String[] args) throws IOException {
    // 创建selector
    selector = Selector.open();

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
          // 监听到写事件, 也就是底层写缓冲区空闲。一般都是空闲的，所以一般不注册此事件
          System.out.println("write");
          handleWrite(selector, key);
        } else if (key.isReadable()) {
          // 监听到读事件。底层缓冲区有数据可读
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

    System.out.println("attachment: " + key.attachment());
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    try {
      // 当服务端读数据，客户端此时异常关闭，会报如下错误
      // Exception in thread "main" java.io.IOException: Connection reset by peer
      int count = socketChannel.read(buffer);
      // 关闭了
      if (count < 0) {
        socketChannel.close();
        return;
      }
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("客户端异常关闭");
      socketChannel.close();
      return;
    }

    buffer.flip();
    System.out.println("收到数据: " + new String(buffer.array(), 0, buffer.limit(), StandardCharsets.UTF_8));

    // 注册一个写事件。用来写数据
    ByteBuffer respBuffer = ByteBuffer.wrap(("服务端响应" + Thread.currentThread().getId()).getBytes(StandardCharsets.UTF_8));
    socketChannel.register(selector, SelectionKey.OP_WRITE, respBuffer);
  }

  private static void handleWrite(Selector selector, SelectionKey key) throws IOException {
    SocketChannel socketChannel = (SocketChannel) key.channel();
    Object attachment = key.attachment();
    if (attachment instanceof ByteBuffer) {
      ByteBuffer buffer = (ByteBuffer) attachment;
      while (buffer.hasRemaining()) {
        socketChannel.write(buffer);
      }
      key.attach(null);
    }
    // 取消写事件
    key.interestOps(key.interestOps() & ~ SelectionKey.OP_WRITE);
  }
}
