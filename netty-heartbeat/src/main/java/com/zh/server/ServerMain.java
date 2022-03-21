package com.zh.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.TimeUnit;

public class ServerMain {

  public static void main(String[] args) {
    EventLoopGroup boss = new NioEventLoopGroup(1);
    EventLoopGroup worker = new NioEventLoopGroup();
    ChannelFuture channelFuture = null;
    try {
      ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap.group(boss, worker)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new IdleStateHandler(3, 5, 7, TimeUnit.SECONDS))
              .addLast(new ClientIdleCheckChannelHandler());
          }
        });
      channelFuture = bootstrap.bind("127.0.0.1", 10090).sync();
      channelFuture.addListener(future -> {
        if (future.isSuccess()) {
          System.out.println("绑定成功: " + 10090);
        } else {
          System.out.println("绑定失败");
        }
      });
      channelFuture.channel().closeFuture().sync();
    } catch (Exception e) {
      boss.shutdownGracefully();
      worker.shutdownGracefully();
    }
  }
}
