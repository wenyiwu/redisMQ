package com.xiaoma.redismq.remoting.request;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class RegisterConsumerRequest {
    private String clientId;

    private Set<String> topicList;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
