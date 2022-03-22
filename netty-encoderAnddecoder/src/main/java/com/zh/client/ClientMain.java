package com.zh.client;

import com.zh.bean.User;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.Date;

public class ClientMain {
  public static void main(String[] args) {
    EventLoopGroup eventExecutors = new NioEventLoopGroup();
    Bootstrap bootstrap = new Bootstrap();
    bootstrap.group(eventExecutors)
      .channel(NioSocketChannel.class)
      .option(ChannelOption.SO_KEEPALIVE, true)
      .handler(new ObjectClientChannelHandlerInitailizer());
    try {
      ChannelFuture future = bootstrap.connect("127.0.0.1", 10090).sync();
      future.addListener(new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
          if (future.isSuccess()) {
            System.out.println("连接成功");
            User user = new User();
            user.setBirthday(new Date());
            user.setAge(100000l);
            user.setName("zhang恒");
            future.channel().writeAndFlush(user);
          } else {
            System.out.println("连接失败");
            future.cause().printStackTrace();
          }
        }
      });
      future.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      e.printStackTrace();
      eventExecutors.shutdownGracefully();
    }
  }
}
