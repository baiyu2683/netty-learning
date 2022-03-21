package com.zh.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Scanner;

public class IMClientMain {

  public static void main(String[] args) {
    EventLoopGroup worker = new NioEventLoopGroup();
    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(worker)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .handler(new ClientChannelInitializer());
      ChannelFuture future = bootstrap.connect("127.0.0.1", 10090).sync();
      future.addListener(future1 -> {
        if (future.isSuccess()) {
          System.out.println("链接成功");
          new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
              String content = scanner.nextLine();
              future.channel().writeAndFlush(content);
            }
          }).start();
        } else {
          System.out.println("链接失败");
        }
      });


      future.channel().closeFuture().sync();
    } catch (Exception e) {
      e.printStackTrace();
      worker.shutdownGracefully();
    }
  }
}
