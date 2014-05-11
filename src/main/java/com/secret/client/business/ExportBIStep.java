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

    private static final String EXPORT_BI_STEP_FETCH_SIZE = "export.bi.step.fetch.size";
    private static final String EXPORT_BI_STEP_WAIT_MILLIS = "export.bi.step.wait.millis";
    private static final String EXPORT_BI_STEP_LOGGING_INTERVAL = "export.bi.step.logging.interval";

    protected static AtomicLong globalRequestCount = new AtomicLong(0L);

    private RequestDao requestDao;

    public ExportBIStep(CountDownLatch globalLatch, int instanceId, Keyspace keyspace, ObjectMapper mapper, RequestDao requestDao) {
        super(globalLatch, instanceId, keyspace, mapper);
        this.requestDao = requestDao;
    }

    @Override
    protected void run() throws Exception {
        Long from = 0L;
        while (true) {
            if (shutdownCalled) {
                break;
            }
            from = process(from);
        }
    }

    protected long process(long oldFrom) throws InterruptedException {
        long from = oldFrom;

        final Statuses statuses = progressDao.findPartitionKeysByStatus(EXPORT_BI, instanceId, from);
        List<String> partitionKeys = statuses.getPartitionKeys();
        final int partitionKeysCount = partitionKeys.size();
        if (partitionKeysCount > 0) {
            from = statuses.getLastTimeStamp();

            periodicRequestLog(partitionKeysCount);

            for (String partitionKey : partitionKeys) {
                requestDao.readOutputXom(partitionKey);
            }
        } else {
            Thread.sleep(sleepDelay);
        }
        return from;
    }

    private void periodicRequestLog(int partitionKeysCount) {
        long previousRequestCount = globalRequestCount.get();
        long requestCount = globalRequestCount.addAndGet(partitionKeysCount);
        logForInterval(requestCount, previousRequestCount, "Found EXPORT_BI so far : {}", requestCount);
    }

    @Override
    protected void startUp() throws Exception {
        LOGGER.info("Starting ExportBIService");
        this.fetchSize = propertyLoader.getInt(EXPORT_BI_STEP_FETCH_SIZE);
        this.sleepDelay = propertyLoader.getInt(EXPORT_BI_STEP_WAIT_MILLIS);
        this.loggingInterval = propertyLoader.getInt(EXPORT_BI_STEP_LOGGING_INTERVAL);
        this.progressDao = new ProgressDao(keyspace, fetchSize);
    }

    @Override
    protected void shutDown() throws Exception {
        LOGGER.info("Shutting down ExportBIService");
        globalLatch.countDown();
    }
}
