package com.secret.client.cassandra;

import static me.prettyprint.hector.api.factory.HFactory.createCounterColumnQuery;
import static me.prettyprint.hector.api.factory.HFactory.createCounterSliceQuery;
import static me.prettyprint.hector.api.factory.HFactory.createIndexedSlicesQuery;
import static me.prettyprint.hector.api.factory.HFactory.createSliceQuery;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import me.prettyprint.cassandra.serializers.BooleanSerializer;
import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.serializers.CompositeSerializer;
import me.prettyprint.cassandra.serializers.DateSerializer;
import me.prettyprint.cassandra.serializers.DoubleSerializer;
import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.ObjectSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.serializers.TimeUUIDSerializer;
import me.prettyprint.cassandra.serializers.UUIDSerializer;
import me.prettyprint.cassandra.service.ColumnSliceIterator;
import me.prettyprint.cassandra.service.KeyIterator;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.HCounterColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.CounterQuery;
import me.prettyprint.hector.api.query.SliceCounterQuery;
import me.prettyprint.hector.api.query.SliceQuery;

public abstract class GenericCassandraDao<K, N, V> {

    public static final StringSerializer STRING_SRZ = StringSerializer.get();
    public static final LongSerializer LONG_SRZ = LongSerializer.get();
    public static final IntegerSerializer INT_SRZ = IntegerSerializer.get();
    public static final UUIDSerializer UUID_SRZ = UUIDSerializer.get();
    public static final TimeUUIDSerializer TIMEUUID_SRZ = TimeUUIDSerializer.get();
    public static final CompositeSerializer COMPOSITE_SRZ = CompositeSerializer.get();
    public static final DateSerializer DATE_SRZ = DateSerializer.get();
    public static final DoubleSerializer DOUBLE_SRZ = DoubleSerializer.get();
    public static final ObjectSerializer OBJECT_SRZ = ObjectSerializer.get();
    public static final BytesArraySerializer BYTE_SRZ = BytesArraySerializer.get();
    public static final BooleanSerializer BOOLEAN_SRZ = BooleanSerializer.get();

    public Keyspace keyspace;

    protected Serializer<K> keySerializer;
    protected Serializer<N> columnNameSerializer;
    protected Serializer<V> valueSerializer;
    protected String columnFamily;

    public int DEFAULT_LENGTH = 50;

    private Function<Row<K, N, V>, K> rowToPartitionKeyFunction = new Function<Row<K, N, V>, K>() {
        @Override
        public K apply(Row<K, N, V> row) {
            return row.getKey();
        }
    };
    ;

    protected GenericCassandraDao(Keyspace keyspace) {
        this.keyspace = keyspace;
    }

    public void insertColumn(K key, N name, V value) {
        Mutator<K> mutator = HFactory.createMutator(keyspace, keySerializer);
        mutator.insert(key, columnFamily, HFactory.createColumn(name, value, columnNameSerializer, valueSerializer));
        mutator.execute();
    }

