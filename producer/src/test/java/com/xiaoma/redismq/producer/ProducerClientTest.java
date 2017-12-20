package com.xiaoma.redismq.producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProducerClientTest {
    @Test
    public void producerClientTest() throws InterruptedException {
        DefaultProducerImpl defaultProducer = new DefaultProducerImpl();

        defaultProducer.start();

        Thread.sleep(10000);

        defaultProducer.send("push", "hello");
        //阻塞代码，不会占用CPU
        Semaphore semaphore = new Semaphore(0);
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Test
    public void test() {
        Map<String, String> map = new HashMap<>();
        map.put("123", "123");
        String str = map.get("123");
        str = "123123";
        System.out.println(map.toString());
    }
}
