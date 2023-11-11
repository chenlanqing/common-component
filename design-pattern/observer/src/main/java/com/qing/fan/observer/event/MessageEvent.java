package com.qing.fan.observer.event;

/**
 * 定义通用事件源，记录事件信息
 *
 * @author QingFan
 * @version 1.0.0
 * @date 2023年11月10日 22:00
 */
public class MessageEvent {

    protected transient Object source;

    public MessageEvent(Object source) {
        if (source == null)
            throw new IllegalArgumentException("null source");

        this.source = source;
    }
}