    public V getValue(K key, N name) {
        V result = null;
        HColumn<N, V> column = HFactory
                .createColumnQuery(keyspace, keySerializer, columnNameSerializer, valueSerializer)
                .setColumnFamily(columnFamily).setKey(key).setName(name).execute().get();
        if (column != null) {
            result = column.getValue();
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public List<HColumn<N, V>> getColumns(K key, List<N> names) {
        N[] columnsName = (N[]) names.toArray();
        List<HColumn<N, V>> columns = new ArrayList<HColumn<N, V>>();
        ColumnSlice<N, V> slices = HFactory
                .createSliceQuery(keyspace, keySerializer, columnNameSerializer, valueSerializer)
                .setColumnFamily(columnFamily).setKey(key).setColumnNames(columnsName).execute().get();

        if (slices.getColumns() != null && slices.getColumns().size() > 0) {
            columns = slices.getColumns();
        }

        return columns;
    }

    public void removeColumn(K key, N name) {
        Mutator<K> mutator = HFactory.createMutator(keyspace, keySerializer);
        mutator.delete(key, columnFamily, name, columnNameSerializer);
        mutator.execute();
    }

    public List<V> findValuesRange(K key, N startName, boolean reverse, int count) {
        List<V> values = new ArrayList<V>();

        List<HColumn<N, V>> columns = createSliceQuery(keyspace, keySerializer, columnNameSerializer, valueSerializer)
                .setColumnFamily(columnFamily).setKey(key).setRange(startName, null, reverse, count).execute().get()
                .getColumns();

        for (HColumn<N, V> column : columns) {
            values.add(column.getValue());
        }
        return values;
    }

    public List<N> findNamesRange(K key, N startName, boolean reverse, int count) {
        List<HColumn<N, V>> columns = createSliceQuery(keyspace, keySerializer, columnNameSerializer, valueSerializer)
                .setColumnFamily(columnFamily).setKey(key)
                .setRange(startName, null, reverse, count).execute().get()
                .getColumns();

        List<N> names = new ArrayList<N>();
        for (HColumn<N, V> column : columns) {
            if (column.getValue() != null) {
                names.add(column.getName());
            }
        }
        return names;
    }

    public List<N> findNamesRange(K key, N startName, N endName, boolean reverse, int count) {
        List<HColumn<N, V>> columns = createSliceQuery(keyspace, keySerializer, columnNameSerializer, valueSerializer)
                .setColumnFamily(columnFamily).setKey(key)
                .setRange(startName, endName, reverse, count).execute().get()
                .getColumns();

        List<N> names = new ArrayList<N>();
        for (HColumn<N, V> column : columns) {
            if (column.getValue() != null) {
                names.add(column.getName());
            }
        }
        return names;
    }

    public List<Pair<N, V>> findColumnsRange(K key, N startName, boolean reverse, int count) {
        return this.findColumnsRange(key, startName, (N) null, reverse, count);
    }

    public List<Pair<N, V>> findColumnsRange(K key, N startName, N endName, boolean reverse, int count) {
        List<HColumn<N, V>> results = createSliceQuery(keyspace, keySerializer, columnNameSerializer, valueSerializer)
                .setColumnFamily(columnFamily).setKey(key).setRange(startName, endName, reverse, count).execute()
                .get().getColumns();

        List<Pair<N, V>> columns = new ArrayList<Pair<N, V>>();
        for (HColumn<N, V> column : results) {
            columns.add(Pair.create(column.getName(), column.getValue()));
        }

        return columns;
    }

    public ColumnSliceIterator<K, N, V> getColumnsIterator(K key, N startName, boolean reverse, int length) {
        SliceQuery<K, N, V> query = createSliceQuery(keyspace, keySerializer, columnNameSerializer, valueSerializer)
                .setColumnFamily(columnFamily).setKey(key);

        return new ColumnSliceIterator<K, N, V>(query, startName, (N) null, reverse, length);
    }

    public ColumnSliceIterator<K, N, V> getColumnsIterator(K key, N startName, N endName, boolean reverse, int length) {
        SliceQuery<K, N, V> query = createSliceQuery(keyspace, keySerializer, columnNameSerializer, valueSerializer)
                .setColumnFamily(columnFamily).setKey(key);

        return new ColumnSliceIterator<K, N, V>(query, startName, endName, reverse, length);
    }

    public List<HCounterColumn<N>> findCounterColumnsRange(K key, N startName, boolean reverse, int size) {
        SliceCounterQuery<K, N> counterQuery = createCounterSliceQuery(keyspace, keySerializer, columnNameSerializer)
                .setColumnFamily(columnFamily).setKey(key);

        return counterQuery.setRange(startName, (N) null, reverse, size).execute().get().getColumns();
    }

    public Long getCounter(K key, N name) {
        CounterQuery<K, N> counterQuery = createCounterColumnQuery(keyspace, keySerializer, columnNameSerializer)
                .setKey(key).setName(name).setColumnFamily(columnFamily);

        final HCounterColumn<N> counterColumn = counterQuery.execute().get();
        return counterColumn != null ? counterColumn.getValue() : null;
    }

    public void removeRow(K key) {
        Mutator<K> mutator = HFactory.createMutator(keyspace, keySerializer);
        mutator.addDeletion(key, columnFamily);
        mutator.execute();
    }

    public void incrementCounter(K key, N name, Long value) {
        Mutator<K> mutator = HFactory.createMutator(keyspace, keySerializer);
        mutator.incrementCounter(key, columnFamily, name, value);
        mutator.execute();
    }

    public void decrementCounter(K key, N name, Long value) {
        Mutator<K> mutator = HFactory.createMutator(keyspace, keySerializer);
        mutator.decrementCounter(key, columnFamily, name, value);
        mutator.execute();

    }

    public void truncate() {
        final KeyIterator.Builder builder = new KeyIterator.Builder(keyspace, columnFamily, keySerializer);
        builder.maxRowCount(Integer.MAX_VALUE);
        Iterator<K> iterator = builder.build().iterator();
        while (iterator.hasNext()) {
            this.removeRow(iterator.next());
        }
    }

    public List<K> indexedQuery(N columnName, V value, int limit) {
        List<K> partitionKeys = new ArrayList<K>();

        final OrderedRows<K, N, V> rows = createIndexedSlicesQuery(keyspace, keySerializer, columnNameSerializer, valueSerializer)
                .setColumnFamily(columnFamily)
                .setRowCount(limit).addEqualsExpression(columnName, value).setReturnKeysOnly()
                .execute().get();

        if (rows != null && rows.getList() != null && rows.getList().size() > 0) {
            partitionKeys = FluentIterable.from(rows.getList()).transform(rowToPartitionKeyFunction).toList();
        }
        return partitionKeys;
    }

    public Mutator<K> createMutator() {
        return HFactory.createMutator(keyspace, keySerializer);
    }
}
