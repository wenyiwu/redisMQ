package com.xiaoma.redismq.common.data;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrokerLiveInfo {
    /**
     * 最后更新时间
     */
    private long lastUpdateTimestamp;
    /**
     * 连接信息
     */
    private Channel channel;
    /**
     * ha服务器地址
     */
    private String haServerAddr;
}
