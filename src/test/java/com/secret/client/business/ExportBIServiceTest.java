package com.secret.client.business;

import static com.secret.client.cassandra.ProgressStatus.EXPORT_BI;
import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.secret.client.CassandraStressMain;
import com.secret.client.cassandra.ProgressDao;
import com.secret.client.cassandra.RequestDao;
import com.secret.client.vo.Statuses;
import me.prettyprint.hector.api.Keyspace;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RunWith(MockitoJUnitRunner.class)
public class ExportBIServiceTest {

    private ExportBIService service;

    @Mock
    private Keyspace keyspace;

    @Mock
    private RequestDao requestDao;

    @Mock
    private ProgressDao progressDao;

    @Mock
    private Statuses statuses;

    @Before
    public void setUp() {
        service = new ExportBIService(null,1,keyspace, CassandraStressMain.buildJacksonMapper(),requestDao);
        service.loggingInterval = 1000;
        service.progressDao = progressDao;
        service.globalRequestCount = new AtomicLong(0L);
    }

    @Test
    public void should_read_request_in_same_bucket() throws Exception {
        //Given
        service.maximumRowSize = 10;
        int bucket = 1;
        service.globalRequestCount = new AtomicLong(10L);
        long columnsCount = 0;
        long from = 0;
        long lastTimestamp = RandomUtils.nextLong();

        String partitionKey = RandomStringUtils.randomAlphabetic(5);
        final List<String> partitionKeys = asList(partitionKey);

        when(progressDao.findPartitionKeysByStatus(EXPORT_BI,bucket,from)).thenReturn(statuses);
        when(statuses.getPartitionKeys()).thenReturn(partitionKeys);
        when(statuses.getLastTimeStamp()).thenReturn(lastTimestamp);

        //When
        final ExportBIService.Counters counters = service.process(bucket, columnsCount, from);

        //Then
        assertThat(counters.getBucket()).isEqualTo(bucket);
        assertThat(service.globalRequestCount.get()).isEqualTo(11L);
        assertThat(counters.getColumnsCount()).isEqualTo(columnsCount+1);
        assertThat(counters.getFrom()).isEqualTo(lastTimestamp);

        verify(requestDao).readOutputXom(partitionKey);
    }

    @Test
    public void should_read_requests_and_change_bucket() throws Exception {
        //Given
        service.maximumRowSize = 2;
        int bucket = 1;
        service.globalRequestCount = new AtomicLong(10L);
        long columnsCount = 0;
        long from = 0;
        long lastTimestamp = RandomUtils.nextLong();

        String partitionKey1 = RandomStringUtils.randomAlphabetic(5);
        String partitionKey2 = RandomStringUtils.randomAlphabetic(5);
        final List<String> partitionKeys = asList(partitionKey1,partitionKey2);

        when(progressDao.findPartitionKeysByStatus(EXPORT_BI,bucket,from)).thenReturn(statuses);
        when(statuses.getPartitionKeys()).thenReturn(partitionKeys);
        when(statuses.getLastTimeStamp()).thenReturn(lastTimestamp);

        //When
        final ExportBIService.Counters counters = service.process(bucket, columnsCount, from);

        //Then
        assertThat(counters.getBucket()).isEqualTo(bucket+1);
        assertThat(service.globalRequestCount.get()).isEqualTo(12L);
        assertThat(counters.getColumnsCount()).isEqualTo(0);
        assertThat(counters.getFrom()).isEqualTo(lastTimestamp);

        verify(requestDao,atLeastOnce()).readOutputXom(partitionKey1);
        verify(requestDao,atLeastOnce()).readOutputXom(partitionKey2);
    }
}
