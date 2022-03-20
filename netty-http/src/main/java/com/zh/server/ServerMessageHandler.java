package com.zh.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.nio.charset.StandardCharsets;

public class ServerMessageHandler extends SimpleChannelInboundHandler<HttpObject> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
    if (msg instanceof HttpRequest) {

      HttpRequest httpRequest = (HttpRequest) msg;
      System.out.println("uri: " + httpRequest.uri());
      // 过滤favicon.ico请求
      int index = httpRequest.uri().lastIndexOf(".");
      if (index > 0) {
        return;
      }

      System.out.println("msg类型: " + httpRequest.getClass());
      System.out.println("客户端地址: " + ctx.channel().remoteAddress());
      System.out.println("请求方法: " + httpRequest.method());
      System.out.println(ctx.channel().pipeline().hashCode());

      // 回消息
      ByteBuf byteBuf = Unpooled.copiedBuffer("我是服务器".getBytes(StandardCharsets.UTF_8));

      DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
      // 这里写上charset=utf-8，防止客户端乱码。注意有一个空格，格式不对不生效。
      response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=utf-8");
      response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());

      ctx.writeAndFlush(response);
    }
  }
}
