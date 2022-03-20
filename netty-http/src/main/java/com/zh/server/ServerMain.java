package com.zh.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerMain {

  public static void main(String[] args) {
    EventLoopGroup boss = new NioEventLoopGroup();
    EventLoopGroup worker = new NioEventLoopGroup();

    try{
      ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap.group(boss, worker)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .childHandler(new ServerInitializer());

      ChannelFuture channelFuture = serverBootstrap.bind(10090).sync();
      channelFuture.channel().closeFuture().sync();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
