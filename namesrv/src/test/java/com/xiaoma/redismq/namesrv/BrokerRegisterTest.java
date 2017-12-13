package com.xiaoma.redismq.namesrv;

import com.xiaoma.redismq.namesrv.processor.NameSrvRequestProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BrokerRegisterTest {

    @Test
    public void contextLoads() throws Exception {
        NameServerService nameServerService = new NameServerService();

        nameServerService.start();

        nameServerService.registerProcessor(new NameSrvRequestProcessor(nameServerService.getRouteInfoManager()));

        while(true){

        }
    }
}
