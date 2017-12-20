package com.xiaoma.redismq.remoting.netty;

import com.xiaoma.redismq.common.flag.ResponseFlag;
import com.xiaoma.redismq.remoting.listen.ChannelEventListener;
import com.xiaoma.redismq.remoting.process.RemotingCommand;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
* 处理消息接收，消息发送，记录client连接状态并做相应处理，检测是否有broker心跳停止
*/
public abstract class NettyRemotingBaseService {

    protected NettyRequestProcessor nettyProcessor;

    private ConcurrentHashMap<Integer, ResponseFuture> responseMap = new ConcurrentHashMap<>(256);

    protected ChannelEventListener eventListener;

    public void processMessageReceived(ChannelHandlerContext ctx, RemotingCommand msg) {
        switch (msg.getCode()) {
            case ResponseFlag.REQUEST:
                processRequestCommand(ctx, msg);
                break;
            case ResponseFlag.RESPONSE:
                processResponseCommand(ctx, msg);
                break;
            default:
                break;
        }
    }

    protected void processRequestCommand(ChannelHandlerContext ctx, RemotingCommand msg) {
        try {
            if(nettyProcessor != null) {
                RemotingCommand command = nettyProcessor.processRequest(ctx, msg);
                ctx.writeAndFlush(command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void processResponseCommand(ChannelHandlerContext ctx, RemotingCommand msg) {
        System.out.println("responsId : " + msg.getResponseId());

        ResponseFuture responseFuture = responseMap.get(msg.getResponseId());
        System.out.println("recive msg: " + msg.toString());

        if(responseFuture != null) {
            responseFuture.setRemotingCommand(msg);
        }
    }

    public void setNettyProcessor(NettyRequestProcessor requestProcessor) {
        this.nettyProcessor = requestProcessor;
    }

    public NettyRequestProcessor getNettyProcessor() {
        return nettyProcessor;
    }

    protected RemotingCommand invokeSyncImpl(final Channel channel, RemotingCommand remotingCommand, final long timeOutMillis)
            throws InterruptedException {
        ResponseFuture responseFuture = new ResponseFuture(timeOutMillis, null);

        responseMap.put(remotingCommand.getResponseId(), responseFuture);
        System.out.println("responsId : " + remotingCommand.getResponseId());
        RemotingCommand response = null;
        try {
            channel.writeAndFlush(remotingCommand).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        responseFuture.setSuccess(true);
                        System.out.println("recive message success");
                        return;
                    } else {
                        responseFuture.setSuccess(false);
                        System.out.println("recive message false");

                    }
                    responseMap.remove(remotingCommand.getResponseId());
                    responseFuture.setRemotingCommand(null);
                    responseFuture.setCause(future.cause());

                }
            });

            response = responseFuture.waitForRensponse();
        } finally {
            responseMap.remove(remotingCommand.getResponseId());
        }

        return response;
    }

    protected void putNettyEvent(NettyEventType eventType, String socketAddr, Channel channel) {
        switch (eventType) {
            case IDLE:
                eventListener.onChannelIdle(socketAddr, channel);
                break;
            case CONNECT:
                eventListener.onChannelConnect(socketAddr, channel);
                break;
            case CLOSE:
                eventListener.onChannelClose(socketAddr, channel);
                break;
            case EXCEPTION:
                eventListener.onChannelException(socketAddr, channel);
                break;
        }
    }
}
