package com.secret.client.business;

import java.util.concurrent.CountDownLatch;
import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.secret.client.property.PropertyLoader;
import me.prettyprint.hector.api.Keyspace;

public abstract class AbstractInjectorStep extends AbstractExecutionThreadService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractInjectorStep.class);

    protected PropertyLoader propertyLoader = new PropertyLoader();
    protected CountDownLatch globalLatch;
    protected int loggingInterval;

    protected Keyspace keyspace;

    protected ObjectMapper mapper;


    protected AbstractInjectorStep(CountDownLatch globalLatch, Keyspace keyspace, ObjectMapper mapper) {
        this.globalLatch = globalLatch;
        this.keyspace = keyspace;
        this.mapper = mapper;
    }

    protected void logInfo(String message, Object... params) {
        if (ArrayUtils.isNotEmpty(params)) {
            LOGGER.info(message, params);
        } else {
            LOGGER.info(message);
        }
    }

    protected void logForInterval(long currentCount, long previousCount, String message, Object... params) {
        if (currentCount % loggingInterval <= previousCount % loggingInterval) {
            if (params != null && params.length > 0) {
                LOGGER.info(message, params);
            } else {
                LOGGER.info(message);
            }
        }
    }
}
