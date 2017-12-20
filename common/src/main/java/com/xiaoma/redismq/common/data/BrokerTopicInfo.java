package com.xiaoma.redismq.common.data;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class BrokerTopicInfo {
    private HashMap<String /*topicString*/, TopicInfo> topicMap;

    public BrokerTopicInfo() {
        this.topicMap = new HashMap<>();
    }

    public BrokerTopicInfo(HashMap<String, TopicInfo> topicMap) {
        this.topicMap = topicMap;
    }

    public void newDefaultBrokerTopicInfo(String brokerName) {
        TopicInfo topicInfo = new TopicInfo();
        topicInfo.setBrokerName(brokerName);
        topicInfo.setPerm(0);
        topicInfo.setReadQueueNum(4);
        topicInfo.setWriteQueueNum(4);
        topicInfo.setTopicName("push");
        topicMap.put("push", topicInfo);
    }
}
