package com.xiaoma.redismq.producer;

import com.xiaoma.redismq.common.data.Message;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class MessageQueueInfo {
    private int queueId;

    private String topicName;

    private String brokerName;

    private Map<Integer /*messageId*/, Message> messageSendMap;

    private Map<Integer /*messageId*/, Message> messageOldSendMap;

    public MessageQueueInfo(int queueId, String topicName, String brokerName) {
        this.queueId = queueId;

        this.topicName = topicName;

        this.brokerName = brokerName;

        messageSendMap = new HashMap<>();

        messageOldSendMap = new HashMap<>();
    }
}
