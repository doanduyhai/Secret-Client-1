package com.secret.client.cassandra;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

public class RequestDao extends GenericCassandraDao<String,String,byte[]> {

    private static final String COLUMN_FAMILY = "odmResIO";
    private static final String XOM_INPUT = "input";
    private static final String XOM_OUTPUT = "output";
    private final byte[] inputXom;
    private final byte[] outputXom;


    public RequestDao(Keyspace keyspace,String inputXomPath,String outputXomPath) throws IOException {
        super(keyspace);
        this.inputXom = loadFiles(inputXomPath);
        this.outputXom = loadFiles(outputXomPath);
        keySerializer = STRING_SRZ;
        columnNameSerializer = STRING_SRZ;
        valueSerializer = BYTE_SRZ;
        columnFamily = COLUMN_FAMILY;

    }

    public void insertInputXOM(Mutator<String> mutator,String partitionKey) {
        mutator.addInsertion(partitionKey, columnFamily, HFactory.createColumn(XOM_INPUT,inputXom, columnNameSerializer, valueSerializer));

    }

    public void insertOutputXOM(Mutator<String> mutator,String partitionKey) {
        mutator.addInsertion(partitionKey, columnFamily, HFactory.createColumn(XOM_OUTPUT, outputXom, columnNameSerializer, valueSerializer));
    }

    public void readOutputXom(String partitionKey) {
        getValue(partitionKey,XOM_OUTPUT);
    }

    private byte[] loadFiles(String path) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(path, "r");
        FileChannel inChannel = randomAccessFile.getChannel();
        MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        buffer.load();
        inChannel.close();
        randomAccessFile.close();
        byte[] payload = new byte[buffer.remaining()];
        buffer.get(payload);
        return payload;
    }

}
