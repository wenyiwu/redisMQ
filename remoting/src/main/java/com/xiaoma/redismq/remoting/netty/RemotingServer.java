package com.xiaoma.redismq.remoting.netty;

import com.xiaoma.redismq.remoting.process.RemotingCommand;
import io.netty.channel.Channel;

import java.util.concurrent.ExecutorService;

public interface RemotingServer extends RemotingService {

    void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
                           final ExecutorService executor);

    void registerDefaultProcessor(final NettyRequestProcessor processor, final ExecutorService executor);

    void registerProcessor(NettyRequestProcessor processor);


    int localListenPort();

    /**
    Pair<NettyRequestProcessor, ExecutorService> getProcessorPair(final int requestCode);
    */

    RemotingCommand invokeSync(final Channel channel, final RemotingCommand request,
                               final long timeoutMillis) throws InterruptedException;// throws InterruptedException, RemotingSendRequestException, RemotingTimeoutException;
    /**
    void invokeAsync(final Channel channel, final RemotingCommand request, final long timeoutMillis,
                     final InvokeCallback invokeCallback);// throws InterruptedException,RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;
    */

    void invokeAsync(final Channel channel, final RemotingCommand request, final long timeoutMillis,
                     final InvokeCallback invokeCallback);

    void invokeOneway(final Channel channel, final RemotingCommand request, final long timeoutMillis);
            //throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;

}
