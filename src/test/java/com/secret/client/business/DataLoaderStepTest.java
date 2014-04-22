package com.secret.client.business;

import com.secret.client.CassandraStressMain;
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
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.secret.client.cassandra.ProgressStatus.CLIENT_IMPORTED;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DataLoaderStepTest {

    private DataLoaderStep service;

    @Mock
    private Keyspace keyspace;

    @Mock
    private RandomDataIterator dataIterator;

    @Mock
    private ClientDao clientDao;

    @Mock
    private ContractDao contractDao;

    @Mock
    private ProgressDao progressDao;

    @Mock
    private Mutator<String> clientMutator;

    @Mock
    private Mutator<String> contratMutator;

    @Mock
    private Mutator<Composite> progressMutator;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private Menage menage;

    @Mock
    private Map<String, List<Contract>> contratsMap;

    @Before
    public void setUp() {
        service = new DataLoaderStep(null, keyspace, CassandraStressMain.buildJacksonMapper(), dataIterator);
        service.batchInsertDelay = 1;
        service.clientDao = clientDao;
        service.contratDao = contractDao;
        service.progressDao = progressDao;

        when(clientDao.createMutator()).thenReturn(clientMutator);
        when(contractDao.createMutator()).thenReturn(contratMutator);
        when(progressDao.createMutator()).thenReturn(progressMutator);
    }

    @Test
    public void should_load_client_and_contrat() throws Exception {

        //Given
        String partitionKey = RandomStringUtils.randomAlphabetic(5);
        String numeroClient = RandomStringUtils.randomAlphabetic(5);
        String idMen = RandomStringUtils.randomAlphabetic(5);
        String identifiantContrat = RandomStringUtils.randomAlphabetic(5);
        Client client = new Client();
        client.setNumeroClient(numeroClient);
        client.setIdMen(idMen);

        Contract contrat = new Contract();
        contrat.setIdentifiant(identifiantContrat);

        service.batchInsertSize = 1;
        service.loggingInterval = 1;
        service.maximumRowSize = 10000;

        when(dataIterator.hasNext()).thenReturn(true, true, false);
        when(dataIterator.next()).thenReturn(menage);

        when(menage.getContratsMap()).thenReturn(contratsMap);
        when(menage.getClientsMap().values()).thenReturn(Arrays.asList(client));

        when(clientDao.insertClient(clientMutator, client)).thenReturn(partitionKey);

        when(contratsMap.containsKey(numeroClient)).thenReturn(true);
        when(contratsMap.get(numeroClient)).thenReturn(Arrays.asList(contrat));

        //When
        service.run();


        //Then
        verify(progressDao).insertPartitionKeyForStatus(progressMutator, CLIENT_IMPORTED, 1, partitionKey);
        verify(contractDao).insertContratForClient(contratMutator, contrat);
        verify(clientMutator).execute();
        verify(contratMutator).execute();
        verify(progressMutator).execute();
    }

    @Test
    public void should_load_client_with_no_contrat() throws Exception {

        //Given
        String partitionKey = RandomStringUtils.randomAlphabetic(5);
        String numeroClient = RandomStringUtils.randomAlphabetic(5);

        Client client = new Client();
        client.setNumeroClient(numeroClient);


        service.batchInsertSize = 1;
        service.loggingInterval = 1;
        service.maximumRowSize = 10000;

        when(dataIterator.hasNext()).thenReturn(true, true, false);
        when(dataIterator.next()).thenReturn(menage);

        when(menage.getContratsMap()).thenReturn(contratsMap);
        when(menage.getClientsMap().values()).thenReturn(Arrays.asList(client));

        when(clientDao.insertClient(clientMutator, client)).thenReturn(partitionKey);

        when(contratsMap.containsKey(numeroClient)).thenReturn(false);

        //When
        service.run();


        //Then
        verify(progressDao).insertPartitionKeyForStatus(progressMutator, CLIENT_IMPORTED, 1, partitionKey);
        verify(contractDao, never()).insertContratForClient(eq(contratMutator), any(Contract.class));
        verify(clientMutator).execute();
        verify(contratMutator).execute();
        verify(progressMutator).execute();
    }


    @Test
    public void should_load_client_and_transition_to_next_bucket() throws Exception {

        //Given
        String partitionKey1 = RandomStringUtils.randomAlphabetic(5);
        String partitionKey2 = RandomStringUtils.randomAlphabetic(5);
        String numeroClient = RandomStringUtils.randomAlphabetic(5);

        Client client1 = new Client();
        Client client2 = new Client();
        client1.setNumeroClient(numeroClient);
        client2.setNumeroClient(numeroClient);


        service.batchInsertSize = 10;
        service.loggingInterval = 2;
        service.maximumRowSize = 1;

        when(dataIterator.hasNext()).thenReturn(true, true, true, false, false);
        when(dataIterator.next()).thenReturn(menage);

        when(menage.getContratsMap()).thenReturn(contratsMap);
        when(menage.getClientsMap().values()).thenReturn(Arrays.asList(client1, client2));

        when(clientDao.insertClient(clientMutator, client1)).thenReturn(partitionKey1);
        when(clientDao.insertClient(clientMutator, client2)).thenReturn(partitionKey2);

        when(contratsMap.containsKey(numeroClient)).thenReturn(false);

        //When
        service.run();


        //Then
        verify(progressDao, atLeastOnce()).insertPartitionKeyForStatus(progressMutator, CLIENT_IMPORTED, 1, partitionKey1);
        verify(progressDao, atLeastOnce()).insertPartitionKeyForStatus(progressMutator, CLIENT_IMPORTED, 2, partitionKey2);
        verify(clientMutator).execute();
        verify(contratMutator).execute();
        verify(progressMutator).execute();
    }
}
