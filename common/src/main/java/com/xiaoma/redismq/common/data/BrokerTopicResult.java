package com.xiaoma.redismq.common.data;

import com.xiaoma.redismq.common.flag.BrokerFlag;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

@Getter
@Setter
public class BrokerTopicResult {
    private String topicName;

    private String brokerName;

    private int readQueueNum;

    private int writeQueueNum;

    private int perm;

    private HashMap<Integer /*brokerId*/, Set<String> /*brokerAddr*/> brokerInfoMap;

    public BrokerTopicResult(TopicInfo topicInfo, BrokerData brokerData) {
        this.topicName = topicInfo.getTopicName();
        this.brokerName = topicInfo.getBrokerName();
        this.readQueueNum = topicInfo.getReadQueueNum();
        this.writeQueueNum = topicInfo.getWriteQueueNum();
        this.perm = topicInfo.getPerm();
        this.brokerInfoMap = brokerData.getBrokerInfoMap();
    }

    public String getBrokerMasterAddr() {
        Set<String> addrSet =  brokerInfoMap.get(BrokerFlag.BROKER_MASTER);
        Iterator<String> it = addrSet.iterator();
        while(it.hasNext())//判断是否有下一个
            return it.next();
        return null;
    }
}
