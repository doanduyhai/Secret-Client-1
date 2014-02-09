package com.secret.client.vo;

import java.util.List;

public class Statuses {

    private Long lastTimeStamp;

    private List<String> partitionKeys;

    public Statuses(Long lastTimeStamp, List<String> partitionKeys) {
        this.lastTimeStamp = lastTimeStamp;
        this.partitionKeys = partitionKeys;
    }

    public Long getLastTimeStamp() {
        return lastTimeStamp;
    }

    public List<String> getPartitionKeys() {
        return partitionKeys;
    }
}
