package com.zh;

import io.netty.util.NettyRuntime;
import org.junit.Test;

public class AvailableProcessor {

  @Test
  public void test1() {
    System.out.println(NettyRuntime.availableProcessors());
  }
}
