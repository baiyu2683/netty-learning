package com.zh.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

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

    // 模拟一个耗时的业务处理过程
//    TimeUnit.SECONDS.sleep(5);
//    ByteBuf byteBuf = (ByteBuf) msg;
//    System.out.println("客户端发送了消息: " + byteBuf.toString(CharsetUtil.UTF_8));
//    System.out.println("客户端地址: " + ctx.channel().remoteAddress());

    // 将耗时任务放到任务队列中执行。这里需要先读取发送的数据。
    ByteBuf byteBuf = (ByteBuf) msg;
    String readMsg = byteBuf.toString(CharsetUtil.UTF_8);
    ctx.channel().eventLoop().execute(() -> {
        try {
          TimeUnit.SECONDS.sleep(5);
          System.out.println("客户端发送了消息: " + readMsg);
          System.out.println("客户端地址: " + ctx.channel().remoteAddress());
        } catch (Exception e) {
          e.printStackTrace();
        }
    });
    System.out.println("go on..");
    // 提交一个延时任务, 提交到schedtaskqueue中，一个优先级队列。
    ctx.channel().eventLoop().scheduleAtFixedRate(() -> {
      System.out.println("延时任务: " + System.currentTimeMillis());
    }, 1, 5, TimeUnit.SECONDS);

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
