package com.secret.client.business;

import java.util.concurrent.CountDownLatch;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.secret.client.cassandra.ContractDao;
import com.secret.client.csv.ContractDataLoader;
import com.secret.client.model.Contract;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.mutation.Mutator;

public class ContractLoaderStep extends AbstractInjectorStep {
    protected static final Logger LOGGER = LoggerFactory.getLogger(ContractLoaderStep.class);

    private static final String CONTRACT_LOADER_BATCH_INSERT_SIZE = "contract.loader.step.batch.insert.size";
    private static final String CONTRACT_LOADER_WAIT_IN_MILLIS = "contract.loader.step.wait.millis";
    private static final String CONTRACT_LOADER_LOGGING_INTERVAL = "contract.loader.step.logging.interval";
    private final ContractDataLoader contractLoader;

    protected Integer batchInsertSize;
    protected Integer batchInsertDelay;


    protected ContractDao contractDao;

    public ContractLoaderStep(CountDownLatch globalLatch, Keyspace keyspace, ObjectMapper mapper,
            ContractDataLoader contractLoader) {
        super(globalLatch, keyspace, mapper);
        this.keyspace = keyspace;
        this.contractLoader = contractLoader;
    }

    @Override
    protected void run() throws Exception {
        long contractCount = 0;
        int i = 0;
        Mutator<String> contractMutator = contractDao.createMutator();
        while (contractLoader.hasNext()) {

            if (i == batchInsertSize) {
                contractMutator.execute();
                contractMutator = contractDao.createMutator();
                Thread.sleep(batchInsertDelay);
                contractCount = periodicLog(contractCount);
                i = 0;
            }

            final Contract contract = contractLoader.next();
            contractDao.insertContratForClient(contractMutator, contract);
            i++;
        }
    }

    private long periodicLog(Long contractCount) {
        Long previousContractCount;
        previousContractCount = contractCount;
        contractCount += batchInsertSize;
        logForInterval(contractCount, previousContractCount, "Contracts inserted so far : {}", contractCount);
        return contractCount;
    }


    @Override
    protected void startUp() throws Exception {
        LOGGER.info("Starting ContractLoaderService");
        this.contractDao = new ContractDao(keyspace, mapper);
        this.batchInsertSize = propertyLoader.getInt(CONTRACT_LOADER_BATCH_INSERT_SIZE);
        this.batchInsertDelay = propertyLoader.getInt(CONTRACT_LOADER_WAIT_IN_MILLIS);
        this.loggingInterval = propertyLoader.getInt(CONTRACT_LOADER_LOGGING_INTERVAL);
        this.maximumRowSize = propertyLoader.getInt(MAXIMUM_ROW_SIZE);
    }

    @Override
    protected void shutDown() throws Exception {
        LOGGER.info("Shutting down ContractLoaderService");
        globalLatch.countDown();
    }
}
