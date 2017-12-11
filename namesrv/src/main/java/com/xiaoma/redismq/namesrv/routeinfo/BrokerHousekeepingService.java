package com.xiaoma.redismq.namesrv.routeinfo;

import com.xiaoma.redismq.namesrv.NameServerService;
import com.xiaoma.redismq.remoting.listen.ChannelEventListener;
import io.netty.channel.Channel;

public class BrokerHousekeepingService implements ChannelEventListener {

    private final NameServerService namesrvController;

    public BrokerHousekeepingService(NameServerService namesrvController) {
        this.namesrvController = namesrvController;
    }

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {

    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        System.out.println("onChannelClose : " + remoteAddr);
        this.namesrvController.getRouteInfoManager().onChannelDestroy(remoteAddr, channel);
    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        System.out.println("onChannelException : " + remoteAddr);

        this.namesrvController.getRouteInfoManager().onChannelDestroy(remoteAddr, channel);
    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
        System.out.println("onChannelIdle : " + remoteAddr);

        this.namesrvController.getRouteInfoManager().onChannelDestroy(remoteAddr, channel);
    }
}
