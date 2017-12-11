package com.xiaoma.redismq.remoting.process;

public class ResponseCode {
    /**
     * 成功
     */
    public static final int SUCCESS = 0;
    /**
     * 系统异常
     */
    public static final int SYSTEM_ERROR = 1;

    public static final int SYSTEM_BUSY = 2;

    public static final int REQUEST_CODE_NOT_SUPPORTED = 3;

    public static final int TRANSACTION_FAILED = 4;

    public static final int TOPIC_NOT_EXIST = 5;

}
