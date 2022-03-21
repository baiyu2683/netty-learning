package com.zh;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.junit.Test;

public class UnpooledTests {

  @Test
  public void createByteBuf() {
    ByteBuf byteBuf = Unpooled.copiedBuffer("中文", CharsetUtil.UTF_8);
    System.out.println(byteBuf);
  }

  @Test
  public void createByteBuf2() {
    // 默认256字节
    ByteBuf byteBuf = Unpooled.buffer();
    for (int i = 0 ; i <= 5 ; i++) {
      System.out.println(byteBuf.readerIndex() + ", " + byteBuf.writerIndex() + ", " + byteBuf.capacity());
      byteBuf.writeInt(i);
    }
    for (int i = 0 ; i <= 5 ; i++) {
      System.out.println(byteBuf.readInt());
      byteBuf.discardReadBytes();
      byteBuf.discardSomeReadBytes();
    }
    System.out.println("capacity: " + byteBuf.capacity());
  }

  @Test
  public void createByteBufByCopy() {
    ByteBuf byteBuf = Unpooled.copiedBuffer("和𰻝", CharsetUtil.UTF_8);
    // 内存返回true和直接内存返回false
    // TODO 作用？
    if (byteBuf.hasArray()) {
      byte[] bytes = byteBuf.array();
      System.out.println(byteBuf.arrayOffset());
      CharSequence c = byteBuf.getCharSequence(0, byteBuf.writerIndex(), CharsetUtil.UTF_8);
      c.toString();
      System.out.println(c);
    }
    System.out.println(byteBuf);
  }
}
