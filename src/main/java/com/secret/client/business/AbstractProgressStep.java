package com.secret.client.business;

import java.util.concurrent.CountDownLatch;
import com.secret.client.cassandra.ClientDao;
import com.secret.client.cassandra.ContractDao;
import com.secret.client.cassandra.ProgressDao;
import me.prettyprint.hector.api.Keyspace;
import org.codehaus.jackson.map.ObjectMapper;

public abstract class AbstractProgressStep extends AbstractInjectorStep {

    protected ClientDao clientDao;

    protected ContractDao contractDao;

    protected ProgressDao progressDao;

    protected Integer fetchSize;

    protected Integer sleepDelay;

    protected boolean shutdownCalled = false;

    protected int instancesCount = 1;
    protected int instanceId = 1;

    protected AbstractProgressStep(CountDownLatch globalLatch, int instanceId, Keyspace keyspace, ObjectMapper mapper) {
        super(globalLatch,keyspace,mapper);
        this.instanceId = instanceId;
    }

    @Override
    protected String serviceName() {
        return super.serviceName()+":"+instanceId;
    }

    @Override
    protected void triggerShutdown() {
        this.shutdownCalled = true;
        super.triggerShutdown();
    }
}
