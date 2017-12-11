package com.xiaoma.redismq.remoting.netty;

public interface InvokeCallback {
    void operationComplete(final ResponseFuture responseFuture);
}
