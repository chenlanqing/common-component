package com.qing.fan.observer.event;

/**
 * @author QingFan
 * @version 1.0.0
 * @date 2023年11月10日 22:01
 */
public interface MessageEventListener<E extends MessageEvent> {

    void handler(E e);

    boolean match(E e);
}
