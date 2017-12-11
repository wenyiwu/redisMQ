package com.xiaoma.redismq.remoting.request;

import com.xiaoma.redismq.common.data.BrokerTopicInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
public class RegisterBrokerRequest {
    private String clusterName;
    private String brokerName;
    private String brokerAddr;
    private int brokerId;
    private BrokerTopicInfo topicInfo;
}
