package com.secret.client.logging;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LoggingService.class);

    public void logInfoMessage(String message,Object...params) {
        if(ArrayUtils.isNotEmpty(params)) {
            LOGGER.info(message,params);
        } else {
            LOGGER.info(message);
        }
    }

    public void logDebugMessage(String message,Object...params) {
        if(ArrayUtils.isNotEmpty(params)) {
            LOGGER.debug(message,params);
        } else {
            LOGGER.debug(message);
        }
    }
}
