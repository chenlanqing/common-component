package com.qing.fan.observer.event;

/**
 * @author QingFan
 * @version 1.0.0
 * @date 2023年11月10日 22:44
 */
public class TestEvent {

    public static void main(String[] args) {
        EventManage.addListener(new EventListener1());
        EventManage.publish(new Event1("第一个事件"));
        System.out.println("第二次事件");
        EventManage.addListener(new EventListener1());
        EventManage.publish(new Event1("第二个事件"));
    }

    private static class Event1 extends MessageEvent {

        public Event1(Object source) {
            super(source);
        }
    }
    private static class Event2 extends MessageEvent {
        public Event2(Object source) {
            super(source);
        }
    }

    private static class EventListener1 implements MessageEventListener<Event1> {
        @Override
        public void handler(Event1 event1) {
            System.err.println("EventListener1: " + event1.source);
        }
        @Override
        public boolean match(Event1 event1) {
            return true;
        }
    }

    private static class EventListener2 implements MessageEventListener<Event1> {
        @Override
        public void handler(Event1 event1) {
            System.out.println("---EventListener2: " + event1.getClass().getName());
        }
        @Override
        public boolean match(Event1 event1) {
            return true;
        }
    }
}
