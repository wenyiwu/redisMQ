package com.xiaoma.redismq.mstore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.Semaphore;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MstoreServerTest {
    @Test
    public void mstoreServer() {
        MstoreServer server = new MstoreServer();
        server.start();

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
