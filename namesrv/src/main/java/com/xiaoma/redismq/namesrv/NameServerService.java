package com.xiaoma.redismq.namesrv;

import com.xiaoma.redismq.namesrv.routeinfo.BrokerHousekeepingService;
import com.xiaoma.redismq.namesrv.routeinfo.RouteInfoManager;
import com.xiaoma.redismq.remoting.netty.NettyRemotingServer;
import com.xiaoma.redismq.remoting.netty.NettyRequestProcessor;
import com.xiaoma.redismq.remoting.netty.NettyServerConfig;
import com.xiaoma.redismq.remoting.netty.RemotingServer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NameServerService {
    private NettyServerConfig nettyServerConfig;

    private RouteInfoManager routeInfoManager;

    private RemotingServer remotingServer;

    public NameServerService() {
        nettyServerConfig = new NettyServerConfig();

        routeInfoManager = new RouteInfoManager();

        remotingServer = new NettyRemotingServer(nettyServerConfig, new BrokerHousekeepingService(this));
    }

    public void start() throws Exception {
        this.remotingServer.start();
    }

    public void registerProcessor(NettyRequestProcessor processor) {
        remotingServer.registerProcessor(processor);
    }
}
