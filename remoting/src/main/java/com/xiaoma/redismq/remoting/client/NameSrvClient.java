package com.xiaoma.redismq.remoting.client;

import com.xiaoma.redismq.common.data.TopicRouteData;
import com.xiaoma.redismq.common.flag.ResponseFlag;
import com.xiaoma.redismq.common.namesrv.BrokerSlaveResult;
import com.xiaoma.redismq.remoting.netty.NettyClientConfig;
import com.xiaoma.redismq.remoting.netty.NettyRemotingClient;
import com.xiaoma.redismq.remoting.netty.RemotingClient;
import com.xiaoma.redismq.remoting.process.RemotingCommand;
import com.xiaoma.redismq.remoting.process.RequestCode;
import com.xiaoma.redismq.remoting.request.RegisterBrokerRequest;
import com.xiaoma.redismq.remoting.request.RouteInfoRequest;

import java.util.LinkedList;
import java.util.List;

public class NameSrvClient {
    private RemotingClient remotingClient;

    private RouteService routeService;

    private List<String> nameSrvAddrList;

    public NameSrvClient() {
        remotingClient = new NettyRemotingClient(new NettyClientConfig());

        remotingClient.start();

        nameSrvAddrList = new LinkedList<>();

    }

    public void addNameSrvAddr(String addr) {
        nameSrvAddrList.add(addr);
        remotingClient.createNameServer(addr);
    }

    public TopicRouteData getTopicRouteDataByTopicName(RouteInfoRequest request) throws InterruptedException {
        RemotingCommand remotingCommand = new RemotingCommand();
        remotingCommand.setCode(ResponseFlag.REQUEST);
        remotingCommand.setType(RequestCode.GET_ROUTEINFO_BY_TOPIC);
        remotingCommand.setBody(request);

        RemotingCommand response = remotingClient.invokeSync(nameSrvAddrList.get(0), remotingCommand, 100);
        if(response == null) {
            return null;
        }else {
            routeService.updateTopicRouteInfo(request.getTopicName(), (TopicRouteData)response.getBody());
            return (TopicRouteData)response.getBody();
        }
    }

}
