package com.xiaoma.redismq.producer;

import com.xiaoma.redismq.remoting.request.RouteInfoRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProducerGetTopicRouteTest {

    @Test
    public void getTopicRouteTest() throws InterruptedException {
        DefaultProducer defaultProducer = new DefaultProducer();
        defaultProducer.setProducerGroup("producer");
        defaultProducer.setClientId("123");
        defaultProducer.setNameSrvAddr("127.0.0.1:8888");
        defaultProducer.getDefaultProducer().getNameSrvClient().getTopicRouteDataByTopicName(new RouteInfoRequest("topic"));
        while(true) {}
    }
}
