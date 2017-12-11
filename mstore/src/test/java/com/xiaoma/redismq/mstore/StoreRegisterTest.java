package com.xiaoma.redismq.mstore;

import com.xiaoma.redismq.common.flag.BrokerFlag;
import com.xiaoma.redismq.mstore.client.StoreRegisterClient;
import com.xiaoma.redismq.remoting.request.RegisterBrokerRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StoreRegisterTest {
    @Test
    public void registerMasterStoreTest() throws InterruptedException {
        RegisterBrokerRequest request = new RegisterBrokerRequest();
        request.setBrokerAddr("127.0.0.1:8856");
        //request.s
        request.setBrokerId(BrokerFlag.BROKER_MASTER);
        request.setBrokerName("ma");
        request.setClusterName("xiao");
        StoreRegisterClient client = new StoreRegisterClient();
        client.addNameSrvAddr("127.0.0.1:8888");
        client.registerStore(request);

        while(true) {
            Thread.sleep(10000);
            client.registerStore(request);
        }
    }

    @Test
    public void registerSlaveStoreTest() throws InterruptedException {
        RegisterBrokerRequest request = new RegisterBrokerRequest();
        request.setBrokerAddr("127.0.0.1:8836");
        //request.s
        request.setBrokerId(BrokerFlag.BROKER_SLAVE);
        request.setBrokerName("ma");
        request.setClusterName("xiao");
        StoreRegisterClient client = new StoreRegisterClient();
        client.addNameSrvAddr("127.0.0.1:8888");
        client.registerStore(request);
        while(true) {
            Thread.sleep(10000);
            client.registerStore(request);
        }
    }
}
