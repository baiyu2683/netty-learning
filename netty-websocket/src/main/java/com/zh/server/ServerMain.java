package com.zh.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class ServerMain {

  public static void main(String[] args) {
    EventLoopGroup boss = new NioEventLoopGroup(1);
    EventLoopGroup worker = new NioEventLoopGroup();

    try {
      ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap.group(boss, worker)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.SO_KEEPALIVE, true)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new HttpServerCodec())
              .addLast(new ChunkedWriteHandler())
              .addLast(new HttpObjectAggregator(8192))
              /**
               * 1. 对应websocket, 数据以帧的形式传递
               * 2. 浏览器一 ws://localhost:7000/hello 表示请求url
               * 3. WebSocketServerProtocolHandler用来将http协议升级为websocket协议
               */
              .addLast(new WebSocketServerProtocolHandler("/hello"));
          }
        });
    } catch (Exception e) {
      boss.shutdownGracefully();
      worker.shutdownGracefully();
    }
  }
}
