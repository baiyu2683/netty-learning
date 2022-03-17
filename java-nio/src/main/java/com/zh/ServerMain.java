package com.zh;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
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
    serverSocketChannel.register(selector, SelectionKey.OP_CONNECT);

    while (true) {
      int num = selector.select();
      System.out.println("事件个数: " + num);
      if (num <= 0) {
        continue;
      }
      Set<SelectionKey> keySet = selector.selectedKeys();
      System.out.println("有事件了: " + keySet.size());
      for (SelectionKey key : keySet) {
        int ops = key.interestOps();
        if (key.isConnectable()) {
          // 处理connect事件
          System.out.println("connect");
          // 下一步监听accept时间
          serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } else if (key.isAcceptable()) {
          // 处理accept事件
          System.out.println("accept");
        } else if (key.isWritable()) {
          // 监听到写事件
          System.out.println("write");
        } else if (key.isReadable()) {
          // 监听到读事件。服务端会有这个
          System.out.println("read");
        }
      }
    }
  }
}
