package com.xiaoma.redismq.mstore.processor;

import com.xiaoma.redismq.common.data.Message;
import com.xiaoma.redismq.common.flag.ResponseFlag;
import com.xiaoma.redismq.mstore.manager.ClientInfoManager;
import com.xiaoma.redismq.remoting.netty.NettyRequestProcessor;
import com.xiaoma.redismq.remoting.process.RemotingCommand;
import com.xiaoma.redismq.remoting.process.RequestCode;
import com.xiaoma.redismq.remoting.request.ConsumerQueueRequest;
import com.xiaoma.redismq.remoting.request.RegisterConsumerRequest;
import com.xiaoma.redismq.remoting.response.ConsumerQueueResult;
import com.xiaoma.redismq.remoting.response.RegisterConsumerResult;
import com.xiaoma.redismq.remoting.response.SendResult;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class MstoreRequestProcessor implements NettyRequestProcessor {

    private ClientInfoManager clientInfoManager;

    public MstoreRequestProcessor(ClientInfoManager clientInfoManager) {
        this.clientInfoManager = clientInfoManager;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        int code = request.getType();

        switch (code) {
            case RequestCode.REGISTER_CONSUMER:
                return registerConsumer(ctx, request);
            case RequestCode.CONSUMER_MESSAGE:
            case RequestCode.CONSUMER_CHANGE_MESSAGE_QUEUE:
            case RequestCode.CONSUMER_SUCCESS:
            case RequestCode.PRODUCER_MESSAGE:
                return producerMessage(ctx, request);
            case RequestCode.REGISTER_PRODUCER:
                return registerProducer(ctx, request);
            default:
                break;
        }
        return null;
    }

    private RemotingCommand registerProducer(ChannelHandlerContext ctx, RemotingCommand request) {
        RemotingCommand remotingCommand = new RemotingCommand();
        remotingCommand.setCode(ResponseFlag.RESPONSE);
        remotingCommand.setResponseId(request.getResponseId());

        String str = (String) request.getBody();

        String result = clientInfoManager.registerProducer(str, ctx.channel());
        remotingCommand.setBody(result);
        return remotingCommand;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    private RemotingCommand registerConsumer(ChannelHandlerContext ctx, RemotingCommand request) {
        System.out.println(ctx.channel().remoteAddress() + " : " + request.getBody().toString());
        RemotingCommand remotingCommand = new RemotingCommand();
        remotingCommand.setCode(ResponseFlag.RESPONSE);
        remotingCommand.setResponseId(request.getResponseId());

        RegisterConsumerRequest consumerRequest = (RegisterConsumerRequest) request.getBody();

        List<RegisterConsumerResult> consumerResultList = clientInfoManager.registerConsumer(consumerRequest, ctx.channel());
        remotingCommand.setBody(consumerResultList);

        return remotingCommand;
    }

    private RemotingCommand changeConsumerMessageQueue(ChannelHandlerContext ctx, RemotingCommand request) {
        RemotingCommand remotingCommand = new RemotingCommand();
        remotingCommand.setCode(ResponseFlag.RESPONSE);
        remotingCommand.setResponseId(request.getResponseId());

        ConsumerQueueRequest consumerQueueRequest = (ConsumerQueueRequest) request.getBody();
        ConsumerQueueResult consumerQueueResult = clientInfoManager.changeConsumerMessageQueue(consumerQueueRequest);
        remotingCommand.setBody(consumerQueueResult);

        return remotingCommand;
    }

    private RemotingCommand producerMessage(ChannelHandlerContext ctx, RemotingCommand request) {
        RemotingCommand remotingCommand = new RemotingCommand();
        remotingCommand.setCode(ResponseFlag.RESPONSE);
        remotingCommand.setResponseId(request.getResponseId());
        Message message = (Message) request.getBody();

        SendResult sendResult = clientInfoManager.producerMessage(message);
        System.out.println("/************************************************************/");
        return remotingCommand;
    }
}
