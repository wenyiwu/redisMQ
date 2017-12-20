package com.xiaoma.redismq.common.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
    private int queueId;

    private String topic;

    private int messageId;

    private String message;
}
