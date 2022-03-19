package com.zh.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ClientMain {

  public static void main(String[] args) {
    EventLoopGroup eventExecutors = new NioEventLoopGroup();
    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(eventExecutors)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline()
              .addLast(new LoggingHandler(LogLevel.DEBUG))
              .addLast(new ClientHandler());
          }
        });
      ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 10090).sync();
      channelFuture.channel().closeFuture().sync();
    } catch (Exception e) {
      eventExecutors.shutdownGracefully();
    }
  }
}
