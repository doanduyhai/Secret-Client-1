package com.secret.client.business;

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

    private static final String GO_ONE_STEP_FETCH_SIZE = "go.one.step.fetch.size";
    private static final String GO_ONE_STEP_WAIT_IN_MILLIS = "go.one.step.wait.millis";
    private static final String GO_ONE_STEP_LOGGING_INTERVAL = "go.one.step.logging.interval";


    protected static AtomicLong globalClientCount = new AtomicLong(0L);

    protected ClientContractMatchingService clientContractMatchingService;
    protected RequestService requestService;
    protected RequestDao requestDao;

    public GoOneStep(CountDownLatch globalLatch, int instanceId, Keyspace keyspace, ObjectMapper mapper, RequestDao requestDao) {
        super(globalLatch, instanceId, keyspace, mapper);
        this.requestDao = requestDao;
        this.clientContractMatchingService = new ClientContractMatchingService(keyspace, mapper);
    }

    @Override
    protected void run() throws Exception {

        GoOneStateHolder goOneStateHolder = new GoOneStateHolder(0, 0L);
        while (true) {
            if (shutdownCalled) {
                break;
            }
            goOneStateHolder = process(goOneStateHolder);
        }

    }

    protected GoOneStateHolder process(GoOneStateHolder goOneStateHolder) throws InterruptedException {
        Long from = goOneStateHolder.getFrom();
        int newBatchClientCount = goOneStateHolder.getBatchClientCount();

        final Statuses statuses = progressDao.findPartitionKeysByStatus(CLIENT_IMPORTED, instanceId, from);
        List<String> partitionKeys = statuses.getPartitionKeys();

        final int partitionKeysCount = partitionKeys.size();

        if (partitionKeysCount > 0) {
            from = statuses.getLastTimeStamp();

            periodicClientLog(partitionKeysCount);

            clientContractMatchingService.processClient(partitionKeys);
            newBatchClientCount = requestService.process(newBatchClientCount, partitionKeysCount);

        } else {
            Thread.sleep(sleepDelay);
        }
        return new GoOneStateHolder(newBatchClientCount, from);
    }

    private void periodicClientLog(int partitionKeysCount) {
        long previousClientCount = globalClientCount.get();
        long clientCount = globalClientCount.addAndGet(partitionKeysCount);

        logForInterval(clientCount, previousClientCount, "Found CLIENT_IMPORTED so far : {}", clientCount);
    }

    @Override
    protected void startUp() throws Exception {
        LOGGER.info("Starting GoOneStep");
        this.fetchSize = propertyLoader.getInt(GO_ONE_STEP_FETCH_SIZE);
        this.sleepDelay = propertyLoader.getInt(GO_ONE_STEP_WAIT_IN_MILLIS);
        this.loggingInterval = propertyLoader.getInt(GO_ONE_STEP_LOGGING_INTERVAL);
        this.clientDao = new ClientDao(keyspace, mapper);
        this.contractDao = new ContractDao(keyspace, mapper);
        this.progressDao = new ProgressDao(keyspace, fetchSize);
        this.requestService = new RequestService(requestDao, progressDao, instanceId, loggingInterval);
    }

    @Override
    protected void shutDown() throws Exception {
        clientContractMatchingService.showClientWithoutContract();
        LOGGER.info("Shutting down GoOneStep");
        globalLatch.countDown();
    }

    protected static class GoOneStateHolder {
        private int batchClientCount;
        private Long from;

        public GoOneStateHolder(int batchClientCount, Long from) {
            this.batchClientCount = batchClientCount;
            this.from = from;
        }

        public int getBatchClientCount() {
            return batchClientCount;
        }

        public Long getFrom() {
            return from;
        }
    }
}
