package com.secret.client.business;

import static com.secret.client.cassandra.ProgressStatus.EXPORT_BI;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
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

@RunWith(MockitoJUnitRunner.class)
public class ExportBIStepTest {

    private ExportBIStep service;

    @Mock
    private Keyspace keyspace;

    @Mock
    private RequestDao requestDao;

    @Mock
    private ProgressDao progressDao;

    @Mock
    private Statuses statuses;

    private int instanceId = 1;

    @Before
    public void setUp() {
        service = new ExportBIStep(null, instanceId, keyspace, CassandraStressMain.buildJacksonMapper(), requestDao);
        service.loggingInterval = 1000;
        service.progressDao = progressDao;
        service.globalRequestCount = new AtomicLong(0L);
    }

    @Test
    public void should_read_request() throws Exception {
        //Given
        service.globalRequestCount = new AtomicLong(10L);

        long from = 0;
        long lastTimestamp = RandomUtils.nextLong();

        String partitionKey = RandomStringUtils.randomAlphabetic(5);
        final List<String> partitionKeys = asList(partitionKey);

        when(progressDao.findPartitionKeysByStatus(EXPORT_BI, instanceId, from)).thenReturn(statuses);
        when(statuses.getPartitionKeys()).thenReturn(partitionKeys);
        when(statuses.getLastTimeStamp()).thenReturn(lastTimestamp);

        //When
        final long actual = service.process(from);

        //Then
        assertThat(service.globalRequestCount.get()).isEqualTo(11L);
        assertThat(actual).isEqualTo(lastTimestamp);

        verify(requestDao).readOutputXom(partitionKey);
    }


    @Test
    public void should_sleep_and_re_poll() throws Exception {
        //Given
        service.globalRequestCount = new AtomicLong(10L);
        service.sleepDelay = 1;

        int bucket = 1;
        long from = 0;
        final List<String> partitionKeys = new ArrayList<String>();

        when(progressDao.findPartitionKeysByStatus(EXPORT_BI, instanceId, from)).thenReturn(statuses);
        when(statuses.getPartitionKeys()).thenReturn(partitionKeys);
        when(statuses.getLastTimeStamp()).thenReturn(from);

        //When
        final long actual = service.process(from);

        //Then
        assertThat(service.globalRequestCount.get()).isEqualTo(10L);
        assertThat(actual).isEqualTo(from);

        verifyZeroInteractions(requestDao);
    }
}
