package com.zh.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * 使用java序列化实现对象的编码和解码
 */
public class ObjectChannelHandlerInitializer extends ChannelInitializer<SocketChannel> {
  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
      ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.softCachingResolver(ObjectChannelHandlerInitializer.class.getClassLoader())))
        .addLast(new ServerUserChannelHandler())
        .addLast(new ObjectEncoder());
  }
}
