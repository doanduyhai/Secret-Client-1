package com.secret.client.business;

import static com.secret.client.business.GoOneStep.GoOneStateHolder;
import static com.secret.client.cassandra.ProgressStatus.CLIENT_IMPORTED;
import static java.util.Arrays.asList;
import static org.apache.commons.lang.RandomStringUtils.random;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.secret.client.cassandra.ClientDao;
import com.secret.client.cassandra.ContractDao;
import com.secret.client.cassandra.ProgressDao;
import com.secret.client.cassandra.RequestDao;
import com.secret.client.vo.Statuses;
import me.prettyprint.hector.api.Keyspace;

@RunWith(MockitoJUnitRunner.class)
public class GoOneStepTest {

    private GoOneStep service;

    @Mock
    private Keyspace keyspace;

    @Mock
    private ClientDao clientDao;

    @Mock
    private ContractDao contractDao;

    @Mock
    private ProgressDao progressDao;

    @Mock
    private RequestDao requestDao;

    @Mock
    private Statuses statuses;

    @Mock
    private ClientContractMatchingService clientContractMatchingService;

    @Mock
    private RequestService requestService;

    private int instanceCount = 100;
    private int loggingInteval = 100;
    private int maximumRowSize = 100;

    @Before
    public void setUp() {
        service = new GoOneStep(null, 3, keyspace, null, requestDao);
        service.clientContractMatchingService = clientContractMatchingService;
        service.progressDao = progressDao;
        service.requestService = requestService;
        service.maximumRowSize = maximumRowSize;
        service.instancesCount = instanceCount;
        service.loggingInterval = loggingInteval;
    }

    @Test
    public void should_proceed_client_and_contract_in_the_same_bucket() throws Exception {
        //Given
        int clientBucket = 1;
        long clientColumnCount = 1;
        int requestBucket = 1;
        long requestColumnCount = 1;
        int batchClientCount = 1;
        RequestService.RequestStateHolder requestStateHolder = new RequestService.RequestStateHolder(requestBucket, requestColumnCount, batchClientCount);
        RequestService.RequestStateHolder newRequestStateHolder = new RequestService.RequestStateHolder(requestBucket, requestColumnCount, batchClientCount);
        Long from = 0L;
        Long extraClientCount = null;
        GoOneStateHolder stateHolder = new GoOneStateHolder(clientBucket, clientColumnCount, requestStateHolder, from, extraClientCount);
        String partitionKey = random(10);
        final List<String> partitionKeys = asList(partitionKey);
        Statuses statuses = new Statuses(10L, partitionKeys);

        when(progressDao.findPartitionKeysByStatus(CLIENT_IMPORTED, clientBucket, from)).thenReturn(statuses);
        when(requestService.process(requestStateHolder, partitionKeys.size())).thenReturn(newRequestStateHolder);

        //When
        final GoOneStateHolder actual = service.process(stateHolder);

        //Then
        assertThat(actual.getClientBucket()).isEqualTo(clientBucket);
        assertThat(actual.getClientColumnsCount()).isEqualTo(clientColumnCount + 1);
        assertThat(actual.getFrom()).isEqualTo(10L);
        assertThat(actual.getRequestStateHolder()).isEqualTo(newRequestStateHolder);
        assertThat(actual.getSafeExtraClientCount()).isEqualTo(0L);

        verify(clientContractMatchingService).processClient(partitionKeys, clientBucket, instanceCount);
    }

    @Test
    public void should_proceed_client_and_contract_and_change_bucket() throws Exception {
        //Given
        service.maximumRowSize = 10;

        int clientBucket = 1;
        long clientColumnCount = 9;
        int requestBucket = 1;
        long requestColumnCount = 1;
        int batchClientCount = 1;
        RequestService.RequestStateHolder requestStateHolder = new RequestService.RequestStateHolder(requestBucket, requestColumnCount, batchClientCount);
        RequestService.RequestStateHolder newRequestStateHolder = new RequestService.RequestStateHolder(requestBucket, requestColumnCount, batchClientCount);
        Long from = 0L;
        Long extraClientCount = null;
        Long newExtraClientCount = 1L;
        GoOneStateHolder stateHolder = new GoOneStateHolder(clientBucket, clientColumnCount, requestStateHolder, from, extraClientCount);
        String partitionKey1 = random(10);
        String partitionKey2 = random(10);
        String partitionKey3 = random(10);
        final List<String> partitionKeys = asList(partitionKey1, partitionKey2, partitionKey3);
        Statuses statuses = new Statuses(10L, partitionKeys);

        when(progressDao.findPartitionKeysByStatus(CLIENT_IMPORTED, clientBucket, from)).thenReturn(statuses);
        when(clientContractMatchingService.getExtraClientsForBucket(clientBucket)).thenReturn(newExtraClientCount);
        when(requestService.process(requestStateHolder, partitionKeys.size())).thenReturn(newRequestStateHolder);

        //When
        final GoOneStateHolder actual = service.process(stateHolder);

        //Then
        assertThat(actual.getClientBucket()).isEqualTo(clientBucket + instanceCount);
        assertThat(actual.getClientColumnsCount()).isEqualTo(0);
        assertThat(actual.getFrom()).isEqualTo(10L);
        assertThat(actual.getRequestStateHolder()).isEqualTo(newRequestStateHolder);
        assertThat(actual.getSafeExtraClientCount()).isEqualTo(newExtraClientCount);

        verify(clientContractMatchingService).processClient(partitionKeys, clientBucket + instanceCount, instanceCount);
    }

    @Test
    public void should_sleep_and_poll_if_no_data() throws Exception {
        //Given

        service.sleepDelay = 1;
        int clientBucket = 1;
        long clientColumnCount = 1;
        int requestBucket = 1;
        long requestColumnCount = 1;
        int batchClientCount = 1;
        RequestService.RequestStateHolder requestStateHolder = new RequestService.RequestStateHolder(requestBucket, requestColumnCount, batchClientCount);
        Long from = 0L;
        Long extraClientCount = null;
        GoOneStateHolder stateHolder = new GoOneStateHolder(clientBucket, clientColumnCount, requestStateHolder, from, extraClientCount);
        final List<String> partitionKeys = new ArrayList<String>();
        Statuses statuses = new Statuses(10L, partitionKeys);

        when(progressDao.findPartitionKeysByStatus(CLIENT_IMPORTED, clientBucket, from)).thenReturn(statuses);

        //When
        final GoOneStateHolder actual = service.process(stateHolder);

        //Then
        assertThat(actual.getClientBucket()).isEqualTo(clientBucket);
        assertThat(actual.getClientColumnsCount()).isEqualTo(clientColumnCount);
        assertThat(actual.getFrom()).isEqualTo(from);
        assertThat(actual.getRequestStateHolder()).isEqualTo(requestStateHolder);
        assertThat(actual.getSafeExtraClientCount()).isEqualTo(0L);

        verifyZeroInteractions(requestService, clientContractMatchingService);
    }
}
