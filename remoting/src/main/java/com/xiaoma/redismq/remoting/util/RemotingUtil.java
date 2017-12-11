package com.xiaoma.redismq.remoting.util;

import com.xiaoma.redismq.common.util.CommonUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class RemotingUtil {
    public static void closeChannel(Channel channel) {
        final String addrRemote = CommonUtil.parseChannelRemoteAddr(channel);
        channel.close().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("closeChannel: close the connection to remote address: " + addrRemote + " result: " +
                        future.isSuccess());
            }
        });
    }
}
