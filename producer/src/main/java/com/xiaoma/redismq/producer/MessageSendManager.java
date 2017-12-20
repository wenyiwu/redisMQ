package com.xiaoma.redismq.producer;

import com.xiaoma.redismq.common.data.*;
import com.xiaoma.redismq.common.flag.CharFlag;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MessageSendManager {

    private AtomicInteger queueId = new AtomicInteger(0);

    //private Map<Integer /*queueId*/, >
    //保存每个messageQueue信息
//    private List<MessageQueueInfo> queueInfoList;

    private Map<String /*brokerName_queueId*/, MessageQueueInfo> queueInfoMap;

    private Map<String /*brokerName*/, String /*brokerAddr*/> brokerAddrMap;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public MessageSendManager() {
        queueInfoMap = new HashMap<>();

        brokerAddrMap = new HashMap<>();
    }

    public void updateMessageManager(TopicRouteData routeData) {

        if(routeData == null || routeData.isEmpty()) {
            return;
        }

        try {

            lock.writeLock().lockInterruptibly();
            //Set<RouteData> data =
            //先更新 queueInfoList
            List<BrokerTopicResult> brokerTopicResultList = routeData.getBrokerTopicResultList();

            for(BrokerTopicResult brokerTopicResult : brokerTopicResultList) {
                //更新 brokerAddrMap
                String brokerName = brokerTopicResult.getBrokerName();
                String topicName = brokerTopicResult.getTopicName();
                //String brokerAddr = brokerAddrMap.get(brokerTopicResult.getBrokerName());
                brokerAddrMap.put(brokerName, brokerTopicResult.getBrokerMasterAddr());

                //更新 queueInfoMap
                int readSize = brokerTopicResult.getReadQueueNum();
                String key = brokerName + CharFlag.UNDERLINE_FLAG + readSize;
                MessageQueueInfo messageQueueInfo = queueInfoMap.get(key);
                if(messageQueueInfo == null) {
                    for(int i = 1; i <= readSize; i++) {
                        MessageQueueInfo newMessageQueueInfo = new MessageQueueInfo(i, topicName, brokerName);
                        queueInfoMap.put(brokerName + CharFlag.UNDERLINE_FLAG + i, newMessageQueueInfo);
                    }
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public MessageData send(String message) {
        int size = queueInfoMap.size();
        int useQueueId = queueId.incrementAndGet()/size + 1;
        MessageQueueInfo queueInfo = getMessageQueueByIndex(useQueueId);

        if(queueInfo == null) {
            return null;
        }

        String brokerAddr = brokerAddrMap.get(queueInfo.getBrokerName());

        if(brokerAddr == null) {
            return null;
        }
        Message msg = new Message();
        msg.setMessage(message);
        msg.setQueueId(queueInfo.getQueueId());
        msg.setTopic(queueInfo.getTopicName());

        //TODO create messageId and save message
        MessageData messageData = new MessageData();
        messageData.setBrokerAddr(brokerAddr);
        messageData.setMessage(msg);
        return  messageData;
    }

    private MessageQueueInfo getMessageQueueByIndex(int index) {
        Iterator<Map.Entry<String, MessageQueueInfo>> iterator = queueInfoMap.entrySet().iterator();
        int i = 1;
        while(iterator.hasNext()) {
            MessageQueueInfo queueInfo = iterator.next().getValue();
            if(i == index){
                return queueInfo;
            }
            i++;
        }
        return null;
    }
}
