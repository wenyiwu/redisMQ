package com.xiaoma.redismq.producer;

import com.xiaoma.redismq.common.data.BrokerData;
import com.xiaoma.redismq.common.data.RouteData;
import com.xiaoma.redismq.common.data.TopicInfo;
import com.xiaoma.redismq.common.data.TopicRouteData;
import com.xiaoma.redismq.remoting.client.NameSrvClient;
import com.xiaoma.redismq.remoting.client.RouteService;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class DefaultProducerImpl implements RouteService {

    private NameSrvClient nameSrvClient;

    private Set<String> topicSet;

    private Map<String /*topic*/, Set<RouteData> /*brokerName*/> topicRouteMap;

    public DefaultProducerImpl() {
        nameSrvClient = new NameSrvClient();

        topicSet = new HashSet<>();

        topicRouteMap = new HashMap<>();
    }

    //private Map<>
    @Override
    public void updateTopicRouteInfo(String topic, TopicRouteData routeData) {
        if(routeData != null) {
//            topicRouteMap.put(topic, )
            Set<RouteData> routeDataSet = new HashSet<>();
            List<TopicInfo> topicInfoList = routeData.getTopicInfoList();
            List<BrokerData> brokerDataList = routeData.getBrokerDataList();
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
        }
    }
}
