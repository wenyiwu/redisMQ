package com.xiaoma.redismq.mstore.config;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

    public StoreConfig() {
        try {
            Properties pro = new Properties();
            InputStream in = getClass().getClassLoader().getResourceAsStream("application.properties");
            pro.load(in);
            clusterName = pro.getProperty("mstore.clusterName");
            brokerName = pro.getProperty("mstore.brokerName");
            brokerAddr = pro.getProperty("mstore.brokerAddr");
            brokerAddr = pro.getProperty("mstore.brokerId");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
