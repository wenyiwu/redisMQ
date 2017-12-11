package com.xiaoma.redismq.remoting.netty;

import com.xiaoma.redismq.remoting.process.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

public interface NettyRequestProcessor {
    RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request)
            throws Exception;

    boolean rejectRequest();

}
