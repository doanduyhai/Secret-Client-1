package com.secret.client.business;

import static com.secret.client.cassandra.ProgressStatus.CLIENT_IMPORTED;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.RandomStringUtils.random;
import static org.assertj.core.api.Assertions.assertThat;
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
import com.secret.client.cassandra.ProgressCounterDao;
import com.secret.client.cassandra.ProgressDao;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.mutation.Mutator;

@RunWith(MockitoJUnitRunner.class)
public class ClientContractMatchingServiceTest {

    @Mock
    private ProgressCounterDao progressCounterDao;

    @Mock
    private ProgressDao progressDao;

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
        service = new ClientContractMatchingService(keyspace, mapper, 2);
        service.progressCounterDao = progressCounterDao;
        service.progressDao = progressDao;
        service.clientDao = clientDao;
        service.contractDao = contractDao;
    }

    @Test
    public void should_get_extra_clients_for_bucket() throws Exception {
        //Given
        when(progressCounterDao.readCounter(CLIENT_IMPORTED, 17)).thenReturn(10L);

        //When
        final Long actual = service.getExtraClientsForBucket(17);

        //Then
        assertThat(actual).isEqualTo(10L);
    }

    @Test
    public void should_process_client_and_find_contract() throws Exception {
        //Given
        String partitionKey = random(10);
        when(contractDao.findContractsForClient(partitionKey)).thenReturn(true);

        //When
        service.processClient(asList(partitionKey), 10, 3);

        //Then
        verify(clientDao).readClient(partitionKey);
    }

    @Test
    public void should_process_client_and_add_to_retry_because_contact_not_found() throws Exception {
        //Given
        String partitionKey = random(10);
        when(contractDao.findContractsForClient(partitionKey)).thenReturn(false);
        when(progressDao.createMutator()).thenReturn(mutator);

        //When
        service.processClient(asList(partitionKey), 10, 3);

        //Then
        verify(clientDao).readClient(partitionKey);
        verify(progressDao).insertPartitionKeyForStatus(mutator, CLIENT_IMPORTED, 13, partitionKey);
        verify(progressCounterDao).incrementCounter(CLIENT_IMPORTED, 13);
    }
}
