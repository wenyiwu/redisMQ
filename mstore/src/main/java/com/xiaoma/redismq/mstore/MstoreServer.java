package com.xiaoma.redismq.mstore;

import com.xiaoma.redismq.common.data.BrokerTopicInfo;
import com.xiaoma.redismq.common.flag.BrokerFlag;
import com.xiaoma.redismq.common.flag.ServerFlag;
import com.xiaoma.redismq.mstore.client.StoreRegisterClient;
import com.xiaoma.redismq.mstore.config.StoreConfig;
import com.xiaoma.redismq.mstore.manager.ClientInfoManager;
import com.xiaoma.redismq.mstore.processor.MstoreRequestProcessor;
import com.xiaoma.redismq.mstore.service.ClientHouseKeepingService;
import com.xiaoma.redismq.remoting.listen.ChannelEventListener;
import com.xiaoma.redismq.remoting.netty.NettyRemotingServer;
import com.xiaoma.redismq.remoting.netty.NettyServerConfig;
import com.xiaoma.redismq.remoting.netty.RemotingServer;
import com.xiaoma.redismq.remoting.request.RegisterBrokerRequest;
import com.xiaoma.redismq.remoting.response.BrokerSlaveResult;
import org.apache.catalina.Server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class MstoreServer {

    private StoreRegisterClient storeClient;

    private RemotingServer remotingServer;

    private ChannelEventListener eventListener;

    private NettyServerConfig serverConfig;

    private ClientInfoManager clientInfoManager;

    private Map<String /*topic*/, Integer /*queueSize*/> topicMap = new HashMap<>();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "MstoreServerScheduledThread");
        }
    });

    private StoreConfig storeConfig;

    public MstoreServer() {
        this.serverConfig = new NettyServerConfig();
        serverConfig.setListenPort(ServerFlag.MSTORE_SERVER_PORT);
        this.storeConfig = new StoreConfig();
        this.topicMap = storeConfig.getTopicMap();

        clientInfoManager = new ClientInfoManager(topicMap, storeConfig.getBrokerName());

        remotingServer = new NettyRemotingServer(serverConfig, new ClientHouseKeepingService(clientInfoManager));

        remotingServer.registerProcessor(new MstoreRequestProcessor(clientInfoManager));

        storeClient = new StoreRegisterClient();
        storeClient.addNameSrvAddr(ServerFlag.NAME_SERVER_ADDR);
    }

    public void start() {
        remotingServer.start();

        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    registerBroker();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 5, ServerFlag.CONNECT_NAME_SERVER_SECONDS, TimeUnit.SECONDS);
    }

    public void registerEventListenner(ChannelEventListener channelEventListener) {

    }

    public BrokerSlaveResult registerBroker() throws InterruptedException {
        RegisterBrokerRequest request = new RegisterBrokerRequest();
        request.setBrokerAddr(ServerFlag.MSTORE_SERVER_ADDR);
        //request.s
        request.setBrokerId(BrokerFlag.BROKER_MASTER);
        request.setBrokerName("ma");
        request.setClusterName("xiao");
        BrokerTopicInfo topicInfo = new BrokerTopicInfo();
        topicInfo.newDefaultBrokerTopicInfo("ma");
        request.setTopicInfo(topicInfo);
        return storeClient.registerStore(request);
    }
}
