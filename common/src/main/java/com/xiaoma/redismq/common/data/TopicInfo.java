package com.xiaoma.redismq.common.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopicInfo {
    private String topicName;

    private String brokerName;

    private int readQueueNum;

    private int writeQueueNum;

    private int perm;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TopicInfo other = (TopicInfo) obj;
        if (brokerName == null) {
            if (other.brokerName != null)
                return false;
        } else if (!brokerName.equals(other.brokerName))
            return false;
        if (perm != other.perm)
            return false;
        if (readQueueNum != other.readQueueNum)
            return false;
        if (writeQueueNum != other.writeQueueNum)
            return false;
        return true;
    }
}
