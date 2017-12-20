package com.xiaoma.redismq.common.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageData {
    private Message message;

    private String brokerAddr;
}
