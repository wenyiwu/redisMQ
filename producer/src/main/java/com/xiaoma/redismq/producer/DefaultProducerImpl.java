package com.xiaoma.redismq.producer;

import com.xiaoma.redismq.common.data.*;
import com.xiaoma.redismq.common.flag.ServerFlag;
import com.xiaoma.redismq.remoting.client.NameSrvClient;
import com.xiaoma.redismq.remoting.client.RouteService;
import com.xiaoma.redismq.remoting.netty.MstoreSrvClient;
import com.xiaoma.redismq.remoting.request.RouteInfoRequest;
import com.xiaoma.redismq.remoting.response.SendResult;
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
public class DefaultProducerImpl implements RouteService {

    private NameSrvClient nameSrvClient;

    private MstoreSrvClient mstoreSrvClient;

    private Set<String> topicSet;

    private Map<String /*topic*/, Set<RouteData> /*brokerName*/> topicRouteMap;

    //private MessageSendManager messageSendStore;

    private Map<String /*topic*/, MessageSendManager> sendManagerMap;

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "ProducerScheduledThread");
        }
    });

    public DefaultProducerImpl() {
        nameSrvClient = new NameSrvClient(this);

        nameSrvClient.addNameSrvAddr(ServerFlag.NAME_SERVER_ADDR);

        List<String> mstoreList = new LinkedList<>();

        mstoreList.add(ServerFlag.MSTORE_SERVER_ADDR);

        mstoreSrvClient = new MstoreSrvClient(mstoreList);

        topicSet = new HashSet<>();

        topicRouteMap = new HashMap<>();

        sendManagerMap = new HashMap<>();
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
                    mstoreSrvClient.registerProducer(ServerFlag.MSTORE_SERVER_ADDR, "REGISTER_PRODUCER");
                    System.out.println("registerProducer");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ServerFlag.REGISTER_CONSUMER_MILLISECONDS, ServerFlag.REGISTER_PRODUCER_MILLISECONDS, TimeUnit.MILLISECONDS);
    }

    //private Map<>
    @Override
    public void updateTopicRouteInfo(String topic, TopicRouteData routeData) {
        MessageSendManager messageSendManager = sendManagerMap.get(topic);
        if(messageSendManager == null) {
            messageSendManager = new MessageSendManager();

            sendManagerMap.put(topic, messageSendManager);
        }

        messageSendManager.updateMessageManager(routeData);
    }

    public SendResult send(String topic, String message) {
        SendResult result = null;
        try {
            MessageSendManager messageSendManager = sendManagerMap.get(topic);
            MessageData messageData = messageSendManager.send(message);
            result = mstoreSrvClient.sendMessageSync(messageData);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }
}
