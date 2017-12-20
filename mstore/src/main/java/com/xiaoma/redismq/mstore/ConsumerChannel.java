package com.xiaoma.redismq.mstore;


import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsumerChannel {
    private Channel channel;

    private String clientAddr;

    public ConsumerChannel(Channel channel, String clientAddr) {
        this.channel = channel;
        this.clientAddr = clientAddr;
    }
}
