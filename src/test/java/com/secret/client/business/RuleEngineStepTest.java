package com.secret.client.business;

import static com.secret.client.business.RuleEngineStep.RuleEngineStateHolder;
import static com.secret.client.cassandra.ProgressStatus.EXPORT_BI;
import static com.secret.client.cassandra.ProgressStatus.ODM_RES;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.secret.client.CassandraStressMain;
import com.secret.client.cassandra.ProgressDao;
import com.secret.client.cassandra.RequestDao;
import com.secret.client.vo.Statuses;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.mutation.Mutator;

@RunWith(MockitoJUnitRunner.class)
public class RuleEngineStepTest {

    private RuleEngineStep service;

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
        service = new RuleEngineStep(null, 1, keyspace, CassandraStressMain.buildJacksonMapper(), requestDao);
        service.instancesCount = 1;
        service.loggingInterval = 1000;
        service.progressDao = progressDao;
        service.globalRequestCount = new AtomicLong(0L);
    }

    @Test
    public void should_read_request_and_export_bi_status_in_same_bucket() throws Exception {
        //Given
        service.maximumRowSize = 10;
        service.globalRequestCount = new AtomicLong(10L);
        int bucket = 1;
        long columnsCount = 0;
        long from = 0;
        RuleEngineStateHolder stateHolder = new RuleEngineStateHolder(bucket, columnsCount, from);
        long lastTimestamp = RandomUtils.nextLong();

        String partitionKey = RandomStringUtils.randomAlphabetic(5);
        final List<String> partitionKeys = asList(partitionKey);

        when(progressDao.findPartitionKeysByStatus(ODM_RES, bucket, from)).thenReturn(statuses);
        when(statuses.getPartitionKeys()).thenReturn(partitionKeys);
        when(statuses.getLastTimeStamp()).thenReturn(lastTimestamp);
        when(requestDao.createMutator()).thenReturn(requestMutator);

        //When
        final RuleEngineStateHolder actual = service.process(stateHolder);

        //Then

        assertThat(actual.getBucket()).isEqualTo(bucket);
        assertThat(actual.getFrom()).isEqualTo(lastTimestamp);
        assertThat(actual.getColumnsCount()).isEqualTo(columnsCount + 1);
        assertThat(service.globalRequestCount.get()).isEqualTo(11L);

        verify(requestDao).insertOutputXOM(requestMutator, partitionKey);
        verify(requestMutator).execute();
        verify(progressDao).insertPartitionKeysForStatus(EXPORT_BI, bucket, partitionKeys);
    }

    @Test
    public void should_read_request_and_export_bi_status_and_change_bucket() throws Exception {
        //Given
        service.maximumRowSize = 1;
        service.globalRequestCount = new AtomicLong(10L);
        int bucket = 1;
        long columnsCount = 0;
        long from = 0;
        RuleEngineStateHolder stateHolder = new RuleEngineStateHolder(bucket, columnsCount, from);
        long lastTimestamp = RandomUtils.nextLong();

        String partitionKey1 = RandomStringUtils.randomAlphabetic(5);
        String partitionKey2 = RandomStringUtils.randomAlphabetic(5);
        final List<String> partitionKeys = asList(partitionKey1, partitionKey2);

        when(progressDao.findPartitionKeysByStatus(ODM_RES, bucket, from)).thenReturn(statuses);
        when(statuses.getPartitionKeys()).thenReturn(partitionKeys);
        when(statuses.getLastTimeStamp()).thenReturn(lastTimestamp);
        when(requestDao.createMutator()).thenReturn(requestMutator);

        //When
        final RuleEngineStateHolder actual = service.process(stateHolder);

        //Then

        assertThat(actual.getBucket()).isEqualTo(bucket + 1);
        assertThat(actual.getFrom()).isEqualTo(lastTimestamp);
        assertThat(service.globalRequestCount.get()).isEqualTo(12L);
        assertThat(actual.getColumnsCount()).isEqualTo(1);

        verify(requestDao, atLeastOnce()).insertOutputXOM(requestMutator, partitionKey1);
        verify(requestDao, atLeastOnce()).insertOutputXOM(requestMutator, partitionKey2);
        verify(requestMutator).execute();
        verify(progressDao, atLeastOnce()).insertPartitionKeysForStatus(EXPORT_BI, bucket, asList(partitionKey1));
        verify(progressDao, atLeastOnce()).insertPartitionKeysForStatus(EXPORT_BI, bucket + 1, asList(partitionKey2));
    }


    @Test
    public void should_sleep_and_re_poll() throws Exception {
        //Given
        service.maximumRowSize = 10;
        service.globalRequestCount = new AtomicLong(10L);
        service.sleepDelay = 1;

        int bucket = 1;
        long columnsCount = 0;
        long from = 0;
        RuleEngineStateHolder stateHolder = new RuleEngineStateHolder(bucket, columnsCount, from);
        long lastTimestamp = RandomUtils.nextLong();

        final List<String> partitionKeys = new ArrayList<String>();

        when(progressDao.findPartitionKeysByStatus(ODM_RES, bucket, from)).thenReturn(statuses);
        when(statuses.getPartitionKeys()).thenReturn(partitionKeys);
        when(statuses.getLastTimeStamp()).thenReturn(lastTimestamp);

        //When
        final RuleEngineStateHolder actual = service.process(stateHolder);

        //Then

        assertThat(actual.getBucket()).isEqualTo(bucket);
        assertThat(actual.getFrom()).isEqualTo(from);
        assertThat(actual.getColumnsCount()).isEqualTo(columnsCount);
        assertThat(service.globalRequestCount.get()).isEqualTo(10L);

        verifyZeroInteractions(requestDao, requestMutator);
    }
}
