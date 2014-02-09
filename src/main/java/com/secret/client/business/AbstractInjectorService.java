package com.secret.client.business;

import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.secret.client.init.PropertyLoader;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import me.prettyprint.hector.api.Keyspace;

public abstract class AbstractInjectorService extends AbstractExecutionThreadService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractInjectorService.class);

    protected final String MAXIMUM_ROW_SIZE = "maximum.row.size";

    protected PropertyLoader propertyLoader= new PropertyLoader();
    protected CountDownLatch globalLatch;
    protected long loggingInterval;

    protected long maximumRowSize;

    protected Keyspace keyspace;

    protected ObjectMapper mapper;


    protected AbstractInjectorService(CountDownLatch globalLatch,Keyspace keyspace,ObjectMapper mapper) {
        this.globalLatch = globalLatch;
        this.keyspace = keyspace;
        this.mapper = mapper;
    }

    protected void logInfo(String message,Object...params) {
        if(ArrayUtils.isNotEmpty(params)) {
            LOGGER.info(message,params);
        } else {
            LOGGER.info(message);
        }
    }

    protected void logForInterval(long currentCount, long previousCount, String message,Object...params) {
        if(currentCount%loggingInterval <= previousCount%loggingInterval) {
            if(params != null && params.length>0) {
                LOGGER.info(message,params);
            } else {
                LOGGER.info(message);
            }
        }
    }
    protected void logForInterval(long currentCount, String message,Object...params) {
        if(currentCount%loggingInterval == 0) {
            if(params != null && params.length>0) {
                LOGGER.info(message,params);
            } else {
                LOGGER.info(message);
            }
        }
    }
}
