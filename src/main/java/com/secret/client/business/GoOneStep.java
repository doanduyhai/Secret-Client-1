package com.secret.client.business;

import static com.secret.client.business.RequestService.RequestStateHolder;
import static com.secret.client.cassandra.ProgressStatus.CLIENT_IMPORTED;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.secret.client.cassandra.ClientDao;
import com.secret.client.cassandra.ContractDao;
import com.secret.client.cassandra.ProgressDao;
import com.secret.client.cassandra.RequestDao;
import com.secret.client.vo.Statuses;
import me.prettyprint.hector.api.Keyspace;

public class GoOneStep extends AbstractProgressStep {
    protected static final Logger LOGGER = LoggerFactory.getLogger(GoOneStep.class);

    private static final String GO_ONE_SERVICE_FETCH_SIZE = "go.one.step.fetch.size";
    private static final String GO_ONE_SERVICE_WAIT_IN_MILLIS = "go.one.step.wait.millis";
    private static final String GO_ONE_SERVICE_LOGGING_INTERVAL = "go.one.step.logging.interval";
    public static final String GO_ONE_SERVICE_INSTANCES = "go.one.step.instances";

    protected static AtomicLong globalClientCount = new AtomicLong(0L);

    protected ClientContractMatchingService clientContractMatchingService;
    protected RequestService requestService;
    protected RequestDao requestDao;

    public GoOneStep(CountDownLatch globalLatch, int instanceId, Keyspace keyspace, ObjectMapper mapper, RequestDao requestDao) {
        super(globalLatch, instanceId, keyspace, mapper);
        this.requestDao = requestDao;
        this.clientContractMatchingService = new ClientContractMatchingService(keyspace, mapper, instanceId);
    }

    @Override
    protected void run() throws Exception {

        GoOneStateHolder goOneStateHolder = new GoOneStateHolder(instanceId, 0L, new RequestStateHolder(instanceId, 0L, 0), 0L, null);
        while (true) {
            if (shutdownCalled) {
                break;
            }
            goOneStateHolder = process(goOneStateHolder);
        }

    }

    protected GoOneStateHolder process(GoOneStateHolder goOneStateHolder) throws InterruptedException {
        Long from = goOneStateHolder.getFrom();
        int clientBucket = goOneStateHolder.getClientBucket();
        long clientColumnsCount = goOneStateHolder.getClientColumnsCount();
        RequestStateHolder requestStateHolder = goOneStateHolder.getRequestStateHolder();

        final Statuses statuses = progressDao.findPartitionKeysByStatus(CLIENT_IMPORTED, clientBucket, from);
        List<String> partitionKeys = statuses.getPartitionKeys();

        final int partitionKeysCount = partitionKeys.size();

        if (partitionKeysCount > 0) {
            from = statuses.getLastTimeStamp();

            periodicClientLog(partitionKeysCount);

            final long newClientColumnCount = clientColumnsCount + partitionKeysCount;

            // Check whether there are extra client for this current bucket
            if (clientColumnsCount < maximumRowSize && newClientColumnCount >= maximumRowSize) {
                goOneStateHolder.setExtraClientCount(clientContractMatchingService.getExtraClientsForBucket(clientBucket));
            }

            long maxPartitionSize = maximumRowSize + goOneStateHolder.getSafeExtraClientCount();
            if (newClientColumnCount > maxPartitionSize) {

                LOGGER.info("CLIENT_IMPORTED bucket transition : {} -> {} for clientColumnCount {}", clientBucket,
                        clientBucket + instancesCount, newClientColumnCount);

                clientBucket += instancesCount;

                // Compute column count for next bucket
                // clientColumnsCount = newClientColumnCount - maximumRowSize;

                // Reset client column count
                clientColumnsCount = 0;

            } else {
                clientColumnsCount = newClientColumnCount;
            }

            clientContractMatchingService.processClient(partitionKeys, clientBucket, instancesCount);
            requestStateHolder = requestService.process(requestStateHolder, partitionKeysCount);

        } else {
            Thread.sleep(sleepDelay);
        }
        return goOneStateHolder.duplicate(clientBucket, clientColumnsCount, requestStateHolder, from);
    }

    private void periodicClientLog(int partitionKeysCount) {
        long previousClientCount = globalClientCount.get();
        long clientCount = globalClientCount.addAndGet(partitionKeysCount);

        logForInterval(clientCount, previousClientCount, "Found CLIENT_IMPORTED so far : {}", clientCount);
    }

    @Override
    protected void startUp() throws Exception {
        LOGGER.info("Starting GoOneStep");
        this.fetchSize = propertyLoader.getInt(GO_ONE_SERVICE_FETCH_SIZE);
        this.sleepDelay = propertyLoader.getInt(GO_ONE_SERVICE_WAIT_IN_MILLIS);
        this.loggingInterval = propertyLoader.getInt(GO_ONE_SERVICE_LOGGING_INTERVAL);
        this.maximumRowSize = propertyLoader.getInt(MAXIMUM_ROW_SIZE);
        this.instancesCount = propertyLoader.getInt(GO_ONE_SERVICE_INSTANCES);
        this.clientDao = new ClientDao(keyspace, mapper);
        this.contractDao = new ContractDao(keyspace, mapper);
        this.progressDao = new ProgressDao(keyspace, fetchSize);
        this.requestService = new RequestService(requestDao, progressDao, maximumRowSize, instancesCount, loggingInterval);
    }

    @Override
    protected void shutDown() throws Exception {
        LOGGER.info("Shutting down GoOneStep");
        globalLatch.countDown();
    }

    protected static class GoOneStateHolder {
        private int clientBucket;
        private long clientColumnsCount;
        private RequestStateHolder requestStateHolder;
        private Long from;
        private Long extraClientCount;

        public GoOneStateHolder(int clientBucket, long clientColumnsCount, RequestStateHolder requestStateHolder, Long from, Long extraClientCount) {
            this.clientBucket = clientBucket;
            this.clientColumnsCount = clientColumnsCount;
            this.requestStateHolder = requestStateHolder;
            this.from = from;
            this.extraClientCount = extraClientCount;
        }

        public GoOneStateHolder duplicate(int clientBucket, long clientColumnsCount, RequestStateHolder requestStateHolder, Long from) {
            return new GoOneStateHolder(clientBucket, clientColumnsCount, requestStateHolder, from, extraClientCount);
        }

        public int getClientBucket() {
            return clientBucket;
        }

        public long getClientColumnsCount() {
            return clientColumnsCount;
        }

        public RequestStateHolder getRequestStateHolder() {
            return requestStateHolder;
        }

        public Long getFrom() {
            return from;
        }

        public long getSafeExtraClientCount() {
            return extraClientCount != null ? extraClientCount : 0L;
        }

        public void setExtraClientCount(Long extraClientCount) {
            this.extraClientCount = extraClientCount;
        }
    }
}
