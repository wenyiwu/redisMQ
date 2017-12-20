package com.xiaoma.redismq.consumer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultClient implements MQConsumer{

    private DefaultConsumerImpl defaultConsumer;

    private String consumerGroup;

    private String clientId;

    public DefaultClient() {
        defaultConsumer = new DefaultConsumerImpl();
    }

    public void setNameSrvAddr(String addr) {
        defaultConsumer.getNameSrvClient().addNameSrvAddr(addr);
    }

    @Override
    public void start() {

    }

    @Override
    public void shutDown() {

    }
}
