package com.secret.client.business;

import static com.secret.client.business.RequestService.RequestStateHolder;
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

    @Before
    public void setUp() {
        when(requestDao.createMutator()).thenReturn(requestMutator);
        when(progressDao.createMutator()).thenReturn(progressMutator);
    }

    @Test
    public void should_process_in_the_same_bucket() throws Exception {
        //Given
        int maximumRowSize = 100;
        int instancesCount = 3;
        int loggingInterval = 5;
        int requestBucket = 1;
        long requestColumnCount = 0;
        int batchClientCount = 201;

        RequestStateHolder stateHolder = new RequestStateHolder(requestBucket, requestColumnCount, batchClientCount);
        service = new RequestService(requestDao, progressDao, maximumRowSize, instancesCount, loggingInterval);

        //When
        final RequestStateHolder actual = service.process(stateHolder, 25);

        //Then
        assertThat(actual.getRequestBucket()).isEqualTo(requestBucket);
        assertThat(actual.getRequestColumnsCount()).isEqualTo(1);
        assertThat(actual.getBatchClientCount()).isEqualTo(26);

        verify(requestDao).insertInputXOM(eq(requestMutator), partitionKeyCaptor.capture());
        final String partitionKey = partitionKeyCaptor.getValue();
        verify(progressDao).insertPartitionKeyForStatus(progressMutator, ODM_RES, requestBucket, partitionKey);
    }

    @Test
    public void should_transition_to_new_bucket() throws Exception {
        //Given
        int maximumRowSize = 1;
        int instancesCount = 3;
        int loggingInterval = 5;
        int requestBucket = 1;
        long requestColumnCount = 1;
        int batchClientCount = 201;

        RequestStateHolder stateHolder = new RequestStateHolder(requestBucket, requestColumnCount, batchClientCount);
        service = new RequestService(requestDao, progressDao, maximumRowSize, instancesCount, loggingInterval);

        //When
        final RequestStateHolder actual = service.process(stateHolder, 25);

        //Then
        assertThat(actual.getRequestBucket()).isEqualTo(requestBucket + instancesCount);
        assertThat(actual.getRequestColumnsCount()).isEqualTo(1);
        assertThat(actual.getBatchClientCount()).isEqualTo(26);

        verify(requestDao).insertInputXOM(eq(requestMutator), partitionKeyCaptor.capture());
        final String partitionKey = partitionKeyCaptor.getValue();
        verify(progressDao).insertPartitionKeyForStatus(progressMutator, ODM_RES, requestBucket + instancesCount, partitionKey);
    }

    @Test
    public void should_not_insert_input_XOM_when_not_reaching_batch_size() throws Exception {
        //Given
        int maximumRowSize = 1;
        int instancesCount = 3;
        int loggingInterval = 5;
        int requestBucket = 1;
        long requestColumnCount = 1;
        int batchClientCount = 100;

        RequestStateHolder stateHolder = new RequestStateHolder(requestBucket, requestColumnCount, batchClientCount);
        service = new RequestService(requestDao, progressDao, maximumRowSize, instancesCount, loggingInterval);

        //When
        final RequestStateHolder actual = service.process(stateHolder, 25);

        //Then
        assertThat(actual.getRequestBucket()).isEqualTo(requestBucket);
        assertThat(actual.getRequestColumnsCount()).isEqualTo(requestColumnCount);
        assertThat(actual.getBatchClientCount()).isEqualTo(batchClientCount + 25);

        verifyZeroInteractions(requestDao, progressDao);
    }
}
