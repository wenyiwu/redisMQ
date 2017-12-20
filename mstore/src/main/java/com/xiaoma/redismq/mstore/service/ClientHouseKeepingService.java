package com.xiaoma.redismq.mstore.service;

import com.xiaoma.redismq.mstore.manager.ClientInfoManager;
import com.xiaoma.redismq.remoting.listen.ChannelEventListener;
import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

import java.sql.ClientInfoStatus;

@Getter
@Setter
public class ClientHouseKeepingService implements ChannelEventListener {

    private ClientInfoManager clientInfoManager;

    public ClientHouseKeepingService(){}

    public ClientHouseKeepingService(ClientInfoManager clientInfoManager) {
        this.clientInfoManager = clientInfoManager;
    }

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {
    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        System.out.println("onChannelClose : " + remoteAddr);
        clientInfoManager.onChannelDestroy(remoteAddr, channel);
    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        System.out.println("onChannelClose : " + remoteAddr);
        clientInfoManager.onChannelDestroy(remoteAddr, channel);
    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {
        System.out.println("onChannelClose : " + remoteAddr);
        clientInfoManager.onChannelDestroy(remoteAddr, channel);
    }
}
