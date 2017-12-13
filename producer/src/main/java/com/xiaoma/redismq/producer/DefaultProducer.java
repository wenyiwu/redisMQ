package com.xiaoma.redismq.producer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DefaultProducer implements MQProducer{

    private DefaultProducerImpl defaultProducer;

    private String producerGroup;

    private String clientId;

    public DefaultProducer() {
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
