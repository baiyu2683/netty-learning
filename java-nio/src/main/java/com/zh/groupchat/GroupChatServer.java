package com.zh.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class GroupChatServer {

  private Selector selector;
  private ServerSocketChannel listenChannel;
  private static final int PORT = 10090;

  public GroupChatServer() {
    try {
      selector = Selector.open();
      listenChannel = ServerSocketChannel.open();
      ServerSocket serverSocket = listenChannel.socket();
      serverSocket.bind(new InetSocketAddress(PORT));
      listenChannel.configureBlocking(false);
      listenChannel.register(selector, SelectionKey.OP_ACCEPT);
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("服务启动失败...");
    }
  }

  public void listen() {
    try {
      while (true) {
        int count = selector.select();
        if (count <= 0) {
          System.out.println("无事件，等待");
          continue;
        }
        Set<SelectionKey> selectionKeySet = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectionKeySet.iterator();
        while (iterator.hasNext()) {
          SelectionKey selectionKey = iterator.next();
          iterator.remove();
          if (!selectionKey.isValid()) {
            continue;
          }

          if (selectionKey.isAcceptable()) {
            SocketChannel socketChannel = listenChannel.accept();
            socketChannel.configureBlocking(false);
            // 监听读事件
            socketChannel.register(selector, SelectionKey.OP_READ);
            System.out.println(socketChannel.getRemoteAddress() + "上线了...");
          } else if (selectionKey.isReadable()) {
            handRead(selectionKey);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {

    }
  }

  private void handRead(SelectionKey selectionKey) {
    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    try {
      int count = socketChannel.read(buffer);
      // 异常关闭了
      if (count < 0) {
        System.out.println(socketChannel.getRemoteAddress() + "离线了.");
        socketChannel.close();
        return;
      }
      buffer.flip();
      String msg = new String(buffer.array(), buffer.position(), buffer.limit(), StandardCharsets.UTF_8);

      // 将消息转发给其他客户端
      transferMessage(msg, selectionKey);
    }catch (ClosedChannelException e) {
      try {
        System.out.println(socketChannel.getRemoteAddress() + "离线了");
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
      try {
        if (socketChannel.isOpen()) {
          socketChannel.close();
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }

  /**
   * 将消息发送给其他客户端
   */
  public void transferMessage(String msg, SelectionKey client) {
    System.out.println("服务器转发消息");
    Set<SelectionKey> keySet = selector.keys();
    // 去掉消息发来的那个客户端自己, 这个set不能修改，不能从这里删除.
//    keySet.remove(client);
    for (SelectionKey key : keySet) {
      if (key == client) {
        continue;
      }
      SelectableChannel channel = key.channel();
      if (channel instanceof SocketChannel) {
        SocketChannel socketChannel = (SocketChannel) channel;
        try {
          socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
          e.printStackTrace();
          try {
            System.out.println("转发失败.." + socketChannel.getRemoteAddress() + "离线了");
            key.cancel();
            channel.close();
          } catch (IOException ex) {
            ex.printStackTrace();
          }
        }
      }
    }
  }

  public static void main(String[] args) {
    GroupChatServer groupChatServer = new GroupChatServer();
    groupChatServer.listen();
  }
}
