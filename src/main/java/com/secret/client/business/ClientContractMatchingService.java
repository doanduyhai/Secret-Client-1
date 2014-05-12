package com.secret.client.business;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.secret.client.cassandra.ClientDao;
import com.secret.client.cassandra.ContractDao;
import me.prettyprint.hector.api.Keyspace;

public class ClientContractMatchingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GoOneStep.class);
    protected ClientDao clientDao;
    protected ContractDao contractDao;
    protected static AtomicLong clientWithoutContract = new AtomicLong(0L);

    public ClientContractMatchingService(Keyspace keyspace, ObjectMapper mapper) {
        this.clientDao = new ClientDao(keyspace, mapper);
        this.contractDao = new ContractDao(keyspace, mapper);
    }


    public void processClient(List<String> partitionKeys) {
        for (String partitionKey : partitionKeys) {
            final byte[] serializedClients = clientDao.readClient(partitionKey);
            if (serializedClients == null || serializedClients.length == 0) {
                LOGGER.error("Cannot find client with id {} ", partitionKey);
            }
            final boolean contractsForClient = contractDao.findContractsForClient(partitionKey);
            if (!contractsForClient) {
                LOGGER.debug("Cannot find contract for client with id {} ", partitionKey);
                clientWithoutContract.addAndGet(1L);
            }
        }
    }

    public void showClientWithoutContract() {
        LOGGER.info("There were {} clients without contract", clientWithoutContract.get());
    }
}
