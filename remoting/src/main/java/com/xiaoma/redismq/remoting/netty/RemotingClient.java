package com.xiaoma.redismq.remoting.netty;

import com.xiaoma.redismq.remoting.process.RemotingCommand;
import io.netty.channel.Channel;

import java.util.List;
import java.util.concurrent.ExecutorService;

public interface RemotingClient extends RemotingService {

    void updateNameServerAddressList(final List<String> addrs);

    List<String> getNameServerAddressList();

    void createNameServer(String nameSrvAddr);

    RemotingCommand invokeSync(final String addr, final RemotingCommand request,
                                      final long timeoutMillis) throws InterruptedException;

    void invokeAsync(final String addr, final RemotingCommand request, final long timeoutMillis,
                            final InvokeCallback invokeCallback);

    void invokeOneway(final String addr, final RemotingCommand request, final long timeoutMillis);

    void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
                                  final ExecutorService executor);

    Channel choiceNameServerChannel(final String addr) throws InterruptedException;


    }
