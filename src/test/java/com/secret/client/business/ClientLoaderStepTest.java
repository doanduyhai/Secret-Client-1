package com.secret.client.business;

import static com.secret.client.cassandra.ProgressStatus.CLIENT_IMPORTED;
import static org.apache.commons.lang.RandomStringUtils.random;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.secret.client.cassandra.ClientDao;
import com.secret.client.cassandra.ProgressDao;
import com.secret.client.csv.ClientDataLoader;
import com.secret.client.model.Client;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.mutation.Mutator;

@RunWith(MockitoJUnitRunner.class)
public class ClientLoaderStepTest {

    private ClientLoaderStep step;

    @Mock
    private Keyspace keyspace;

    @Mock
    private ClientDao clientDao;

    @Mock
    private ProgressDao progressDao;

    @Mock
    private ClientDataLoader loader;

    @Mock
    private Mutator<String> clientMutator;

    @Mock
    private Mutator<Composite> progressMutator;

    @Before
    public void setUp() {
        step = new ClientLoaderStep(null, keyspace, null, loader);
        step.delayInSecond = 1;
        step.loggingInterval = 1;
        step.batchInsertDelay = 1;
        step.maximumRowSize = 100;
        step.clientDao = clientDao;
        step.progressDao = progressDao;
        when(clientDao.createMutator()).thenReturn(clientMutator);
        when(progressDao.createMutator()).thenReturn(progressMutator);
    }

    @Test
    public void should_insert_clients_in_the_same_bucket() throws Exception {
        //Given
        step.batchInsertSize = 1;
        String partitionKey = random(10);
        Client client = new Client();

        when(loader.hasNext()).thenReturn(true, false);
        when(loader.next()).thenReturn(client);
        when(clientDao.insertClient(clientMutator, client)).thenReturn(partitionKey);

        //When
        step.run();

        //Then
        verify(progressDao).insertPartitionKeyForStatus(progressMutator, CLIENT_IMPORTED, 1, partitionKey);
    }

    @Test
    public void should_insert_clients_and_change_bucket() throws Exception {
        //Given
        step.batchInsertSize = 1;
        step.maximumRowSize = 0;
        String partitionKey = random(10);
        Client client = new Client();

        when(loader.hasNext()).thenReturn(true, false);
        when(loader.next()).thenReturn(client);
        when(clientDao.insertClient(clientMutator, client)).thenReturn(partitionKey);

        //When
        step.run();

        //Then
        verify(progressDao).insertPartitionKeyForStatus(progressMutator, CLIENT_IMPORTED, 2, partitionKey);
    }

    @Test
    public void should_insert_clients_and_flush() throws Exception {
        //Given
        step.batchInsertSize = 0;
        String partitionKey = random(10);
        Client client = new Client();

        when(loader.hasNext()).thenReturn(true, false);
        when(loader.next()).thenReturn(client);
        when(clientDao.insertClient(clientMutator, client)).thenReturn(partitionKey);

        //When
        step.run();

        //Then
        verify(progressDao).insertPartitionKeyForStatus(progressMutator, CLIENT_IMPORTED, 1, partitionKey);
        verify(clientMutator).execute();
        verify(progressMutator).execute();
    }
}
