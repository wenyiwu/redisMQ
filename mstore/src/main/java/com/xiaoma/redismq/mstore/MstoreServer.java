package com.xiaoma.redismq.mstore;

import com.xiaoma.redismq.common.queue.MessageQueue;

import java.util.Map;

public class MstoreServer {
    private Map<Integer /*MessageQueueId*/, MessageQueue> mQueueMap;
}
