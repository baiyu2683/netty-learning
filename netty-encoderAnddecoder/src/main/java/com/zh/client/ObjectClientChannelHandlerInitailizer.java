package com.zh.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class ObjectClientChannelHandlerInitailizer extends ChannelInitializer<SocketChannel> {

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
      ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.softCachingResolver(ObjectClientChannelHandlerInitailizer.class.getClassLoader())))
        .addLast(new ClientUserChannelOutboundChannel())
        .addLast(new ObjectEncoder());
  }
}
