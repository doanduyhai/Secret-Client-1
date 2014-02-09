package com.secret.client.business;

import com.secret.client.CassandraStressMain;
import com.secret.client.cassandra.ClientDao;
import com.secret.client.cassandra.ContractDao;
import com.secret.client.cassandra.ProgressDao;
import com.secret.client.cassandra.ProgressStatus;
import com.secret.client.cassandra.RequestDao;
import com.secret.client.vo.Statuses;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.mutation.Mutator;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.secret.client.business.GoOneService.Counters;
import static com.secret.client.cassandra.ProgressStatus.ODM_RES;
import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GoOneServiceTest {

    private GoOneService service;

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
    private Mutator<String> requestMutator;

    @Mock
    private Mutator<Composite> progressMutator;

    @Before
    public void setUp() {

        service = new GoOneService(null, 1,keyspace, CassandraStressMain.buildJacksonMapper(), requestDao);
        service.loggingInterval = 1000;
        service.clientDao = clientDao;
        service.contratDao = contractDao;
        service.progressDao = progressDao;
        service.instancesCount = 1;
        service.globalClientCount = new AtomicLong(0L);
        service.globalRequestCount = new AtomicLong(0L);
    }

    @Test
    public void should_read_validated_clients_same_bucket_no_request_generated() throws Exception {
        //Given
        service.maximumRowSize = 10;
        service.batchClientCount = 0;
        service.REQUEST_BATCH_SIZE = 200;

        int clientBucket = 1;
        long clientColumnsCount = 0;

        int requestBucket = 1;
        long requestColumnsCount = 0;

        long from = 0;
        long lastTimestamp = RandomUtils.nextLong();

        String partitionKey1 = RandomStringUtils.randomAlphabetic(5);
        String partitionKey2 = RandomStringUtils.randomAlphabetic(5);
        final List<String> partitionKeys = asList(partitionKey1, partitionKey2);


        when(progressDao.findPartitionKeysByStatus(ProgressStatus.CLIENT_IMPORTED, clientBucket, from)).thenReturn(statuses);
        when(statuses.getPartitionKeys()).thenReturn(partitionKeys);
        when(statuses.getLastTimeStamp()).thenReturn(lastTimestamp);

        //When
        final Counters counters = service.process(clientBucket, clientColumnsCount, requestBucket, requestColumnsCount, from);

        //Then

        verify(clientDao, atLeastOnce()).readClient(partitionKey1);
        verify(clientDao, atLeastOnce()).readClient(partitionKey2);
        assertThat(counters.getFrom()).isEqualTo(lastTimestamp);
        assertThat(counters.getClientBucket()).isEqualTo(clientBucket);
        assertThat(service.globalClientCount.get()).isEqualTo(2);
        assertThat(counters.getClientColumnsCount()).isEqualTo(clientColumnsCount + 2);

        assertThat(counters.getRequestBucket()).isEqualTo(requestBucket);
        assertThat(service.globalRequestCount.get()).isEqualTo(0);
        assertThat(counters.getRequestColumnsCount()).isEqualTo(requestColumnsCount);

        assertThat(service.batchClientCount).isEqualTo(2);

        verify(contractDao, atLeastOnce()).findContractsForClient(partitionKey1);
        verify(contractDao, atLeastOnce()).findContractsForClient(partitionKey2);

        verify(progressDao, never()).insertPartitionKeyForStatus(eq(ODM_RES), any(Integer.class), anyString());
    }

    @Test
    public void should_read_validated_clients_bucket_change_no_request_generated() throws Exception {
        //Given
        service.maximumRowSize = 1;
        service.batchClientCount = 0;
        service.REQUEST_BATCH_SIZE = 200;

        int clientBucket = 1;
        long clientColumnsCount = 0;

        int requestBucket = 1;
        long requestColumnsCount = 0;

        long from = 0;
        long lastTimestamp = RandomUtils.nextLong();

        String partitionKey1 = RandomStringUtils.randomAlphabetic(5);
        String partitionKey2 = RandomStringUtils.randomAlphabetic(5);
        final List<String> partitionKeys = asList(partitionKey1, partitionKey2);


        when(progressDao.findPartitionKeysByStatus(ProgressStatus.CLIENT_IMPORTED, clientBucket, from)).thenReturn(statuses);
        when(statuses.getPartitionKeys()).thenReturn(partitionKeys);
        when(statuses.getLastTimeStamp()).thenReturn(lastTimestamp);


        //When
        final Counters counters = service.process(clientBucket, clientColumnsCount, requestBucket, requestColumnsCount, from);

        //Then
        verify(clientDao, atLeastOnce()).readClient(partitionKey1);
        verify(clientDao, atLeastOnce()).readClient(partitionKey2);

        assertThat(counters.getFrom()).isEqualTo(lastTimestamp);
        assertThat(counters.getClientBucket()).isEqualTo(clientBucket + 1);
        assertThat(service.globalClientCount.get()).isEqualTo(2);

        assertThat(counters.getClientColumnsCount()).isEqualTo(1);

        assertThat(counters.getRequestBucket()).isEqualTo(requestBucket);
        assertThat(service.globalRequestCount.get()).isEqualTo(0);
        assertThat(counters.getRequestColumnsCount()).isEqualTo(requestColumnsCount);

        assertThat(service.batchClientCount).isEqualTo(2);

        verify(contractDao, atLeastOnce()).findContractsForClient(partitionKey1);
        verify(contractDao, atLeastOnce()).findContractsForClient(partitionKey2);
        verify(progressDao, never()).insertPartitionKeyForStatus(eq(ODM_RES), any(Integer.class), anyString());
    }


    @Test
    public void should_read_validated_clients_and_generate_request_with_buckets_change() throws Exception {
        //Given
        service.maximumRowSize = 2;
        service.batchClientCount = 0;
        service.REQUEST_BATCH_SIZE = 1;

        int clientBucket = 1;
        long clientColumnsCount = 0;

        int requestBucket = 1;
        long requestColumnsCount = 0;

        long from = 0;
        long lastTimestamp = RandomUtils.nextLong();

        String partitionKey1 = RandomStringUtils.randomAlphabetic(5);
        String partitionKey2 = RandomStringUtils.randomAlphabetic(5);
        String partitionKey3 = RandomStringUtils.randomAlphabetic(5);
        final List<String> partitionKeys = asList(partitionKey1, partitionKey2, partitionKey3);

        when(progressDao.findPartitionKeysByStatus(ProgressStatus.CLIENT_IMPORTED, clientBucket, from)).thenReturn(statuses);
        when(statuses.getPartitionKeys()).thenReturn(partitionKeys);
        when(statuses.getLastTimeStamp()).thenReturn(lastTimestamp);
        when(requestDao.createMutator()).thenReturn(requestMutator);
        when(progressDao.createMutator()).thenReturn(progressMutator);


        //When
        final Counters counters = service.process(clientBucket, clientColumnsCount, requestBucket, requestColumnsCount, from);

        //Then
        verify(clientDao, atLeastOnce()).readClient(partitionKey1);
        verify(clientDao, atLeastOnce()).readClient(partitionKey2);
        verify(clientDao, atLeastOnce()).readClient(partitionKey3);

        assertThat(counters.getFrom()).isEqualTo(lastTimestamp);
        assertThat(counters.getClientBucket()).isEqualTo(clientBucket + 1);
        assertThat(service.globalClientCount.get()).isEqualTo(3);
        assertThat(counters.getClientColumnsCount()).isEqualTo(1);

        assertThat(counters.getRequestBucket()).isEqualTo(requestBucket + 1);
        assertThat(service.globalRequestCount.get()).isEqualTo(3);
        assertThat(counters.getRequestColumnsCount()).isEqualTo(1);

        assertThat(service.batchClientCount).isEqualTo(0);

        verify(contractDao,atLeastOnce()).findContractsForClient(partitionKey1);
        verify(contractDao,atLeastOnce()).findContractsForClient(partitionKey2);
        verify(contractDao,atLeastOnce()).findContractsForClient(partitionKey3);

        verify(requestDao, times(3)).insertInputXOM(eq(requestMutator),anyString());
        verify(progressDao, times(2)).insertPartitionKeyForStatus(eq(progressMutator),eq(ODM_RES), eq(requestBucket), anyString());

        verify(requestMutator).execute();
        verify(progressMutator).execute();
    }


    @Test
    public void should_read_validated_clients_and_generate_request_with_buckets_change_with_multiple_instances() throws Exception {
        //Given
        service.maximumRowSize = 2;
        service.batchClientCount = 0;
        service.REQUEST_BATCH_SIZE = 1;
        service.instancesCount=10;

        int clientBucket = 1;
        long clientColumnsCount = 0;

        int requestBucket = 1;
        long requestColumnsCount = 0;

        long from = 0;
        long lastTimestamp = RandomUtils.nextLong();

        String partitionKey1 = RandomStringUtils.randomAlphabetic(5);
        String partitionKey2 = RandomStringUtils.randomAlphabetic(5);
        String partitionKey3 = RandomStringUtils.randomAlphabetic(5);
        final List<String> partitionKeys = asList(partitionKey1, partitionKey2, partitionKey3);


        when(progressDao.findPartitionKeysByStatus(ProgressStatus.CLIENT_IMPORTED, clientBucket, from)).thenReturn(statuses);
        when(statuses.getPartitionKeys()).thenReturn(partitionKeys);
        when(statuses.getLastTimeStamp()).thenReturn(lastTimestamp);
        when(requestDao.createMutator()).thenReturn(requestMutator);
        when(progressDao.createMutator()).thenReturn(progressMutator);


        //When
        final Counters counters = service.process(clientBucket, clientColumnsCount, requestBucket, requestColumnsCount, from);

        //Then
        verify(clientDao, atLeastOnce()).readClient(partitionKey1);
        verify(clientDao, atLeastOnce()).readClient(partitionKey2);
        verify(clientDao, atLeastOnce()).readClient(partitionKey3);

        assertThat(counters.getFrom()).isEqualTo(lastTimestamp);
        assertThat(counters.getClientBucket()).isEqualTo(clientBucket + 10);
        assertThat(service.globalClientCount.get()).isEqualTo(3);
        assertThat(counters.getClientColumnsCount()).isEqualTo(1);

        assertThat(counters.getRequestBucket()).isEqualTo(requestBucket + 10);
        assertThat(service.globalRequestCount.get()).isEqualTo(3);
        assertThat(counters.getRequestColumnsCount()).isEqualTo(1);

        assertThat(service.batchClientCount).isEqualTo(0);

        verify(contractDao,atLeastOnce()).findContractsForClient(partitionKey1);
        verify(contractDao,atLeastOnce()).findContractsForClient(partitionKey2);
        verify(contractDao,atLeastOnce()).findContractsForClient(partitionKey3);

        verify(requestDao, times(3)).insertInputXOM(eq(requestMutator),anyString());
        verify(progressDao, times(2)).insertPartitionKeyForStatus(eq(progressMutator),eq(ODM_RES), eq(requestBucket), anyString());

        verify(requestMutator).execute();
        verify(progressMutator).execute();
    }
}
