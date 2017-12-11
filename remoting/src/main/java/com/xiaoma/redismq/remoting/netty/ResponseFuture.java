package com.xiaoma.redismq.remoting.netty;

import com.xiaoma.redismq.remoting.process.RemotingCommand;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
public class ResponseFuture {
    private InvokeCallback invokeCallback;

    private RemotingCommand remotingCommand;

    private Long timeOutMillis;

    private Long beginTime = System.currentTimeMillis();

    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    private Throwable cause;

    private boolean isSuccess;

    public ResponseFuture(Long timeOutMillis, InvokeCallback invokeCallback) {
        this.timeOutMillis = timeOutMillis;
        this.invokeCallback = invokeCallback;
    }

    public void setRemotingCommand(RemotingCommand remotingCommand) {
        this.remotingCommand = remotingCommand;
        countDownLatch.countDown();
    }

    public RemotingCommand waitForRensponse() throws InterruptedException {
        countDownLatch.await(timeOutMillis, TimeUnit.MILLISECONDS);
        return remotingCommand;
    }
}
