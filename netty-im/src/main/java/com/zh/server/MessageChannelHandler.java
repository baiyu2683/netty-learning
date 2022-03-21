package com.zh.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class MessageChannelHandler extends SimpleChannelInboundHandler<String> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
    System.out.println("消息: " + msg);
    Channel channel = ctx.channel();
    IMClientStateHandler.channels.forEach(ch -> {
      if (ch != channel) {
        ch.writeAndFlush("[其他人]: " + channel.remoteAddress() + ", 消息: " + msg + "\n");
      } else {
        ch.writeAndFlush("[自己]: " + msg + "\n");
      }
    });
  }
}
