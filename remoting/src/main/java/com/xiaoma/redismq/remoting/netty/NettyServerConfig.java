package com.xiaoma.redismq.remoting.netty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NettyServerConfig {
    private int listenPort = 8888;
    private int serverWorkerThreads = Math.max(2, Runtime.getRuntime().availableProcessors()) * 2;
    private int serverCallbackExecutorThreads = 0;
    private int serverSelectorThreads = 3;
    private int serverOnewaySemaphoreValue = 256;
    private int serverAsyncSemaphoreValue = 64;
    private int serverChannelMaxIdleTimeSeconds = 120;

    private int serverSocketSndBufSize = 65535;
    private int serverSocketRcvBufSize = 65535;
    private boolean serverPooledByteBufAllocatorEnable = true;
    private boolean useEpollNativeSelector = false;

    //public static final int SYSTEM_PROPERTY_PARALLEL = Math.max(2, Runtime.getRuntime().availableProcessors());

}
