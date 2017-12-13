package com.xiaoma.redismq.consumer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultClient implements MQProducer{

    private DefaultProducerImpl defaultProducer;

    private String producerGroup;

    private String clientId;

    public DefaultClient() {
        defaultProducer = new DefaultProducerImpl();
    }

    public void setNameSrvAddr(String addr) {
        defaultProducer.getNameSrvClient().addNameSrvAddr(addr);
    }

    @Override
    public void start() {

    }

    @Override
    public void shutDown() {

    }
}
