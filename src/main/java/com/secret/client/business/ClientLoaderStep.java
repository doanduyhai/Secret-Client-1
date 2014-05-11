package com.secret.client.business;

import static com.secret.client.cassandra.ProgressStatus.CLIENT_IMPORTED;
import java.util.concurrent.CountDownLatch;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.secret.client.cassandra.ClientDao;
import com.secret.client.cassandra.ProgressDao;
import com.secret.client.csv.ClientDataLoader;
import com.secret.client.model.Client;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.mutation.Mutator;

public class ClientLoaderStep extends AbstractInjectorStep {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ClientLoaderStep.class);

    private static final String CLIENT_LOADER_BATCH_INSERT_SIZE = "client.loader.step.batch.insert.size";
    private static final String CLIENT_LOADER_WAIT_IN_MILLIS = "client.loader.step.wait.millis";
    private static final String CLIENT_LOADER_LOGGING_INTERVAL = "client.loader.step.logging.interval";
    private final ClientDataLoader clientLoader;

    protected Integer batchInsertSize;
    protected Integer batchInsertDelay;


    protected ClientDao clientDao;
    protected ProgressDao progressDao;
    protected int instanceId;

    public ClientLoaderStep(CountDownLatch globalLatch, Keyspace keyspace, ObjectMapper mapper,
            ClientDataLoader clientLoader, int instanceId) {
        super(globalLatch, keyspace, mapper);
        this.instanceId = instanceId;
        this.keyspace = keyspace;
        this.clientLoader = clientLoader;
    }

    @Override
    protected void run() throws Exception {
        long clientCount = 0;
        int i = 0;
        Mutator<String> clientMutator = clientDao.createMutator();
        Mutator<Composite> progressMutator = progressDao.createMutator();
        while (clientLoader.hasNext()) {
            if (i == batchInsertSize) {
                LOGGER.info("Flush client data");
                clientMutator.execute();
                progressMutator.execute();
                clientMutator = clientDao.createMutator();
                progressMutator = progressDao.createMutator();
                Thread.sleep(batchInsertDelay);
                clientCount = periodicLog(clientCount);
                i = 0;
            }

            final Client client = clientLoader.next();
            final String partitionKey = clientDao.insertClient(clientMutator, client);
            progressDao.insertPartitionKeyForStatus(progressMutator, CLIENT_IMPORTED, instanceId, partitionKey);
            i++;
        }
    }

    private long periodicLog(Long clientCount) {
        Long previousClientCount;
        previousClientCount = clientCount;
        clientCount += batchInsertSize;
        logForInterval(clientCount, previousClientCount, "Clients inserted so far : {}", clientCount);
        return clientCount;
    }


    @Override
    protected void startUp() throws Exception {
        LOGGER.info("Starting ClientLoaderService");
        this.clientDao = new ClientDao(keyspace, mapper);
        this.batchInsertSize = propertyLoader.getInt(CLIENT_LOADER_BATCH_INSERT_SIZE);
        this.batchInsertDelay = propertyLoader.getInt(CLIENT_LOADER_WAIT_IN_MILLIS);
        this.loggingInterval = propertyLoader.getInt(CLIENT_LOADER_LOGGING_INTERVAL);
        this.progressDao = new ProgressDao(keyspace, batchInsertSize);
    }

    @Override
    protected void shutDown() throws Exception {
        LOGGER.info("Shutting down ClientLoaderService");
        globalLatch.countDown();
    }
}
