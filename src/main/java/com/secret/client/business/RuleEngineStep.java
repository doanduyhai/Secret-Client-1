package com.secret.client.business;

import static com.secret.client.cassandra.ProgressStatus.EXPORT_BI;
import static com.secret.client.cassandra.ProgressStatus.ODM_RES;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.secret.client.cassandra.ProgressDao;
import com.secret.client.cassandra.RequestDao;
import com.secret.client.vo.Statuses;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.mutation.Mutator;

public class RuleEngineStep extends AbstractProgressStep {

    protected static final Logger LOGGER = LoggerFactory.getLogger(RuleEngineStep.class);

    private static final String RULE_ENGINE_STEP_FETCH_SIZE = "rule.engine.step.fetch.size";
    private static final String RULE_ENGINE_STEP_WAIT_IN_MILLIS = "rule.engine.step.wait.millis";
    private static final String RULE_ENGINE_STEP_LOGGING_INTERVAL = "rule.engine.step.logging.interval";

    protected static AtomicLong globalRequestCount = new AtomicLong(0L);

    private RequestDao requestDao;

    public RuleEngineStep(CountDownLatch globalLatch, int instanceId, Keyspace keyspace, ObjectMapper mapper, RequestDao requestDao) {
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

        final Statuses statuses = progressDao.findPartitionKeysByStatus(ODM_RES, instanceId, from);
        List<String> partitionKeys = statuses.getPartitionKeys();

        final int partitionKeysCount = partitionKeys.size();
        if (partitionKeysCount > 0) {

            from = statuses.getLastTimeStamp();

            periodicRequestLog(partitionKeysCount);

            insertOutputXOM(partitionKeys);
            LOGGER.info("Inserting EXPORT_BI : {}", partitionKeysCount);
            progressDao.insertPartitionKeysForStatus(EXPORT_BI, instanceId, partitionKeys);
        } else {
            Thread.sleep(sleepDelay);
        }
        return from;
    }

    private void periodicRequestLog(int partitionKeysCount) {
        long previousRequestCount = globalRequestCount.get();
        long requestCount = globalRequestCount.addAndGet(partitionKeysCount);
        logForInterval(requestCount, previousRequestCount, "Found ODM_RES so far : {}", requestCount);
    }

    private void insertOutputXOM(List<String> partitionKeys) {
        final Mutator<String> mutator = requestDao.createMutator();
        for (String partitionKey : partitionKeys) {
            requestDao.insertOutputXOM(mutator, partitionKey);
        }
        mutator.execute();
    }

    @Override
    protected void startUp() throws Exception {
        LOGGER.info("Starting RuleEngineService");
        this.fetchSize = propertyLoader.getInt(RULE_ENGINE_STEP_FETCH_SIZE);
        this.sleepDelay = propertyLoader.getInt(RULE_ENGINE_STEP_WAIT_IN_MILLIS);
        this.loggingInterval = propertyLoader.getInt(RULE_ENGINE_STEP_LOGGING_INTERVAL);

        this.progressDao = new ProgressDao(keyspace, fetchSize);
    }

    @Override
    protected void shutDown() throws Exception {
        LOGGER.info("Shutting down RuleEngineService");
        globalLatch.countDown();
    }
}
