package com.zh.buffer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 通过RandomAccessFile对文件在内存修改
 * 下面例子会将target文件夹下的mappedbytebuffer.txt文件内容修改
 */
public class MappedByteBufferMain {

  public static void main(String[] args) throws IOException {
    URL url = MappedByteBufferMain.class.getClassLoader().getResource("mappedbytebuffer.txt");
    RandomAccessFile accessFile = new RandomAccessFile(url.getFile(), "rw");
    FileChannel fileChannel = accessFile.getChannel();
    // 读写模式、映射开始的位置、映射字节数
    MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 5);

    mappedByteBuffer.put(0, (byte) 'H');
    mappedByteBuffer.put(3, (byte) '9');
    accessFile.close();
  }
}
