package com.xiaoma.redismq.consumer;

import com.xiaoma.redismq.common.data.BrokerData;
import com.xiaoma.redismq.common.data.RouteData;
import com.xiaoma.redismq.common.data.TopicInfo;
import com.xiaoma.redismq.common.data.TopicRouteData;
import com.xiaoma.redismq.common.flag.ServerFlag;
import com.xiaoma.redismq.common.util.CommonUtil;
import com.xiaoma.redismq.remoting.client.NameSrvClient;
import com.xiaoma.redismq.remoting.client.RouteService;
import com.xiaoma.redismq.remoting.netty.MstoreSrvClient;
import com.xiaoma.redismq.remoting.request.RegisterConsumerRequest;
import com.xiaoma.redismq.remoting.request.RouteInfoRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Getter
@Setter
public class DefaultConsumerImpl implements RouteService {

    private NameSrvClient nameSrvClient;

    private BrokerConsumerClient brokerConsumerClient;

    private MstoreSrvClient mstoreSrvClient;

    private Set<String> topicSet;

    private Map<String /*topic*/, Set<RouteData> /*brokerName*/> topicRouteMap;

    private String clientId;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ConsumerScheduledThread");
        }
    });

    public DefaultConsumerImpl() {
        nameSrvClient = new NameSrvClient(this);

        nameSrvClient.addNameSrvAddr(ServerFlag.NAME_SERVER_ADDR);

        List<String> mstoreList = new LinkedList<>();

        mstoreList.add(ServerFlag.MSTORE_SERVER_ADDR);

        mstoreSrvClient = new MstoreSrvClient(mstoreList);

        //mstoreSrvClient.addBrokerSrvAddr(ServerFlag.MSTORE_SERVER_ADDR);

        topicSet = new HashSet<>();

        topicRouteMap = new HashMap<>();

        clientId = CommonUtil.createClientId();
    }

    public void start() {
        //name_server心跳定时器
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    nameSrvClient.getTopicRouteDataByTopicName(new RouteInfoRequest("push"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 1, ServerFlag.CONNECT_NAME_SERVER_SECONDS, TimeUnit.SECONDS);

        //mstore_server 心跳定时器
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    RegisterConsumerRequest request = new RegisterConsumerRequest();
                    request.setClientId(clientId);
                    request.setTopicList(topicRouteMap.keySet());
                    mstoreSrvClient.registerConsumer(ServerFlag.MSTORE_SERVER_ADDR, request);
                    System.out.println("registerConsumer");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ServerFlag.REGISTER_CONSUMER_MILLISECONDS, ServerFlag.REGISTER_CONSUMER_MILLISECONDS, TimeUnit.MILLISECONDS);
    }

    @Override
    public void updateTopicRouteInfo(String topic, TopicRouteData routeData) {
     /*   if(routeData != null) {
            Set<RouteData> routeDataSet = new HashSet<>();
            List<BrokerData> brokerDataList = routeData.getBrokerDataList();
            List<TopicInfo> topicInfoList = routeData.getTopicInfoList();
            for(TopicInfo topicInfo : topicInfoList) {
                RouteData route = new RouteData();
                for (BrokerData brokerData : brokerDataList) {
                    if(brokerData.getBrokerName().equals(topicInfo.getBrokerName())) {
                        route.setBrokerData(brokerData);
                        break;
                    }
                }
                if(route.getBrokerData() != null) {
                    routeDataSet.add(route);
                }
            }
            topicRouteMap.put(topic, routeDataSet);
        }*/
    }

    //public
}
