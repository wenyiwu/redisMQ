package com.xiaoma.redismq.remoting.process;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
public class RemotingCommand {
    //request type
    private int code;
    //message code
    private int type;
    private Object body;
    private String error;
    private String clientAddr;
    //private static AtomicInteger requestId = new AtomicInteger(0);

    private static ThreadLocal<AtomicInteger> requestId = new ThreadLocal<>();

    private int responseId;

    public RemotingCommand() {
        if(requestId.get() == null) {
            requestId.set(new AtomicInteger(0));
        }
        responseId  = requestId.get().incrementAndGet();
    }

    public RemotingCommand(int code, Object body) {
        this.code = code;
        this.body = body;
    }

    public RemotingCommand(int code, String error) {
        this.code = code;
        this.error = error;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
