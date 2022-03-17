package com.zh.buffer;

import java.nio.IntBuffer;

/**
 * mark: 标记
 * position: 当前读写位置
 * limit: 最大读写位置。
 * capacity： 本buffer大小
 * mark <= position <= limit <= capacity
 */
public class BufferMain {

  public static void main(String[] args) {
    IntBuffer buffer = IntBuffer.allocate(5);

    for (int i = 0 ; i < 5 ; i++) {
      buffer.put(i + 1);
    }
    // 读写转换， 将limit放到position位置，将position置为0
    buffer.flip();
    // 如果有数据，就一直读取
    while (buffer.hasRemaining()) {
      int num = buffer.get();
      System.out.println("数据: " + num);
    }
    // 从头开始写，但是只能写到limit位置了
//    buffer.rewind();
    // 清空整个buffer, 和新建时一样
    buffer.clear();
  }
}
