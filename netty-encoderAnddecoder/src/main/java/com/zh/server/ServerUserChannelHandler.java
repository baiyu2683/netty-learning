package com.zh.server;

import com.zh.bean.User;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerUserChannelHandler extends SimpleChannelInboundHandler<User> {
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, User msg) throws Exception {
    System.out.println("收到: " + msg);
    ctx.fireChannelRead(msg);
  }
}
