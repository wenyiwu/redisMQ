package com.xiaoma.redismq.common.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TopicRouteData {
    private List<TopicInfo> topicInfoList;

    private List<BrokerData> brokerDataList;
}
