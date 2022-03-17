package com.zh.selector;

import java.io.IOException;
import java.nio.channels.Selector;

public class SelectorMain {

  public static void main(String[] args) throws IOException {
    Selector selector = Selector.open();
    // 当有事件发生时，返回个数，没有时阻塞
    selector.select();
    // 返回发生事件的key
    selector.selectedKeys();
    // 返回所有key
    selector.keys();
    // 让select返回
    selector.wakeup();
    // 立即返回，不会和select方法一样等待。
    selector.selectNow();
  }
}
