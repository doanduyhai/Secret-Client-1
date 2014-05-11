package com.secret.client.business;

import static com.secret.client.cassandra.ProgressStatus.EXPORT_BI;
import static com.secret.client.cassandra.ProgressStatus.ODM_RES;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import java.util.Collections;
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

    private int instanceId = 1;

    @Before
    public void setUp() {
        service = new RuleEngineStep(null, instanceId, keyspace, CassandraStressMain.buildJacksonMapper(), requestDao);
        service.loggingInterval = 1000;
        service.progressDao = progressDao;
        service.globalRequestCount = new AtomicLong(0L);
    }

    @Test
    public void should_read_request_and_export_bi() throws Exception {
        //Given

        long from = 0;
        long lastTimestamp = RandomUtils.nextLong();
        service.globalRequestCount = new AtomicLong(10L);

        String partitionKey = RandomStringUtils.randomAlphabetic(5);
        final List<String> partitionKeys = asList(partitionKey);

        when(progressDao.findPartitionKeysByStatus(ODM_RES, instanceId, from)).thenReturn(statuses);
        when(statuses.getPartitionKeys()).thenReturn(partitionKeys);
        when(statuses.getLastTimeStamp()).thenReturn(lastTimestamp);
        when(requestDao.createMutator()).thenReturn(requestMutator);

        //When
        final long actual = service.process(from);

        //Then

        assertThat(service.globalRequestCount.get()).isEqualTo(11L);
        assertThat(actual).isEqualTo(lastTimestamp);

        verify(requestDao).insertOutputXOM(requestMutator, partitionKey);
        verify(requestMutator).execute();
        verify(progressDao).insertPartitionKeysForStatus(EXPORT_BI, instanceId, partitionKeys);
    }


    @Test
    public void should_read_and_sleep() throws Exception {
        //Given

        long from = 0;
        service.globalRequestCount = new AtomicLong(10L);
        service.sleepDelay = 1;

        final List<String> partitionKeys = Collections.emptyList();

        when(progressDao.findPartitionKeysByStatus(ODM_RES, instanceId, from)).thenReturn(statuses);
        when(statuses.getPartitionKeys()).thenReturn(partitionKeys);
        when(statuses.getLastTimeStamp()).thenReturn(from);
        when(requestDao.createMutator()).thenReturn(requestMutator);

        //When
        final long actual = service.process(from);

        //Then

        assertThat(service.globalRequestCount.get()).isEqualTo(10L);
        assertThat(actual).isEqualTo(from);

        verifyZeroInteractions(requestMutator);
        verify(progressDao, never()).insertPartitionKeysForStatus(EXPORT_BI, instanceId, partitionKeys);
    }
}
