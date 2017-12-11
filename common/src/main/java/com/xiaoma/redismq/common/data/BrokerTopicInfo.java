package com.xiaoma.redismq.common.data;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class BrokerTopicInfo {
    private HashMap<String /*topicString*/, TopicInfo> topicMap;
}
