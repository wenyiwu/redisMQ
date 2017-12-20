package com.xiaoma.redismq.namesrv.routeinfo;

import com.xiaoma.redismq.common.data.*;
import com.xiaoma.redismq.common.flag.BrokerFlag;
import com.xiaoma.redismq.common.flag.CharFlag;
import com.xiaoma.redismq.remoting.response.BrokerSlaveResult;
import com.xiaoma.redismq.remoting.request.RegisterBrokerRequest;
import io.netty.channel.Channel;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class RouteInfoManager {

    private HashMap<String /*topic*/, List<TopicInfo>> topicQueueMap;

//    private HashMap<String /*clusterName*/, Set<String /*brokerName*/>> clusterInfoMap;

    private HashMap<String /*brokerName*/, BrokerData> brokerDataMap;

    private HashMap<String /*clientAddr*/, String /*serverAddr*/> addrSwitchMap;

//    private HashMap<String /*brokerAddr*/, BrokerLiveInfo> brokerLiveInfoMap;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public RouteInfoManager() {
        topicQueueMap = new HashMap<>();
//        clusterInfoMap = new HashMap<>();
        brokerDataMap = new HashMap<>();
//        brokerLiveInfoMap = new HashMap<>();
        addrSwitchMap = new HashMap<>();
    }


    public BrokerSlaveResult registerBroker(RegisterBrokerRequest brokerRequest, String clientAddr) {

        final String clusterName = brokerRequest.getClusterName();
        final String brokerName = brokerRequest.getBrokerName();
        final String brokerAddr = brokerRequest.getBrokerAddr();
        final int brokerId = brokerRequest.getBrokerId();
        final BrokerTopicInfo topicInfo = brokerRequest.getTopicInfo();
        BrokerSlaveResult slaveResult = new BrokerSlaveResult();

        try{
            lock.writeLock().lockInterruptibly();
         /*   //先更新 clusterInfoMap
            Set<String> brokerNameSet = clusterInfoMap.get(clusterName);
            if(brokerNameSet ==  null) {
                brokerNameSet = new HashSet<>();
                clusterInfoMap.put(clusterName, brokerNameSet);
            }
            brokerNameSet.add(brokerName);*/

            //更新 addrSwitchMap
            addrSwitchMap.put(clientAddr, brokerAddr);

            //更新 brokerDataMap
            boolean isFirstRegister = false;
            BrokerData brokerData = brokerDataMap.get(brokerName);
            if(brokerData == null) {
                isFirstRegister = true;
                brokerData = new BrokerData(clusterName, brokerName);
                brokerDataMap.put(brokerName, brokerData);
            }
            isFirstRegister = brokerData.setBrokerInfo(brokerId, brokerAddr) || isFirstRegister;

            //更新 topicQueueMap
            if(topicInfo != null && brokerId == BrokerFlag.BROKER_MASTER && isFirstRegister) {
                HashMap<String, TopicInfo> tcTable =
                        topicInfo.getTopicMap();
                if (tcTable != null) {
                    tcTable.forEach((key, value) -> this.createAndUpdateQueueData(brokerName, value));
                }
            }

            if(brokerId == BrokerFlag.BROKER_SLAVE) {
                Set<String> masterAddrSet = brokerData.getBrokerInfoMap().get(BrokerFlag.BROKER_MASTER);

                StringBuffer masterAddr = new StringBuffer();

                for(String addr : masterAddrSet) {
                    masterAddr.append(addr);
                    masterAddr.append(CharFlag.SPACE_FLAG);
                }

                slaveResult.setBrokerName(brokerName);
                slaveResult.setMasterAddrs(masterAddrSet.toString());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }

        return slaveResult;
    }

    private void createAndUpdateQueueData(String brokerName, TopicInfo topicInfo) {
        String topicName = topicInfo.getTopicName();
        List<TopicInfo> topicInfoList = topicQueueMap.get(topicName);
        if(topicInfoList == null) {
            topicInfoList = new LinkedList<>();
            topicInfoList.add(topicInfo);
            topicQueueMap.put(topicName, topicInfoList);
        } else {
            boolean isNewData = true;
            Iterator<TopicInfo> it = topicInfoList.iterator();
            while (it.hasNext()) {
                TopicInfo qd = it.next();
                if (qd.getBrokerName().equals(brokerName)) {
                    if (qd.equals(topicInfo)) {
                        isNewData = false;
                    } else {
                        it.remove();
                    }
                }
            }
            if(isNewData) {
                topicInfoList.add(topicInfo);
            }
        }
    }

    public void unregisterBroker(
            String clusterName,
            String brokerName,
            String brokerAddr,
            int brokerId
    ) {
        try {
            lock.writeLock().lockInterruptibly();
            String topicName;
         /*   // 移除集群信息
            Set<String> brokerNameSet = clusterInfoMap.get(clusterName);
            if(brokerNameSet != null) {
                brokerNameSet.remove(brokerName);
            }*/

            //移除broker信息
            BrokerData brokerData = brokerDataMap.get(brokerName);
            if(brokerData != null) {
                brokerData.removeBrokeInfo(brokerId, brokerAddr);
                if(brokerData.getSize() == 0) {
                    brokerDataMap.remove(brokerName);
                }
            }

            //移除topic信息
            removeTopicInfoByBrokerName(brokerName);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void removeTopicInfoByBrokerName(String brokerName) {
        topicQueueMap.forEach((topicName, topicInfoList)-> {
            Iterator<TopicInfo> iter = topicInfoList.iterator();
            while(iter.hasNext()) {
                TopicInfo qd = iter.next();
                if (qd.getBrokerName().equals(brokerName)) {
                    iter.remove();
                    return;
                }
            }
        });
    }

    //private
    public TopicRouteData getTopicRouteDataByTopicName(String topicName) {
        TopicRouteData topicRouteData = new TopicRouteData();
        List<BrokerTopicResult> brokerTopicResults = new LinkedList<>();
        try {
            lock.readLock().lockInterruptibly();
            List<TopicInfo> topicInfoList = topicQueueMap.get(topicName);
            if(topicInfoList != null) {
                for (TopicInfo topicInfo : topicInfoList) {
                    BrokerData brokerData = brokerDataMap.get(topicInfo.getBrokerName());
                    if(brokerData != null) {
                        BrokerTopicResult brokerTopicResult = new BrokerTopicResult(topicInfo, brokerData);
                        brokerTopicResults.add(brokerTopicResult);
                    }
                }
                topicRouteData.setBrokerTopicResultList(brokerTopicResults);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.readLock().unlock();
        }
        return topicRouteData;
    }

    public void onChannelDestroy(String remoteAddr, Channel channel) {
        if (remoteAddr != null && remoteAddr.length() > 0) {
            try {
                try {
                    this.lock.writeLock().lockInterruptibly();
                    String brokerNameFound = null;
                    boolean removeBrokerName = false;

                    String serverAddr = addrSwitchMap.remove(remoteAddr);

                    Iterator<Map.Entry<String, BrokerData>> itBrokerAddrTable =
                            this.brokerDataMap.entrySet().iterator();
                    while (itBrokerAddrTable.hasNext() && (null == brokerNameFound)) {
                        BrokerData brokerData = itBrokerAddrTable.next().getValue();

                        Iterator<Map.Entry<Integer, Set<String>>> it = brokerData.getBrokerInfoMap().entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<Integer, Set<String>> entry = it.next();
                            Integer brokerId = entry.getKey();
                            Set<String> brokerAddrSet = entry.getValue();
                            Iterator<String> iter = brokerAddrSet.iterator();
                            while(iter.hasNext()) {
                                String brokerAddr = iter.next();
                                if (brokerAddr.equals(serverAddr)) {
                                    brokerNameFound = brokerData.getBrokerName();
                                    iter.remove();
                                    break;
                                }
                            }
                        }

                        if (brokerData.getBrokerInfoMap().isEmpty()) {
                            removeBrokerName = true;
                            itBrokerAddrTable.remove();
                        }
                    }

/*                    if (brokerNameFound != null && removeBrokerName) {
                        Iterator<Map.Entry<String, Set<String>>> it = this.clusterInfoMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<String, Set<String>> entry = it.next();
                            String clusterName = entry.getKey();
                            Set<String> brokerNames = entry.getValue();
                            boolean removed = brokerNames.remove(brokerNameFound);
                            if (removed && brokerNames.isEmpty()) {
                                it.remove();
                                break;
                            }
                        }
                    }*/

                    if (removeBrokerName) {
                        Iterator<Map.Entry<String, List<TopicInfo>>> itTopicQueueTable =
                                this.topicQueueMap.entrySet().iterator();
                        while (itTopicQueueTable.hasNext()) {
                            Map.Entry<String, List<TopicInfo>> entry = itTopicQueueTable.next();
                            String topic = entry.getKey();
                            List<TopicInfo> queueDataList = entry.getValue();

                            Iterator<TopicInfo> itQueueData = queueDataList.iterator();
                            while (itQueueData.hasNext()) {
                                TopicInfo queueData = itQueueData.next();
                                if (queueData.getBrokerName().equals(brokerNameFound)) {
                                    itQueueData.remove();
                                }
                            }

                            if (queueDataList.isEmpty()) {
                                itTopicQueueTable.remove();
                            }
                        }
                    }
                } finally {
                    this.lock.writeLock().unlock();
                }
            } catch (Exception e) {
            }
        }
    }
}
