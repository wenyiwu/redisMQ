package com.xiaoma.redismq.namesrv;

import com.xiaoma.redismq.namesrv.processor.NameSrvRequestProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.Semaphore;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BrokerRegisterTest {

    @Test
    public void contextLoads() throws Exception {
        NameServerService nameServerService = new NameServerService();

        nameServerService.start();

        nameServerService.registerProcessor(new NameSrvRequestProcessor(nameServerService.getRouteInfoManager()));

        //阻塞代码，不会占用CPU
        Semaphore semaphore = new Semaphore(0);
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
