package com.zh.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class MessageHandler extends ChannelInboundHandlerAdapter {

  /**
   * 处理读取到的消息
   * @param ctx
   * @param msg
   * @throws Exception
   */
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    System.out.println("server ctx = " + ctx);
    ByteBuf byteBuf = (ByteBuf) msg;
    System.out.println("客户端发送了消息: " + byteBuf.toString(CharsetUtil.UTF_8));
    System.out.println("客户端地址: " + ctx.channel().remoteAddress());
  }


  /**
   * 读取完成之后，写一个响应消息
   * @param ctx
   * @throws Exception
   */
  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.writeAndFlush(Unpooled.copiedBuffer("我是服务端".getBytes(StandardCharsets.UTF_8)));
  }

  /**
   * 异常的话关闭通道
   * @param ctx
   * @param cause
   * @throws Exception
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.channel().close();
  }
}
