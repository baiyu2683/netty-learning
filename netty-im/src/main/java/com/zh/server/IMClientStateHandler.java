package com.zh.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class IMClientStateHandler extends ChannelInboundHandlerAdapter {

  // 记录所有channel
  public final static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    String username = ctx.channel().remoteAddress().toString();
    if (channels.size() > 0) {
      channels.writeAndFlush(username + " 上线了");
    }
    if (!channels.contains(ctx.channel())) {
      channels.add(ctx.channel());
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("channel数量: " + channels.size());
    if (channels.size() > 0) {
      channels.writeAndFlush(ctx.channel().remoteAddress() + " 下线了");
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.channel().close();
    cause.printStackTrace();
  }
}
