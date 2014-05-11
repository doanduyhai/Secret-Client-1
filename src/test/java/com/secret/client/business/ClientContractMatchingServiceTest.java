package com.secret.client.business;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.RandomStringUtils.random;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.secret.client.cassandra.ClientDao;
import com.secret.client.cassandra.ContractDao;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.mutation.Mutator;

@RunWith(MockitoJUnitRunner.class)
public class ClientContractMatchingServiceTest {

    @Mock
    private ClientDao clientDao;

    @Mock
    private ContractDao contractDao;

    @Mock
    private Keyspace keyspace;

    @Mock
    private Mutator<Composite> mutator;


    private ObjectMapper mapper = new ObjectMapper();

    private ClientContractMatchingService service;

    @Before
    public void setUp() {
        service = new ClientContractMatchingService(keyspace, mapper);
        service.clientDao = clientDao;
        service.contractDao = contractDao;
    }

    @Test
    public void should_process_client_and_find_contract() throws Exception {
        //Given
        String partitionKey = random(10);
        byte[] clientPayload = "client".getBytes();
        when(clientDao.readClient(partitionKey)).thenReturn(clientPayload);
        when(contractDao.findContractsForClient(partitionKey)).thenReturn(true);

        //When
        service.processClient(asList(partitionKey));

        //Then
        verify(clientDao).readClient(partitionKey);
        verify(contractDao).findContractsForClient(partitionKey);
    }

    @Test
    public void should_log_error_when_client_or_contract_not_found() throws Exception {
        //Given
        String partitionKey = random(10);

        //When
        service.processClient(asList(partitionKey));
        //Then

    }


}
