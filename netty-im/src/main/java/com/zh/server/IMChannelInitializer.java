package com.zh.server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

public class IMChannelInitializer extends ChannelInitializer<SocketChannel> {

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8))
      .addLast(new MessageChannelHandler())
      .addLast(new IMClientStateHandler())
      .addLast(new StringEncoder(CharsetUtil.UTF_8));
  }
}
