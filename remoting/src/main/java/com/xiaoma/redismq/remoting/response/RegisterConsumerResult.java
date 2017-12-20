package com.xiaoma.redismq.remoting.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class RegisterConsumerResult {

    private String topic;

    private int messageQueueSize;

    private String brokerName;

    private Set<String> clientIdList;

}
