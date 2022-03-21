package com.zh.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8))
      .addLast(new MessageClientChannelHandler())
      .addLast(new StringEncoder(CharsetUtil.UTF_8));
  }
}
