package com.xiaoma.redismq.mstore.client;

import com.xiaoma.redismq.common.flag.ResponseFlag;
import com.xiaoma.redismq.remoting.response.BrokerSlaveResult;
import com.xiaoma.redismq.mstore.config.StoreConfig;
import com.xiaoma.redismq.remoting.netty.NettyClientConfig;
import com.xiaoma.redismq.remoting.netty.NettyRemotingClient;
import com.xiaoma.redismq.remoting.netty.RemotingClient;
import com.xiaoma.redismq.remoting.process.RemotingCommand;
import com.xiaoma.redismq.remoting.process.RequestCode;
import com.xiaoma.redismq.remoting.request.RegisterBrokerRequest;

import java.util.LinkedList;
import java.util.List;

public class StoreRegisterClient{

    private RemotingClient remotingClient;

    private List<String> nameSrvAddrList;

    private final StoreConfig storeConfig;

    public StoreRegisterClient() {
        remotingClient = new NettyRemotingClient(new NettyClientConfig());

        remotingClient.start();

        nameSrvAddrList = new LinkedList<>();

        storeConfig = new StoreConfig();
    }

    public void addNameSrvAddr(String addr) {
        nameSrvAddrList.add(addr);
        remotingClient.createNameServer(addr);
    }

    public RemotingCommand invokeSync(RemotingCommand request, Long timeoutMillis) throws InterruptedException {
        return remotingClient.invokeSync(nameSrvAddrList.get(0), request, timeoutMillis);
    }

    public BrokerSlaveResult registerStore(RegisterBrokerRequest request) throws InterruptedException {
        RemotingCommand remotingCommand = new RemotingCommand();
        remotingCommand.setCode(ResponseFlag.REQUEST);
        remotingCommand.setType(RequestCode.REGISTER_BROKER);
        remotingCommand.setBody(request);

        RemotingCommand response = remotingClient.invokeSync(nameSrvAddrList.get(0), remotingCommand, 100);
        if(response == null) {
            return null;
        }else {
            return (BrokerSlaveResult)response.getBody();
        }
    }
}
