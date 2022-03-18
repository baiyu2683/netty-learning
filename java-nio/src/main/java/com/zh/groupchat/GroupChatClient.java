package com.zh.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GroupChatClient {

  private final String HOST = "127.0.0.1";
  private final int PORT = 10090;
  private Selector selector;
  private SocketChannel socketChannel;
  private String username;

 public GroupChatClient() {
   try {
     selector = Selector.open();
     socketChannel = SocketChannel.open();
     socketChannel.connect(new InetSocketAddress(HOST, PORT));

     socketChannel.configureBlocking(false);
     socketChannel.register(selector, SelectionKey.OP_READ);
     username = System.currentTimeMillis() + "";
   } catch (IOException e) {
     e.printStackTrace();
   }
 }

 public void sendMsg(String msg) {
   msg = username + " - " + msg;
   try {
     socketChannel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
   } catch (IOException e) {
     e.printStackTrace();
   }
 }

 public void readMsg() {
   try {
     int count = selector.select();
     if (count <= 0) {
       return;
     }
     Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
     while (iterator.hasNext()) {
       SelectionKey selectionKey = iterator.next();
       iterator.remove();
       if (selectionKey.isReadable()) {
         SocketChannel sc = (SocketChannel) selectionKey.channel();
         ByteBuffer buffer = ByteBuffer.allocate(1024);
         int c = sc.read(buffer);
         if (c <= 0) {
           System.out.println("已经关闭");
           sc.close();
           continue;
         }
         buffer.flip();
         String msg = new String(buffer.array(), buffer.position(), buffer.limit(), StandardCharsets.UTF_8);
         System.out.println("收到消息: " + msg);
       }
     }
   } catch (IOException e) {
     e.printStackTrace();
   }
 }

  public static void main(String[] args) {
    GroupChatClient chatClient = new GroupChatClient();
    new Thread(() -> {
      while (true) {
        chatClient.readMsg();
        try {
          TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }).start();
    Scanner scanner = new Scanner(System.in);
    while (scanner.hasNext()) {
      String msg = scanner.nextLine();
      chatClient.sendMsg(msg);
    }
  }
}
