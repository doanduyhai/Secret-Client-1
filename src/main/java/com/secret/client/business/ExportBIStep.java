package com.secret.client.business;

import static com.secret.client.cassandra.ProgressStatus.EXPORT_BI;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.codehaus.jackson.map.ObjectMapper;
import com.secret.client.cassandra.ProgressDao;
import com.secret.client.cassandra.RequestDao;
import com.secret.client.vo.Statuses;
import me.prettyprint.hector.api.Keyspace;

public class ExportBIStep extends AbstractProgressStep {

    private static final String EXPORT_BI_SERVICE_FETCH_SIZE = "export.bi.step.fetch.size";
    private static final String EXPORT_BI_SERVICE_WAIT_MILLIS = "export.bi.step.wait.millis";
    private static final String EXPORT_BI_SERVICE_LOGGING_INTERVAL = "export.bi.step.logging.interval";
    public static final String EXPORT_BI_SERVICE_INSTANCES = "export.bi.step.instances";

    protected static AtomicLong globalRequestCount = new AtomicLong(0L);

    private RequestDao requestDao;

    public ExportBIStep(CountDownLatch globalLatch, int instanceId, Keyspace keyspace, ObjectMapper mapper, RequestDao requestDao) {
        super(globalLatch, instanceId, keyspace, mapper);
        this.requestDao = requestDao;
    }

    @Override
    protected void run() throws Exception {
        ExportBIStateHolder exportBIStateHolder = new ExportBIStateHolder(instanceId, 0, 0L);
        while (true) {
            if (shutdownCalled) {
                break;
            }
            exportBIStateHolder = process(exportBIStateHolder);
        }
    }

    protected ExportBIStateHolder process(ExportBIStateHolder exportBIStateHolder) throws InterruptedException {
        int bucket = exportBIStateHolder.getBucket();
        long columnsCount = exportBIStateHolder.getColumnsCount();
        long from = exportBIStateHolder.getFrom();

        final Statuses statuses = progressDao.findPartitionKeysByStatus(EXPORT_BI, bucket, from);
        List<String> partitionKeys = statuses.getPartitionKeys();
        final int partitionKeysCount = partitionKeys.size();
        if (partitionKeysCount > 0) {
            from = statuses.getLastTimeStamp();

            periodicRequestLog(partitionKeysCount);

            final long newColumnsCount = columnsCount + partitionKeysCount;
            if (newColumnsCount >= maximumRowSize) {
                LOGGER.info("EXPORT_BI bucket transition : {} -> {} for requestColumnCount {}", bucket, bucket + instancesCount,
                        newColumnsCount);
                bucket += instancesCount;

                // Compute column count for next bucket
                columnsCount = newColumnsCount - maximumRowSize;

            } else {
                columnsCount += partitionKeysCount;
            }

            for (String partitionKey : partitionKeys) {
                requestDao.readOutputXom(partitionKey);
            }
        } else {
            Thread.sleep(sleepDelay);
        }
        return new ExportBIStateHolder(bucket, columnsCount, from);
    }

    private void periodicRequestLog(int partitionKeysCount) {
        long previousRequestCount = globalRequestCount.get();
        long requestCount = globalRequestCount.addAndGet(partitionKeysCount);
        logForInterval(requestCount, previousRequestCount, "Found EXPORT_BI so far : {}", requestCount);
    }

    @Override
    protected void startUp() throws Exception {
        LOGGER.info("Starting ExportBIService");
        this.fetchSize = propertyLoader.getInt(EXPORT_BI_SERVICE_FETCH_SIZE);
        this.sleepDelay = propertyLoader.getInt(EXPORT_BI_SERVICE_WAIT_MILLIS);
        this.loggingInterval = propertyLoader.getInt(EXPORT_BI_SERVICE_LOGGING_INTERVAL);
        this.maximumRowSize = propertyLoader.getInt(MAXIMUM_ROW_SIZE);
        this.instancesCount = propertyLoader.getInt(EXPORT_BI_SERVICE_INSTANCES);
        this.progressDao = new ProgressDao(keyspace, fetchSize);
    }

    @Override
    protected void shutDown() throws Exception {
        LOGGER.info("Shutting down ExportBIService");
        globalLatch.countDown();
    }

    protected static class ExportBIStateHolder {
        private int bucket;
        private long columnsCount;
        private long from;

        public ExportBIStateHolder(int bucket, long columnsCount, long from) {
            this.bucket = bucket;
            this.columnsCount = columnsCount;
            this.from = from;
        }

        public int getBucket() {
            return bucket;
        }

        public long getColumnsCount() {
            return columnsCount;
        }

        public long getFrom() {
            return from;
        }
    }
}
