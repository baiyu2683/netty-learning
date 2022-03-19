package com.zh.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ServerMain {

  public static void main(String[] args) throws InterruptedException {
    // 声明两个线程池，默认是cpu个数两倍
    // boss给主Reactor使用，仅仅处理accept事件
    // worker给从Reactor使用，处理读写事件
    EventLoopGroup boss = new NioEventLoopGroup();
    EventLoopGroup worker = new NioEventLoopGroup();
    try {
      ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap.group(boss, worker)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 128)  // 等待队列
        .childOption(ChannelOption.SO_KEEPALIVE, true) // socket保持链接
        .childHandler(new ChannelInitializer<SocketChannel>() {   // 为socketChannel相关事件增加事件处理器，放到pipeline中
          @Override
          protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline()
              .addLast(new LoggingHandler(LogLevel.DEBUG))
              .addLast(new MessageHandler());
          }
        });
      // 绑定端口
      ChannelFuture channelFuture = serverBootstrap.bind(10090).sync();
      // 监听关闭
      channelFuture.channel().closeFuture().sync();
    } catch (Exception e){
      boss.shutdownGracefully();
      worker.shutdownGracefully();
    }
  }
}
