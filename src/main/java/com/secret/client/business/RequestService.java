package com.secret.client.business;

import static com.secret.client.cassandra.ProgressStatus.ODM_RES;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.secret.client.cassandra.ProgressDao;
import com.secret.client.cassandra.RequestDao;
import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.mutation.Mutator;

public class RequestService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(GoOneStep.class);
    protected static int REQUEST_BATCH_SIZE = 200;

    protected RequestDao requestDao;
    protected ProgressDao progressDao;
    protected int maximumRowSize;
    protected int instancesCount;
    protected int loggingInterval;

    protected static AtomicLong globalRequestCount = new AtomicLong(0L);

    public RequestService(RequestDao requestDao, ProgressDao progressDao, int maximumRowSize, int instancesCount, int loggingInterval) {
        this.requestDao = requestDao;
        this.progressDao = progressDao;
        this.maximumRowSize = maximumRowSize;
        this.instancesCount = instancesCount;
        this.loggingInterval = loggingInterval;
    }

    public RequestStateHolder process(RequestStateHolder stateHolder, int partitionKeysCount) {
        int batchClientCount = stateHolder.getBatchClientCount();
        batchClientCount += partitionKeysCount;
        long requestColumnsCount = stateHolder.getRequestColumnsCount();
        int requestBucket = stateHolder.getRequestBucket();

        final long count = new Double(Math.floor(batchClientCount / REQUEST_BATCH_SIZE)).longValue();
        if (count >= 1) {

            final Mutator<String> requestMutator = requestDao.createMutator();
            final Mutator<Composite> progressMutator = progressDao.createMutator();

            for (int i = 0; i < count; i++) {
                requestColumnsCount++;

                if (requestColumnsCount > maximumRowSize) {
                    LOGGER.info("ODM_RES bucket transition: {} -> {} for requestColumnsCount {}", requestBucket, requestBucket + instancesCount, requestColumnsCount);
                    requestBucket += instancesCount;
                    // Re-init column count to 1 and not 0 because current insert is taken into account for next bucket
                    requestColumnsCount = 1;
                }
                final String requestPartitionKey = TimeUUIDUtils.getUniqueTimeUUIDinMillis().toString();
                requestDao.insertInputXOM(requestMutator, requestPartitionKey);
                progressDao.insertPartitionKeyForStatus(progressMutator, ODM_RES, requestBucket, requestPartitionKey);

            }
            periodicRequestLog(count);
            requestMutator.execute();
            progressMutator.execute();
            batchClientCount = batchClientCount % REQUEST_BATCH_SIZE;
        }

        return new RequestStateHolder(requestBucket, requestColumnsCount, batchClientCount);
    }

    private void periodicRequestLog(long count) {
        long requestCount;
        long previousRequestCount = globalRequestCount.get();
        requestCount = globalRequestCount.addAndGet(count);
        logForInterval(requestCount, previousRequestCount, "Inserted ODM_RES so far : {}", requestCount);
    }

    protected void logForInterval(long currentCount, long previousCount, String message, Object... params) {
        if (currentCount % loggingInterval <= previousCount % loggingInterval) {
            if (params != null && params.length > 0) {
                LOGGER.info(message, params);
            } else {
                LOGGER.info(message);
            }
        }
    }

    public static class RequestStateHolder {
        private final int requestBucket;
        private final long requestColumnsCount;
        private final int batchClientCount;

        public RequestStateHolder(int requestBucket, long requestColumnsCount, int batchClientCount) {
            this.requestBucket = requestBucket;
            this.requestColumnsCount = requestColumnsCount;
            this.batchClientCount = batchClientCount;
        }

        public int getBatchClientCount() {
            return batchClientCount;
        }

        public int getRequestBucket() {
            return requestBucket;
        }

        public long getRequestColumnsCount() {
            return requestColumnsCount;
        }
    }

}
