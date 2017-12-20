package com.xiaoma.redismq.remoting.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ConsumerQueueRequest {
    private String clientId;

    private Map<String /*topic*/, List< Integer/*MessageQueueId*/>> newTopicQueueMap;
}
