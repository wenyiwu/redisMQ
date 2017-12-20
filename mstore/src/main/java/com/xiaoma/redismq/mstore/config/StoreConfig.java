package com.xiaoma.redismq.mstore.config;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Getter
@Setter
public class StoreConfig {
    /**
     *
     * mstore.clusterName=xiaoma
        mstore.brokerName=push
        mstore.brokerAddr=10.20.72.168
        mstore.brokerId=1
     */
    private String clusterName;

    private String brokerName;

    private String brokerAddr;

    private int storeId;

//    private List<String> topicList = new LinkedList<>();

    private Map<String /*topic*/, Integer /*queueSize*/> topicMap = new HashMap<>();

    private int listenPort = 8866;

    public StoreConfig() {
        try {
            Properties pro = new Properties();
            InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties");
            pro.load(in);
            clusterName = pro.getProperty("mstore.clusterName");
            brokerName = pro.getProperty("mstore.brokerName");
            brokerAddr = pro.getProperty("mstore.brokerAddr");
            storeId = Integer.parseInt(pro.getProperty("mstore.brokerId"));
            topicMap.put("push", 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
