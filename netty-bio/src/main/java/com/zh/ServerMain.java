package com.zh;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServerMain {

  private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10)) {
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
      System.out.println("发生了异常");
      t.printStackTrace();
    }
  };

  public static void main(String[] args) throws IOException {
    ServerSocket serverSocket = new ServerSocket(10090);
    while (true) {
      Socket socket = serverSocket.accept();
      System.out.println("获得了一个链接: " + socket.getInetAddress());
      executor.execute(() -> {
        handlSocker(socket);
      });
    }
  }

  private static void handlSocker(Socket socket) {
    System.out.println("线程id: " + Thread.currentThread().getId());
    ByteArrayOutputStream data = new ByteArrayOutputStream();
    try {
      InputStream inputStream = socket.getInputStream();
      byte[] buffer = new byte[1024];
      int len = 0;
      while ((len = inputStream.read(buffer)) > 0) {
        System.out.println("长度: " + len);
        data.write(buffer, 0, len);
      }
      String message = new String(data.toByteArray());
      System.out.println("收到了消息: " + message);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
