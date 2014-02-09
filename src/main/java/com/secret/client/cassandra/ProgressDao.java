package com.secret.client.cassandra;

import static com.google.common.collect.FluentIterable.from;
import static me.prettyprint.hector.api.factory.HFactory.createColumn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.secret.client.utils.UUIDGen;
import com.secret.client.vo.Statuses;
import com.google.common.base.Function;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.AbstractComposite;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.mutation.Mutator;


public class ProgressDao extends GenericCassandraDao<Composite,Composite,String>{

    private static final String COLUMN_FAMILY = "progress";

    private int fetchSize;

    private final Function<Pair<Composite,String>,String> pairToValueFn = new Function<Pair<Composite, String>, String>() {
        @Override
        public String apply(Pair<Composite, String> pair) {
            return pair.right;
        }
    };

    public ProgressDao(Keyspace keyspace,int fetchSize) {
        super(keyspace);
        this.fetchSize = fetchSize;
        keySerializer = COMPOSITE_SRZ;
        columnNameSerializer = COMPOSITE_SRZ;
        valueSerializer = STRING_SRZ;
        columnFamily = COLUMN_FAMILY;
    }

    public void insertPartitionKeysForStatus(ProgressStatus status, int bucket,List<String> partitionKeys) {
        Composite keyComposite = new Composite();
        keyComposite.setComponent(0, status.name(), STRING_SRZ);
        keyComposite.setComponent(1, bucket, INT_SRZ);

        final Mutator<Composite> mutator = createMutator();
        for(String numeroClient:partitionKeys) {
            Composite name = new Composite();
            name.setComponent(0, UUIDGen.increasingMicroTimestamp(),LONG_SRZ);
            mutator.addInsertion(keyComposite, COLUMN_FAMILY, createColumn(name, numeroClient, COMPOSITE_SRZ, STRING_SRZ));
        }
        mutator.execute();
    }

    public void insertPartitionKeyForStatus(ProgressStatus status, int bucket,String partitionKey) {
        insertPartitionKeysForStatus(status,bucket, Arrays.asList(partitionKey));
    }

    public void insertPartitionKeyForStatus(Mutator<Composite> mutator, ProgressStatus status, int bucket,String partitionKey) {
        Composite keyComposite = new Composite();
        keyComposite.setComponent(0, status.name(), STRING_SRZ);
        keyComposite.setComponent(1, bucket, INT_SRZ);
        Composite name = new Composite();
        name.setComponent(0, UUIDGen.increasingMicroTimestamp(),LONG_SRZ);
        mutator.addInsertion(keyComposite, COLUMN_FAMILY, createColumn(name, partitionKey, COMPOSITE_SRZ, STRING_SRZ));
    }

    public Statuses findPartitionKeysByStatus(ProgressStatus status, int bucket,long from) {
        Statuses result = new Statuses(null,new ArrayList<String>());
        Composite keyComposite = new Composite();
        keyComposite.setComponent(0, status.name(), STRING_SRZ);
        keyComposite.setComponent(1, bucket, INT_SRZ);
        Composite startName = new Composite();
        startName.setComponent(0, from, LONG_SRZ, LONG_SRZ.getComparatorType().getTypeName(),
                             AbstractComposite.ComponentEquality.GREATER_THAN_EQUAL);

        List<Pair<Composite, String>> columnsRange = findColumnsRange(keyComposite, startName, null, false,
                                                                            fetchSize);
        if(columnsRange!= null && columnsRange.size()>0) {
            final List<String> numerosClient = from(columnsRange).transform(pairToValueFn).toList();
            final Long lastDate = from(columnsRange).last().get().left.get(0, LONG_SRZ);
            result = new Statuses(lastDate,numerosClient);
        }
        return result;
    }
}
