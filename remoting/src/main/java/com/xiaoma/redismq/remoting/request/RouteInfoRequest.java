package com.xiaoma.redismq.remoting.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RouteInfoRequest {
    private String topicName;

    public RouteInfoRequest() {
    }

    public RouteInfoRequest(String topicName) {
        this.topicName = topicName;
    }

}
