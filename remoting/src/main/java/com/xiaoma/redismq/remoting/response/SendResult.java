package com.xiaoma.redismq.remoting.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendResult {
    int messageId;

    public SendResult(int messageId) {
        this.messageId = messageId;
    }
}
