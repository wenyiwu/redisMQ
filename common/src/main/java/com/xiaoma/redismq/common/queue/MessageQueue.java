package com.xiaoma.redismq.common.queue;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class MessageQueue {
    private String queueName;

    private int queueId;

    private String consumerClientId;

    private int consumerProgress;

    //用于发送到消费者之后的缓存，若消费者消费成功，则将此缓存从Redis中清除，并改变消费进度 consumerProgress
    private Map<Integer /*MessageId*/, String /*Message*/> messageMap;
}
