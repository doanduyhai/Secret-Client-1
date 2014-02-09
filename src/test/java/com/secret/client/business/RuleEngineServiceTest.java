package com.secret.client.business;

import com.secret.client.CassandraStressMain;
import com.secret.client.cassandra.ProgressDao;
import com.secret.client.cassandra.RequestDao;
import com.secret.client.vo.Statuses;
import me.prettyprint.hector.api.Keyspace;
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

import static com.secret.client.cassandra.ProgressStatus.EXPORT_BI;
import static com.secret.client.cassandra.ProgressStatus.ODM_RES;
import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RuleEngineServiceTest {

    private RuleEngineService service;

    @Mock
    private Keyspace keyspace;


    @Mock
    private RequestDao requestDao;

    @Mock
    private ProgressDao progressDao;

    @Mock
    private Statuses statuses;

    @Mock
    private Mutator<String> requestMutator;

    @Before
    public void setUp() {
        service = new RuleEngineService(null,1, keyspace, CassandraStressMain.buildJacksonMapper(), requestDao);
        service.instancesCount=1;
        service.loggingInterval = 1000;
        service.progressDao = progressDao;
        service.globalRequestCount = new AtomicLong(0L);
    }

    @Test
    public void should_read_request_and_export_bi_status_in_same_bucket() throws Exception {
        //Given
        service.maximumRowSize = 10;
        int bucket = 1;
        service.globalRequestCount = new AtomicLong(10L);
        long columnsCount = 0;
        long from = 0;
        long lastTimestamp = RandomUtils.nextLong();

        String partitionKey = RandomStringUtils.randomAlphabetic(5);
        final List<String> partitionKeys = asList(partitionKey);

        when(progressDao.findPartitionKeysByStatus(ODM_RES, bucket, from)).thenReturn(statuses);
        when(statuses.getPartitionKeys()).thenReturn(partitionKeys);
        when(statuses.getLastTimeStamp()).thenReturn(lastTimestamp);
        when(requestDao.createMutator()).thenReturn(requestMutator);

        //When
        final RuleEngineService.Counters counters = service.process(bucket, columnsCount, from);

        //Then

        assertThat(counters.getBucket()).isEqualTo(bucket);
        assertThat(counters.getFrom()).isEqualTo(lastTimestamp);
        assertThat(service.globalRequestCount.get()).isEqualTo(11L);
        assertThat(counters.getColumnsCount()).isEqualTo(columnsCount + 1);

        verify(requestDao).insertOutputXOM(requestMutator,partitionKey);
        verify(requestMutator).execute();
        verify(progressDao).insertPartitionKeysForStatus(EXPORT_BI, bucket, partitionKeys);
    }

    @Test
    public void should_read_request_and_export_bi_status_and_change_bucket() throws Exception {
        //Given
        service.maximumRowSize = 1;
        int bucket = 1;
        service.globalRequestCount = new AtomicLong(10L);
        long columnsCount = 0;
        long from = 0;
        long lastTimestamp = RandomUtils.nextLong();

        String partitionKey1 = RandomStringUtils.randomAlphabetic(5);
        String partitionKey2 = RandomStringUtils.randomAlphabetic(5);
        final List<String> partitionKeys = asList(partitionKey1, partitionKey2);

        when(progressDao.findPartitionKeysByStatus(ODM_RES, bucket, from)).thenReturn(statuses);
        when(statuses.getPartitionKeys()).thenReturn(partitionKeys);
        when(statuses.getLastTimeStamp()).thenReturn(lastTimestamp);
        when(requestDao.createMutator()).thenReturn(requestMutator);

        //When
        final RuleEngineService.Counters counters = service.process(bucket, columnsCount, from);

        //Then

        assertThat(counters.getBucket()).isEqualTo(bucket+1);
        assertThat(counters.getFrom()).isEqualTo(lastTimestamp);
        assertThat(service.globalRequestCount.get()).isEqualTo(12L);
        assertThat(counters.getColumnsCount()).isEqualTo(1);

        verify(requestDao, atLeastOnce()).insertOutputXOM(requestMutator,partitionKey1);
        verify(requestDao, atLeastOnce()).insertOutputXOM(requestMutator,partitionKey2);
        verify(requestMutator).execute();
        verify(progressDao,atLeastOnce()).insertPartitionKeysForStatus(EXPORT_BI, bucket, asList(partitionKey1));
        verify(progressDao,atLeastOnce()).insertPartitionKeysForStatus(EXPORT_BI, bucket+1, asList(partitionKey2));
    }
}
