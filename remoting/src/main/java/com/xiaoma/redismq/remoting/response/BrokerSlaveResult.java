package com.xiaoma.redismq.remoting.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BrokerSlaveResult {
    private String brokerName;

    private String masterAddrs;
}
