package com.secret.client.business;

import static com.secret.client.business.RequestService.REQUEST_BATCH_SIZE;
import static com.secret.client.cassandra.ProgressStatus.ODM_RES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.secret.client.cassandra.ProgressDao;
import com.secret.client.cassandra.RequestDao;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.mutation.Mutator;

@RunWith(MockitoJUnitRunner.class)
public class RequestServiceTest {

    @Mock
    private RequestDao requestDao;

    @Mock
    private ProgressDao progressDao;

    @Mock
    private Mutator<String> requestMutator;

    @Mock
    private Mutator<Composite> progressMutator;

    @Captor
    private ArgumentCaptor<String> partitionKeyCaptor;


    private RequestService service;

    private int instanceId = 1;

    @Before
    public void setUp() {
        when(requestDao.createMutator()).thenReturn(requestMutator);
        when(progressDao.createMutator()).thenReturn(progressMutator);
    }

    @Test
    public void should_process() throws Exception {
        //Given
        int loggingInterval = 5;
        int partitionKeyCount = 25;
        int batchClientCount = 201;
        REQUEST_BATCH_SIZE = 200;

        service = new RequestService(requestDao, progressDao, instanceId, loggingInterval);

        //When
        final int newBatchClientCount = service.process(batchClientCount, partitionKeyCount);

        //Then
        assertThat(newBatchClientCount).isEqualTo((batchClientCount + partitionKeyCount) % REQUEST_BATCH_SIZE);

        verify(requestDao).insertInputXOM(eq(requestMutator), partitionKeyCaptor.capture());
        final String partitionKey = partitionKeyCaptor.getValue();
        verify(progressDao).insertPartitionKeyForStatus(progressMutator, ODM_RES, instanceId, partitionKey);
    }


    @Test
    public void should_not_insert_input_XOM_when_not_reaching_batch_size() throws Exception {
        //Given
        int loggingInterval = 5;
        int partitionKeyCount = 25;
        int batchClientCount = 100;
        REQUEST_BATCH_SIZE = 200;

        service = new RequestService(requestDao, progressDao, instanceId, loggingInterval);

        //When
        final int newBatchClientCount = service.process(batchClientCount, partitionKeyCount);

        //Then
        assertThat(newBatchClientCount).isEqualTo(batchClientCount + partitionKeyCount);

        verifyZeroInteractions(requestDao, progressDao);
    }
}
