package com.xiaoma.redismq.namesrv.processor;

import com.xiaoma.redismq.common.data.TopicRouteData;
import com.xiaoma.redismq.common.flag.ResponseFlag;
import com.xiaoma.redismq.common.namesrv.BrokerSlaveResult;
import com.xiaoma.redismq.namesrv.routeinfo.RouteInfoManager;
import com.xiaoma.redismq.remoting.netty.NettyRequestProcessor;
import com.xiaoma.redismq.remoting.process.RemotingCommand;
import com.xiaoma.redismq.remoting.process.RequestCode;
import com.xiaoma.redismq.remoting.request.RegisterBrokerRequest;
import com.xiaoma.redismq.remoting.request.RouteInfoRequest;
import io.netty.channel.ChannelHandlerContext;

public class NameSrvRequestProcessor implements NettyRequestProcessor {

    RouteInfoManager routeInfoManager;

    public NameSrvRequestProcessor(RouteInfoManager routeInfoManager) {
        this.routeInfoManager = routeInfoManager;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        int code = request.getType();
        switch (code) {
            case RequestCode.REGISTER_BROKER:
                return registerBroker(ctx, request);
            case RequestCode.UNREGISTER_BROKER:
                return unregisterBroker(ctx, request);
            case RequestCode.GET_ROUTEINFO_BY_TOPIC:
                return getRouteinfoByTopic(ctx, request);
            default:
                break;
        }
        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    private RemotingCommand registerBroker(ChannelHandlerContext ctx, RemotingCommand request) {
        RemotingCommand remotingCommand = new RemotingCommand();
        remotingCommand.setCode(ResponseFlag.RESPONSE);
        RegisterBrokerRequest brokerRequest = (RegisterBrokerRequest) request.getBody();
        BrokerSlaveResult brokerSlaveResult = routeInfoManager.registerBroker(brokerRequest, request.getClientAddr());
        remotingCommand.setBody(brokerSlaveResult);
        //RemotingCommand remotingCommand =
        return remotingCommand;
    }

    private RemotingCommand unregisterBroker(ChannelHandlerContext ctx, RemotingCommand request) {
        return null;
    }

    private RemotingCommand getRouteinfoByTopic(ChannelHandlerContext ctx, RemotingCommand request) {
        RemotingCommand remotingCommand = new RemotingCommand();
        remotingCommand.setCode(ResponseFlag.RESPONSE);
        RouteInfoRequest routeInfoRequest = (RouteInfoRequest) request.getBody();
        TopicRouteData topicRouteData = routeInfoManager.getTopicRouteDataByTopicName(routeInfoRequest.getTopicName());
        remotingCommand.setBody(topicRouteData);
        return remotingCommand;
    }
}
