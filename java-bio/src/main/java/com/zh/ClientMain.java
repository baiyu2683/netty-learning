package com.zh;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientMain {

  public static void main(String[] args) throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(21);
    for (int i = 0 ; i < 21 ; i++) {
      executorService.execute(() -> {
        Socket socket = new Socket();
        try {
          socket.connect(new InetSocketAddress(10090));
        } catch (IOException e) {
          e.printStackTrace();
        }
        while (true) {
          try {
            // 链接
            OutputStream outputStream = socket.getOutputStream();
            String message = "hello, " + Thread.currentThread().getId();
            outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

            TimeUnit.SECONDS.sleep(10);
          } catch (IOException e) {
            e.printStackTrace();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      });
    }
    new CountDownLatch(1).await();
  }
}
