package com.xiaoma.redismq.mstore.manager;

import com.xiaoma.redismq.common.data.Message;
import com.xiaoma.redismq.common.flag.CharFlag;
import com.xiaoma.redismq.common.util.CommonUtil;
import com.xiaoma.redismq.mstore.ConsumerChannel;
import com.xiaoma.redismq.remoting.request.ConsumerQueueRequest;
import com.xiaoma.redismq.remoting.request.RegisterConsumerRequest;
import com.xiaoma.redismq.remoting.response.ConsumerQueueResult;
import com.xiaoma.redismq.remoting.response.RegisterConsumerResult;
import com.xiaoma.redismq.remoting.response.SendResult;
import io.netty.channel.Channel;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 消费逻辑：
 * 1.消费者心跳发送为：clientId，List<topic></>
 * 2.消费者接收到的回复为：topic，brokerName，messageQueueSize，List<clientId>的List</>
 * 3.消费者重新获取队列时，先发送自己所需要的新的队列给broker
 *   然后broker看是否有变化，若MessageQueue拥有者发生变化，将MessageQueue的flag标志标位false，此时在新clientId中将新拥有者的clientId记录
 *   当MessageQueue中的东西消灭完，或者旧consumer挂掉时，启用新client，若无新client加入，则clientId和newClientId相同，也依旧照此逻辑执行
 *  4.消费者获取消息，发送QueueSize给消费者，并将lastProgress等于QueueSize，然后批量或者逐条发送给consumer，当lastProgress等于当前progress时，
 *    再执行此逻辑
 *
 *  生产逻辑
 *  1.生产者除了顺序消息外，都是均衡发送给所有MessageQueue，此时发送会接收消息，并更新QueueSize
 */



public class ClientInfoManager {

    private String brokerName;

    private Map<String /*topicName*/, MessageQueueManager> topicMessageQueueMap;

    private Map<String /*topic*/, Set<String> /*clientId*/> topicClientMap;

    private Map<String /*clientId*/, ConsumerChannel /*ConsumerChannel*/> clientChannelMap;

    //TODO delete messageKey = topicName_brokerName_queueId
    private Map<String /*topicName_brokerName_queueId*/, List<Message>> producerMessageMap;

    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public ClientInfoManager(Map<String, Integer> topicMap, String brokerName) {
        this.brokerName = brokerName;
        this.topicMessageQueueMap = new HashMap<>();
        clientChannelMap = new HashMap<>();
        topicClientMap = new HashMap<>();
        producerMessageMap = new HashMap<>();
        Iterator<Map.Entry<String, Integer>> iterator = topicMap.entrySet().iterator();

        while(iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            String topic = entry.getKey();
            int size = entry.getValue();

            //init topicMessageQueueMap
            MessageQueueManager manager = new MessageQueueManager(topic);
            manager.initMessageQueue(size);
            topicMessageQueueMap.put(topic, manager);
        }
    }

    public void registerMessageQueue(Map<String /*topicName*/, MessageQueueManager> topicMessageQueueMap) {
        this.topicMessageQueueMap = topicMessageQueueMap;
    }

    public String registerProducer(String str, Channel channel) {
        System.out.println(str);
        return "OK";
    }

    public List<RegisterConsumerResult> registerConsumer(RegisterConsumerRequest consumerRequest, Channel channel) {
        List<RegisterConsumerResult> consumerResultList = null;
        String clientId = consumerRequest.getClientId();
        try {
            lock.writeLock().lockInterruptibly();

            if(!clientChannelMap.containsKey(clientId) && consumerRequest.getTopicList() != null) {
                consumerResultList = new LinkedList<>();
                clientChannelMap.put(clientId, new ConsumerChannel(channel, CommonUtil.parseChannelRemoteAddr(channel)));
                for(String topic : consumerRequest.getTopicList()) {
                    RegisterConsumerResult consumerResult = new RegisterConsumerResult();
                    MessageQueueManager manager = topicMessageQueueMap.get(topic);
                    if(manager == null) {
                        continue;
                    }else {
                        consumerResult.setBrokerName(brokerName);
                        consumerResult.setTopic(topic);
                        consumerResult.setMessageQueueSize(manager.getMessageQueueMap().size());
                        //List<String> clientList = new LinkedList<>();
                        Set<String> clientIdList = topicClientMap.get(topic);
                        if(clientIdList == null) {
                            clientIdList = new HashSet<>();
                            clientIdList.add(consumerRequest.getClientId());
                            topicClientMap.put(topic, clientIdList);
                        }else {
                            clientIdList.add(consumerRequest.getClientId());
                            consumerResult.setClientIdList(clientIdList);
                        }

                    }
                    consumerResultList.add(consumerResult);
                }

                //TODO 广播通知有新成员加入
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
        return consumerResultList;
    }


    public ConsumerQueueResult changeConsumerMessageQueue(ConsumerQueueRequest consumerQueueRequest) {
        ConsumerQueueResult result = new ConsumerQueueResult();
        result.setSuccess(true);
        try {
            String clientId = consumerQueueRequest.getClientId();
            lock.writeLock().lockInterruptibly();

            Iterator<Map.Entry<String, List<Integer>>> iterator = consumerQueueRequest.getNewTopicQueueMap().entrySet().iterator();

            //第一步判断是否能进行clientId更换
            while(iterator.hasNext()) {
                Map.Entry<String, List<Integer>> entry = iterator.next();
                List<Integer> queueIdList = entry.getValue();
                String topic = entry.getKey();
                MessageQueueManager manager = topicMessageQueueMap.get(topic);
                if(manager == null) {
                    result.setSuccess(false);
                    return result;
                }
                manager.changeQueueClient(queueIdList, clientId);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
        return result;
    }

    public void onChannelDestroy(String remoteAddr, Channel channel) {
    }

    public SendResult producerMessage(Message message) {
        String topic = message.getTopic();
        String key = topic + CharFlag.UNDERLINE_FLAG + brokerName + CharFlag.UNDERLINE_FLAG + message.getQueueId();
        List<Message> messageList = producerMessageMap.get(key);

        if(messageList == null) {
            messageList = new LinkedList<>();
            producerMessageMap.put(key, messageList);
        }

        messageList.add(message);
        SendResult sendResult = new SendResult(message.getMessageId());
        message.setMessageId(messageList.size());
        return sendResult;
    }
}
