package com.xiaoma.redismq.remoting.process;

public class RequestCode {
    //nameserver code
    public final static int REGISTER_BROKER = 101;

    public final static int UNREGISTER_BROKER = 102;

    public final static int GET_ROUTEINFO_BY_TOPIC = 103;

    //broker
    public final static int REGISTER_CONSUMER = 104;

    public final static int REGISTER_PRODUCER = 105;

    public final static int CONSUMER_MESSAGE = 106;

    public final static int PRODUCER_MESSAGE = 107;

    public final static int CONSUMER_CHANGE_MESSAGE_QUEUE = 108;

    public final static int CONSUMER_SUCCESS = 110;

    //consumer
    public final static int MESSAGE_QUEUE_CHANGED = 111;

    //producer
    public final static int PRODUCER_SUCCESS = 112;
}
