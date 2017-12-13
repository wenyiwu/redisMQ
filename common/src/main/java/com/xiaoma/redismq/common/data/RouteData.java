package com.xiaoma.redismq.common.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteData {
    private BrokerData brokerData;

    private int readQueueSize;

    private int writeQueueSize;

    private int perm;

    public RouteData() {
        brokerData = new BrokerData();
    }

    public RouteData(BrokerData brokerData) {
        this.brokerData = brokerData;
    }
}
