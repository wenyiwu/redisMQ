package com.xiaoma.redismq.mstore.manager;

import com.xiaoma.redismq.common.queue.MessageQueue;
import com.xiaoma.redismq.mstore.ConsumerChannel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class MessageQueueManager {
    private String topicName;

    private int consumerSize;

    private Map<Integer /*MessageQueueId*/, MessageQueue> messageQueueMap;

    public MessageQueueManager() {}

    public MessageQueueManager(String topicName) {
        this(topicName, 0);
    }

    public MessageQueueManager(String topicName, int consumerSize) {
        this.topicName = topicName;
        this.consumerSize = consumerSize;
        messageQueueMap = new HashMap<>(4);
    }

    public void initMessageQueue(int size) {
        for(int i = 0; i < size; i++) {
            MessageQueue messageQueue = new MessageQueue(i);
            messageQueueMap.put(i, messageQueue);
        }
    }

    public boolean canChangeClient(List<Integer> queueIdList) {
        for(int queueId : queueIdList) {
            MessageQueue messageQueue = messageQueueMap.get(queueId);
            if(messageQueue == null) {
                return false;
            }
            if(!messageQueue.canChangeClient()) {
                return false;
            }
        }
        return true;
    }

    public void changeQueueClient(List<Integer> queueIdList, String clientId) {
        for(int queueId : queueIdList) {
            MessageQueue messageQueue = messageQueueMap.get(queueId);
            if(messageQueue == null) {
                continue;
            }
            if(clientId.equals(messageQueue.getClientId())) {
                continue;
            } else {
                messageQueue.setNewClientId(clientId);
                messageQueue.setChange(true);
            }
        }
    }
}
