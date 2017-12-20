package com.xiaoma.redismq.common.queue;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class MessageQueue {
    private int queueId;

    private String clientId;

    private String newClientId;

    private String clientIP;

    private boolean isChange;

    //当前消费进度
    private int consumerProgress;
    //最大消费进度
    private int consumerLastProgress;

    //queue最后一个值的进度
    private int queueSize;

    //用于发送到消费者之后的缓存，若消费者消费成功，则将此缓存从Redis中清除，并改变消费进度 consumerProgress
    private Map<Integer /*MessageId*/, String /*Message*/> messageMap;

    //用于发送到消费者之后，消费者没有返回成功的缓存记录的重试队列
    private Map<Integer /*MessageId*/, String /*Message*/> messageRetryMap;

    public MessageQueue() {
        this(0);
    }

    public MessageQueue(int queueId) {
        initMessageQueue(queueId);
    }

    public void initMessageQueue(int queueId) {
        this.queueId = queueId;
        this.clientId = null;
        this.newClientId = null;
        this.clientIP = null;
        this.isChange = false;
        this.consumerProgress = 0;
        this.consumerLastProgress = 0;
        this.queueSize = 0;
        messageMap = new HashMap<>(256);
        messageRetryMap = new HashMap<>(64);
    }

    public boolean canChangeClient() {
        if(clientId == null || clientId.equals("")) {
            return true;
        }

        return clientId.equals(newClientId);
    }
}
