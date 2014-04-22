package com.secret.client.business;

import static com.secret.client.cassandra.ProgressStatus.CLIENT_IMPORTED;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.secret.client.cassandra.ClientDao;
import com.secret.client.cassandra.ContractDao;
import com.secret.client.cassandra.ProgressDao;
import com.secret.client.model.Client;
import com.secret.client.model.Contract;
import com.secret.client.model.Menage;
import com.secret.client.random.RandomDataIterator;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.mutation.Mutator;

public class DataLoaderStep extends AbstractInjectorStep {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DataLoaderStep.class);

    private static final String DATALOADER_BATCH_INSERT_SIZE = "dataloader.service.batch.insert.size";
    private static final String DATALOADER_WAIT_IN_MILLIS = "dataloader.service.wait.millis";
    private static final String DATALOADER_LOGGING_INTERVAL = "dataloader.service.logging.interval";
    private RandomDataIterator randomDataIterator;

    protected Integer batchInsertSize;
    protected Integer batchInsertDelay;


    protected ClientDao clientDao;
    protected ContractDao contratDao;
    protected ProgressDao progressDao;

    public DataLoaderStep(CountDownLatch globalLatch, Keyspace keyspace, ObjectMapper mapper,
            RandomDataIterator randomDataIterator) {
        super(globalLatch,keyspace,mapper);
        this.keyspace = keyspace;
        this.randomDataIterator = randomDataIterator;
    }

    @Override
    protected void run() throws Exception {
        long clientCount=0;
        int bucket=1;
        long columnsCount=0;
        while(randomDataIterator.hasNext()) {
            final Mutator<String> clientMutator = clientDao.createMutator();
            final Mutator<String> contratMutator = contratDao.createMutator();
            final Mutator<Composite> progressMutator = progressDao.createMutator();

            for(int i=0; i<batchInsertSize; i++) {
                if(randomDataIterator.hasNext()) {
                    final Menage menage = randomDataIterator.next();
                    final Map<String,List<Contract>> contratsMap = menage.getContratsMap();

                    for(Client client: menage.getClientsMap().values()) {
                        final String partitionKey = clientDao.insertClient(clientMutator, client);
                        columnsCount++;
                        if(columnsCount>maximumRowSize) {
                            logInfo("CLIENT_IMPORTED bucket transition: {} -> {}", bucket,bucket+1);
                            bucket++;

                            // Re-init column count to 1 and not 0 because current insert is taken into account for next bucket
                            columnsCount=1;
                        }

                        progressDao.insertPartitionKeyForStatus(progressMutator, CLIENT_IMPORTED, bucket, partitionKey);
                        final String numeroClient = client.getNumeroClient();
                        insertContratForClient(contratMutator, contratsMap, numeroClient);
                    }
                }
            }

            clientCount = periodicLog(clientCount);
            clientMutator.execute();
            contratMutator.execute();
            progressMutator.execute();
            Thread.sleep(batchInsertDelay);
        }
    }

    private long periodicLog(Long clientCount) {
        Long previousClientCount;
        previousClientCount = clientCount;
        clientCount += batchInsertSize;
        logForInterval(clientCount,previousClientCount,"Client inserted so far : {}",clientCount);
        return clientCount;
    }

    private void insertContratForClient(Mutator<String> contratMutator, Map<String, List<Contract>> contratsMap, String numeroClient) {
        if(contratsMap.containsKey(numeroClient)) {
            final List<Contract> contrats = contratsMap.get(numeroClient);
            for(Contract contrat:contrats) {
                contratDao.insertContratForClient(contratMutator, contrat);
            }
        }
    }

    @Override
    protected void startUp() throws Exception {
        LOGGER.info("Starting DataLoaderService");
        this.clientDao = new ClientDao(keyspace,mapper);
        this.contratDao = new ContractDao(keyspace,mapper);
        this.batchInsertSize = propertyLoader.getInt(DATALOADER_BATCH_INSERT_SIZE);
        this.batchInsertDelay = propertyLoader.getInt(DATALOADER_WAIT_IN_MILLIS);
        this.loggingInterval = propertyLoader.getInt(DATALOADER_LOGGING_INTERVAL);
        this.maximumRowSize = propertyLoader.getInt(MAXIMUM_ROW_SIZE);
        this.progressDao = new ProgressDao(keyspace,batchInsertSize);
    }

    @Override
    protected void shutDown() throws Exception {
        LOGGER.info("Shutting down DataLoaderService");
        globalLatch.countDown();
    }
}
