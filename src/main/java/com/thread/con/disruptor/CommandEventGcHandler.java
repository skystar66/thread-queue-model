package com.thread.con.disruptor;

import com.lmax.disruptor.EventHandler;
import com.thread.con.vo.MessageEvent;

/**
 * DbCommandEvent的GC处理器
 */
public class CommandEventGcHandler implements EventHandler<MessageEvent> {

  @Override
  public void onEvent(MessageEvent event, long sequence, boolean endOfBatch) throws Exception {
    event.clearForGc();
  }

}
