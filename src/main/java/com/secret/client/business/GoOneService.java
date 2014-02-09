package com.secret.client.business;

import com.secret.client.cassandra.ClientDao;
import com.secret.client.cassandra.ContractDao;
import com.secret.client.cassandra.ProgressDao;
import com.secret.client.cassandra.RequestDao;
import com.secret.client.vo.Statuses;
import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.mutation.Mutator;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import static com.secret.client.cassandra.ProgressStatus.CLIENT_IMPORTED;
import static com.secret.client.cassandra.ProgressStatus.ODM_RES;

public class GoOneService extends AbstractProgressService {
    protected static final Logger LOGGER = LoggerFactory.getLogger(GoOneService.class);

    private static final String GO_ONE_SERVICE_FETCH_SIZE = "go.one.service.fetch.size";
    private static final String GO_ONE_SERVICE_WAIT_IN_MILLIS = "go.one.service.wait.millis";
    private static final String GO_ONE_SERVICE_LOGGING_INTERVAL = "go.one.service.logging.interval";
    public static final String GO_ONE_SERVICE_INSTANCES = "go.one.service.instances";
    protected static int REQUEST_BATCH_SIZE = 200;

    protected static AtomicLong globalClientCount = new AtomicLong(0L);
    protected static AtomicLong globalRequestCount = new AtomicLong(0L);

    private RequestDao requestDao;

    protected int batchClientCount = 0;

    public GoOneService(CountDownLatch globalLatch, int instanceId, Keyspace keyspace, ObjectMapper mapper, RequestDao requestDao) {
        super(globalLatch, instanceId, keyspace, mapper);
        this.requestDao = requestDao;
    }

    @Override
    protected void run() throws Exception {
        int clientBucket = instanceId;
        long clientColumnsCount = 0;
        int requestBucket = instanceId;
        long requestColumnsCount = 0;

        Long from = 0L;
        while (true) {
            if (shutdownCalled) {
                break;
            }
            Counters counters = process(clientBucket, clientColumnsCount, requestBucket, requestColumnsCount, from);
            clientBucket = counters.clientBucket;
            clientColumnsCount = counters.clientColumnsCount;
            requestBucket = counters.requestBucket;
            requestColumnsCount = counters.requestColumnsCount;
        }

    }

    protected Counters process(int clientBucket, long clientColumnsCount, int requestBucket, long requestColumnsCount, long from) throws InterruptedException {
        final Statuses statuses = progressDao.findPartitionKeysByStatus(CLIENT_IMPORTED, clientBucket, from);
        List<String> partitionKeys = statuses.getPartitionKeys();

        final int partitionKeysCount = partitionKeys.size();

        if (partitionKeysCount > 0) {
            from = statuses.getLastTimeStamp();

            periodicClientLog(partitionKeysCount);

            final long newClientColumnCount = clientColumnsCount + partitionKeysCount;
            if (newClientColumnCount > maximumRowSize) {
                LOGGER.info("CLIENT_VALIDATED bucket transition : {} -> {} for clientColumnCount {}", clientBucket,
                        clientBucket + instancesCount, newClientColumnCount);

                clientBucket += instancesCount;

                // Compute column count for next bucket
                clientColumnsCount = newClientColumnCount - maximumRowSize;

            } else {
                clientColumnsCount += partitionKeysCount;
            }

            processClient(partitionKeys, 0, partitionKeysCount);

            batchClientCount += partitionKeysCount;

            final long count = new Double(Math.floor(batchClientCount / REQUEST_BATCH_SIZE)).longValue();
            if (count > 1) {

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
        } else {
            Thread.sleep(sleepDelay);
        }
        return new Counters(clientBucket, clientColumnsCount, requestBucket, requestColumnsCount, from);
    }

    private void periodicRequestLog(long count) {
        long requestCount;
        long previousRequestCount = globalRequestCount.get();
        requestCount = globalRequestCount.addAndGet(count);
        logForInterval(requestCount, previousRequestCount, "Inserted ODM_RES so far : {}", requestCount);
    }

    private void periodicClientLog(int partitionKeysCount) {
        long previousClientCount = globalClientCount.get();
        long clientCount = globalClientCount.addAndGet(partitionKeysCount);

        logForInterval(clientCount, previousClientCount, "Found CLIENT_VALIDATED so far : {}", clientCount);
    }

    private void processClient(List<String> partitionKeys, int from, int to) {
        for (int i = from; i < to; i++) {
            String partitionKey = partitionKeys.get(i);
            clientDao.readClient(partitionKey);
            contratDao.findContractsForClient(partitionKey);
        }
    }

    @Override
    protected void startUp() throws Exception {
        LOGGER.info("Starting GoOneService");
        this.fetchSize = propertyLoader.getInt(GO_ONE_SERVICE_FETCH_SIZE);
        this.sleepDelay = propertyLoader.getInt(GO_ONE_SERVICE_WAIT_IN_MILLIS);
        this.loggingInterval = propertyLoader.getInt(GO_ONE_SERVICE_LOGGING_INTERVAL);
        this.maximumRowSize = propertyLoader.getInt(MAXIMUM_ROW_SIZE);
        this.instancesCount = propertyLoader.getInt(GO_ONE_SERVICE_INSTANCES);
        this.clientDao = new ClientDao(keyspace, mapper);
        this.contratDao = new ContractDao(keyspace, mapper);
        this.progressDao = new ProgressDao(keyspace, fetchSize);
    }

    @Override
    protected void shutDown() throws Exception {
        LOGGER.info("Shutting down GoOneService");
        globalLatch.countDown();
    }

    protected static class Counters {
        private int clientBucket;
        private long clientColumnsCount;
        private int requestBucket;
        private long requestColumnsCount;
        private Long from;

        public Counters(int clientBucket, long clientColumnsCount, int requestBucket, long requestColumnsCount, Long from) {
            this.clientBucket = clientBucket;
            this.clientColumnsCount = clientColumnsCount;
            this.requestBucket = requestBucket;
            this.requestColumnsCount = requestColumnsCount;
            this.from = from;
        }

        public int getClientBucket() {
            return clientBucket;
        }

        public long getClientColumnsCount() {
            return clientColumnsCount;
        }

        public int getRequestBucket() {
            return requestBucket;
        }

        public long getRequestColumnsCount() {
            return requestColumnsCount;
        }

        public Long getFrom() {
            return from;
        }


    }
}
