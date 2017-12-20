package com.xiaoma.redismq.remoting.netty;

import com.xiaoma.redismq.common.data.Message;
import com.xiaoma.redismq.common.data.MessageData;
import com.xiaoma.redismq.common.flag.ResponseFlag;
import com.xiaoma.redismq.remoting.process.RemotingCommand;
import com.xiaoma.redismq.remoting.process.RequestCode;
import com.xiaoma.redismq.remoting.request.ConsumerQueueRequest;
import com.xiaoma.redismq.remoting.request.RegisterConsumerRequest;
import com.xiaoma.redismq.remoting.response.ConsumerQueueResult;
import com.xiaoma.redismq.remoting.response.RegisterConsumerResult;
import com.xiaoma.redismq.remoting.response.SendResult;

import java.util.List;

public class MstoreSrvClient {
    private RemotingClient remotingClient;

    private List<String> brokerAddrList;

    public MstoreSrvClient(List<String> brokerAddrList) {
        this.brokerAddrList = brokerAddrList;
        remotingClient = new NettyRemotingClient(new NettyClientConfig());

        remotingClient.start();

    }

    public void addBrokerSrvAddr(String addr) {
        brokerAddrList.add(addr);
        remotingClient.createNameServer(addr);
    }

    public void removeAddr(String addr) {
        brokerAddrList.remove(addr);
        remotingClient.closeChannel(addr);
    }

    //TODO registerConsumer
    public RegisterConsumerResult registerConsumer(String addr, RegisterConsumerRequest request) throws InterruptedException {
        RemotingCommand remotingCommand = new RemotingCommand();
        remotingCommand.setCode(ResponseFlag.REQUEST);
        remotingCommand.setType(RequestCode.REGISTER_CONSUMER);
        remotingCommand.setBody(request);

        RemotingCommand response = remotingClient.invokeSync(addr, remotingCommand, 100);
        //System.out.println("Mstore registerConsumer : " + response.toString());

        if(response == null) {
            return null;
        }else {
            //TODO 处理 RegisterConsumerResult
            return null;
        }
    }

    //TODO registerConsumer
    public RegisterConsumerResult registerProducer(String addr, String request) throws InterruptedException {
        RemotingCommand remotingCommand = new RemotingCommand();
        remotingCommand.setCode(ResponseFlag.REQUEST);
        remotingCommand.setType(RequestCode.REGISTER_PRODUCER);
        remotingCommand.setBody(request);

        RemotingCommand response = remotingClient.invokeSync(addr, remotingCommand, 2000);
        //System.out.println("Mstore registerConsumer : " + response.toString());

        if(response == null) {
            return null;
        }else {
            //TODO 处理 RegisterConsumerResult
            return null;
        }
    }

    //TODO getConsumerQueue
    public ConsumerQueueResult getConsumerQueue(String addr, ConsumerQueueRequest request) throws InterruptedException {
        RemotingCommand remotingCommand = new RemotingCommand();
        remotingCommand.setCode(ResponseFlag.REQUEST);
        remotingCommand.setType(RequestCode.CONSUMER_CHANGE_MESSAGE_QUEUE);
        remotingCommand.setBody(request);

        RemotingCommand response = remotingClient.invokeSync(addr, remotingCommand, 2000);
        System.out.println("Mstore registerConsumer : " + response.toString());
        if(response == null) {
            return null;
        }else {
            //TODO 处理 ConsumerQueueResult
            return null;
        }
    }

    public SendResult sendMessageSync(MessageData messageData) throws InterruptedException {
        Message message = messageData.getMessage();
        String addr = messageData.getBrokerAddr();
        RemotingCommand remotingCommand = new RemotingCommand();
        remotingCommand.setCode(ResponseFlag.REQUEST);
        remotingCommand.setType(RequestCode.PRODUCER_MESSAGE);
        remotingCommand.setBody(message);

        RemotingCommand response = remotingClient.invokeSync(addr, remotingCommand, 2000);

        SendResult sendResult = (SendResult) response.getBody();

        return sendResult;
    }

    public void registerProcessor(NettyRequestProcessor requestProcessor) {
        remotingClient.registerProcessor(requestProcessor);
    }
}
