package com.xiaoma.redismq.common.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TopicRouteData {
    private List<BrokerTopicResult> brokerTopicResultList;

    public boolean isEmpty() {
        if(brokerTopicResultList == null || brokerTopicResultList.size() == 0) {
            return true;
        }
        return false;
    }

    public void setData() {

    }
}
