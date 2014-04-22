package com.secret.client.business;

import static com.secret.client.cassandra.ProgressStatus.CLIENT_IMPORTED;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.secret.client.cassandra.ClientDao;
import com.secret.client.cassandra.ContractDao;
import com.secret.client.cassandra.ProgressCounterDao;
import com.secret.client.cassandra.ProgressDao;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.mutation.Mutator;

public class ClientContractMatchingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoOneStep.class);
    protected ProgressCounterDao progressCounterDao;
    protected ProgressDao progressDao;
    protected ClientDao clientDao;
    protected ContractDao contractDao;

    public ClientContractMatchingService(Keyspace keyspace, ObjectMapper mapper, int instanceId) {
        this.progressCounterDao = new ProgressCounterDao(keyspace, instanceId);
        this.progressDao = new ProgressDao(keyspace, 100);
        this.clientDao = new ClientDao(keyspace, mapper);
        this.contractDao = new ContractDao(keyspace, mapper);
    }

    public Long getExtraClientsForBucket(int bucket) {
        return progressCounterDao.readCounter(CLIENT_IMPORTED, bucket);
    }


    public void processClient(List<String> partitionKeys, int clientBucket, int nextBucketIncrement) {
        final Mutator<Composite> progressMutator = progressDao.createMutator();
        boolean retry = false;
        for (String partitionKey : partitionKeys) {
            final byte[] serializedClients = clientDao.readClient(partitionKey);
            if (serializedClients == null || serializedClients.length == 0) {
                LOGGER.error("Cannot find client with id {} ", partitionKey);
            }
            final boolean foundContract = contractDao.findContractsForClient(partitionKey);
            if (!foundContract) {
                retry = true;
                LOGGER.warn("Contract with id {} not found, put back client to bucket {}", partitionKey, clientBucket + nextBucketIncrement);
                progressDao.insertPartitionKeyForStatus(progressMutator, CLIENT_IMPORTED, clientBucket + nextBucketIncrement, partitionKey);
                progressCounterDao.incrementCounter(CLIENT_IMPORTED, clientBucket + nextBucketIncrement);
            }
        }
        if (retry)
            progressMutator.execute();
    }
}
