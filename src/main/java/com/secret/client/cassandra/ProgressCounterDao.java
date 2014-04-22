package com.secret.client.cassandra;


import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Composite;

public class ProgressCounterDao extends GenericCassandraDao<Composite, Integer, Long> {

    private static final String COLUMN_FAMILY = "ProgressCounter";
    private final int instanceId;

    public ProgressCounterDao(Keyspace keyspace, int instanceId) {
        super(keyspace);
        this.instanceId = instanceId;
        keySerializer = COMPOSITE_SRZ;
        columnNameSerializer = INT_SRZ;
        valueSerializer = LONG_SRZ;
        columnFamily = COLUMN_FAMILY;
    }

    public Long readCounter(ProgressStatus status, int bucket) {
        Composite keyComposite = new Composite();
        keyComposite.setComponent(0, status.name(), STRING_SRZ);
        keyComposite.setComponent(1, instanceId, INT_SRZ);
        return getCounter(keyComposite, bucket);
    }

    public void incrementCounter(ProgressStatus status, int bucket) {
        Composite keyComposite = new Composite();
        keyComposite.setComponent(0, status.name(), STRING_SRZ);
        keyComposite.setComponent(1, instanceId, INT_SRZ);
        super.incrementCounter(keyComposite, bucket, 1L);
    }
}
