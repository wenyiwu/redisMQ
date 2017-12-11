package com.xiaoma.redismq.common.data;

import com.xiaoma.redismq.common.flag.BrokerFlag;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class BrokerData {
    private String clusterName;

    private String brokerName;

    private HashMap<Integer /*brokerId*/, Set<String> /*brokerAddr*/> brokerInfoMap;

    private int size = 0;

    public BrokerData() {
        brokerInfoMap = new HashMap<Integer, Set<String>>();
        brokerInfoMap.put(BrokerFlag.BROKER_MASTER, new HashSet<>());
        brokerInfoMap.put(BrokerFlag.BROKER_SLAVE, new HashSet<>());
    }

    public BrokerData(String clusterName, String brokerName) {
        this.clusterName = clusterName;
        this.brokerName = brokerName;
        brokerInfoMap = new HashMap<Integer, Set<String>>();
        brokerInfoMap.put(BrokerFlag.BROKER_MASTER, new HashSet<>());
        brokerInfoMap.put(BrokerFlag.BROKER_SLAVE, new HashSet<>());
    }


    public boolean setBrokerInfo(int brokerId, String brokerAddr) {
        if(brokerId == BrokerFlag.BROKER_MASTER || brokerId == BrokerFlag.BROKER_SLAVE) {
            //brokerInfoMap.put(brokerId, brokerAddr);
            size++;
            return brokerInfoMap.get(brokerId).add(brokerAddr);
        }

        return false;
    }

    public void removeBrokeInfo(int brokerId, String brokerAddr) {
        if(brokerId == BrokerFlag.BROKER_MASTER || brokerId == BrokerFlag.BROKER_SLAVE) {
            size--;
            brokerInfoMap.get(brokerId).remove(brokerAddr);
        }
    }
}
