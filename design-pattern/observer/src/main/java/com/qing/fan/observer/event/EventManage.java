package com.qing.fan.observer.event;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author QingFan
 * @version 1.0.0
 * @date 2023年11月10日 22:03
 */
public class EventManage {

    /**
     * 记录发布的事件和对该事件进行了监听的监听器
     */
    private static final Map<String, List<MessageEventListener>> LISTENER_MAP = new ConcurrentHashMap<>();

    /**
     * 注册监听
     *
     * @param listener 监听器
     */
    public static void addListener(MessageEventListener listener) {
        // 获取泛型类
        Class<?> arg = (Class<?>) ((ParameterizedType) listener.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        String name = arg.getName();
        LISTENER_MAP.computeIfAbsent(name, s -> new ArrayList<>()).add(listener);
    }

    /**
     * 发布事件
     */
    public static void publish(MessageEvent event) {
        List<MessageEventListener> listeners = LISTENER_MAP.computeIfAbsent(event.getClass().getName(), s -> new ArrayList<>());
        for (MessageEventListener listener : listeners) {
            if (listener.match(event)) {
                listener.handler(event);
            }
        }
    }
}
