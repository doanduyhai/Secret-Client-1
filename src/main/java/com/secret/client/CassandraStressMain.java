package com.secret.client;

import static java.util.Arrays.asList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import com.secret.client.business.ContractLoaderStep;
import com.secret.client.cassandra.HectorBootstrapper;
import com.secret.client.csv.ContractDataLoader;
import com.secret.client.property.PropertyLoader;
import me.prettyprint.hector.api.Keyspace;

public class CassandraStressMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraStressMain.class);

    private static final String CONTRACTS_FILES = "contracts.files";

    private CountDownLatch globalLatch;

    public static void main(String[] args) throws InterruptedException, IOException {
        new CassandraStressMain().startProcessing();
    }

    protected void startProcessing() throws InterruptedException, IOException {

        LOGGER.info("************* Starting Cassandra stress batch **************");

        final PropertyLoader propertyLoader = new PropertyLoader();
        propertyLoader.init();
        final HectorBootstrapper hectorBootstrapper = new HectorBootstrapper();
        hectorBootstrapper.init();
        hectorBootstrapper.truncateTables();
        final Keyspace keyspace = hectorBootstrapper.getKeyspace();

        final List<Service> services = buildServiceChain(propertyLoader, keyspace);
        final int threadCount = asList(propertyLoader.getString(CONTRACTS_FILES).split(",")).size();

        globalLatch = new CountDownLatch(5);

        final ExecutorService multiThreadedExecutor = Executors.newFixedThreadPool(threadCount);
        final ServiceManager serviceManager = new ServiceManager(services);

        serviceManager.addListener(new ServiceManager.Listener() {
            public void failure(Service service) {
                LOGGER.error("************* Service {} has failed, exit batch **************", service.toString());
                System.exit(1);
            }
        }, multiThreadedExecutor);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                // Give the services 5 seconds to stop to ensure that we are responsive to shutdown
                // requests.
                try {
                    LOGGER.info("************* Stopping Cassandra stress batch **************");
                    for (Service service : services) {
                        service.stopAsync();
                    }
                    serviceManager.stopAsync().awaitStopped(5, TimeUnit.SECONDS);
                    LOGGER.info("************* Exit **************");
                    multiThreadedExecutor.shutdown();
                } catch (TimeoutException timeout) {
                    LOGGER.error("Timeout for stopping services !!!");
                }
            }
        });
        serviceManager.startAsync();  // start all the services asynchronously
        globalLatch.await();
        serviceManager.stopAsync();
        multiThreadedExecutor.shutdownNow();
    }

    protected List<Service> buildServiceChain(PropertyLoader propertyLoader, Keyspace keyspace) throws IOException {

        List<Service> services = new ArrayList<Service>();
        final ObjectMapper mapper = buildJacksonMapper();

        final List<String> contractFiles = asList(propertyLoader.getString(CONTRACTS_FILES).split(","));
        for (String contractFile : contractFiles) {
            services.add(new ContractLoaderStep(globalLatch, keyspace, mapper, new ContractDataLoader(contractFile)));
        }
        return services;
    }

    public static ObjectMapper buildJacksonMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }
}
