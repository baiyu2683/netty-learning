package com.zh.channel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

public class FileChannelMain {

  public static void main(String[] args) throws IOException {
    String content = "hello";
    FileOutputStream fileOutputStream = new FileOutputStream("/home/crispr/filechanneltest.txt");
    FileChannel fileChannel = fileOutputStream.getChannel();
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    buffer.put(content.getBytes(StandardCharsets.UTF_8));
    buffer.flip();
    fileChannel.write(buffer);

    fileChannel.close();


    FileChannel readChannel = FileChannel.open(Paths.get("/home/crispr", "filechanneltest.txt"));
    // 使用rewind，重新读取相同的数据，大小和之前写的数据相同。
    buffer.rewind();
    readChannel.read(buffer);
    readChannel.close();
    buffer.flip();
    System.out.println(new String(buffer.array(), buffer.position(), buffer.limit(), StandardCharsets.UTF_8));
  }
}
