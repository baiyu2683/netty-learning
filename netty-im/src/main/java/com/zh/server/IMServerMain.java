package com.zh.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class IMServerMain {

  public static void main(String[] args) {
    EventLoopGroup boss = new NioEventLoopGroup(1);
    EventLoopGroup worker = new NioEventLoopGroup();

    try {
      ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap.group(boss, worker)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .childHandler(new IMChannelInitializer());
      ChannelFuture channelFuture = serverBootstrap.bind(10090).sync();
      channelFuture.addListener(future -> {
        if (future.isSuccess()) {
          System.out.println("绑定成功");
        } else {
          System.out.println("绑定失败");
        }
      });
      channelFuture.channel().closeFuture().sync();
    } catch (Exception e) {
      e.printStackTrace();
      boss.shutdownGracefully();
      worker.shutdownGracefully();
    }
  }
}
