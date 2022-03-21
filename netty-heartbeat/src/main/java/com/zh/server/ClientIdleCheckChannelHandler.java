package com.zh.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 处理idle事件
 */
public class ClientIdleCheckChannelHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (!(evt instanceof IdleStateEvent)) {
      ctx.fireUserEventTriggered(evt);
      return;
    }
    String remoteAddress = ctx.channel().remoteAddress().toString();
    IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
    switch (idleStateEvent.state()) {
      case READER_IDLE:
        System.out.println(remoteAddress+ ", 读空闲");
        break;
      case WRITER_IDLE:
        System.out.println(remoteAddress + ", 写空闲");
        break;
      default:
        System.out.println(remoteAddress + "， 空闲");
    }
  }
}
