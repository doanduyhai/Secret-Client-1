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
import com.secret.client.CassandraStressMain;
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

    private int instanceId = 1;
    private int loggingInteval = 100;

    @Before
    public void setUp() {
        service = new GoOneStep(null, instanceId, keyspace, CassandraStressMain.buildJacksonMapper(), requestDao);
        service.clientContractMatchingService = clientContractMatchingService;
        service.progressDao = progressDao;
        service.requestService = requestService;
        service.loggingInterval = loggingInteval;
    }

    @Test
    public void should_proceed_client_and_contract() throws Exception {
        //Given
        int batchClientCount = 1;
        int newBatchClientCount = 11;
        Long from = 0L;
        Long newFrom = 10L;

        GoOneStateHolder stateHolder = new GoOneStateHolder(batchClientCount, from);

        String partitionKey = random(10);
        final List<String> partitionKeys = asList(partitionKey);
        Statuses statuses = new Statuses(newFrom, partitionKeys);

        when(progressDao.findPartitionKeysByStatus(CLIENT_IMPORTED, instanceId, from)).thenReturn(statuses);
        when(requestService.process(batchClientCount, partitionKeys.size())).thenReturn(newBatchClientCount);

        //When
        final GoOneStateHolder actual = service.process(stateHolder);

        //Then
        assertThat(actual.getFrom()).isEqualTo(newFrom);
        assertThat(actual.getBatchClientCount()).isEqualTo(newBatchClientCount);

        verify(clientContractMatchingService).processClient(partitionKeys);
    }


    @Test
    public void should_sleep_and_poll_if_no_data() throws Exception {
        //Given

        service.sleepDelay = 1;
        int batchClientCount = 1;
        Long from = 0L;

        GoOneStateHolder stateHolder = new GoOneStateHolder(batchClientCount, from);

        final List<String> partitionKeys = new ArrayList<String>();
        Statuses statuses = new Statuses(from, partitionKeys);

        when(progressDao.findPartitionKeysByStatus(CLIENT_IMPORTED, instanceId, from)).thenReturn(statuses);

        //When
        final GoOneStateHolder actual = service.process(stateHolder);

        //Then
        assertThat(actual.getFrom()).isEqualTo(from);
        assertThat(actual.getBatchClientCount()).isEqualTo(batchClientCount);

        verifyZeroInteractions(requestService, clientContractMatchingService);
    }
}
