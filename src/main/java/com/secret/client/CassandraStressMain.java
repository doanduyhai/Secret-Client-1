package com.secret.client;

import static java.util.Arrays.asList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import com.secret.client.business.ClientLoaderStep;
import com.secret.client.business.ContractLoaderStep;
import com.secret.client.business.ExportBIStep;
import com.secret.client.business.GoOneStep;
import com.secret.client.business.RuleEngineStep;
import com.secret.client.cassandra.HectorBootstrapper;
import com.secret.client.cassandra.RequestDao;
import com.secret.client.csv.ClientDataLoader;
import com.secret.client.csv.ContractDataLoader;
import com.secret.client.property.PropertyLoader;
import me.prettyprint.hector.api.Keyspace;

public class CassandraStressMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraStressMain.class);

    private static final String PARALLELISM = "parallelism";
    private static final String LOAD_DATA_ONLY = "load.data.only";
    private static final String CONTRACTS_FILES = "contracts.files";
    private static final String CLIENTS_FILES = "clients.files";
    private static final String SAMPLE_XOM_INPUT_PATH = "sample.xom.input.path";
    private static final String SAMPLE_XOM_OUTPUT_PATH = "sample.xom.output.path";

    private ServiceManager.Listener serviceListener = new ServiceManager.Listener() {
        public void failure(Service service) {
            LOGGER.error("************* Service {} has failed, exit batch **************", service.toString());
            System.exit(1);
        }
    };


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

        final int parallelism = propertyLoader.getInt(PARALLELISM);
        final boolean loadDataOnly = Boolean.parseBoolean(propertyLoader.getString(LOAD_DATA_ONLY));

        final RequestDao requestDao = buildRequestDao(propertyLoader, keyspace);
        final ExecutorService loadingTheadPool = Executors.newFixedThreadPool(parallelism);
        ExecutorService processingTheadPool = Executors.newSingleThreadExecutor();
        CountDownLatch contractsCountDownLatch = new CountDownLatch(parallelism);
        CountDownLatch clientsCountDownLatch = new CountDownLatch(parallelism);
        CountDownLatch processingCountDownLatch = new CountDownLatch(parallelism * 3);
        List<Service> processingServices = Collections.emptyList();
        if (!loadDataOnly) {
            processingTheadPool = Executors.newFixedThreadPool(parallelism * 3);
            processingServices = buildProcessingServiceChain(processingCountDownLatch, keyspace, requestDao, parallelism);
        }

        final List<Service> loadContractServices = buildLoadContractsServiceChain(contractsCountDownLatch, propertyLoader, keyspace);
        final List<Service> loadClientServices = buildLoadClientsServiceChain(clientsCountDownLatch, propertyLoader, keyspace);

        final ServiceManager contractServiceManager = new ServiceManager(loadContractServices);
        contractServiceManager.addListener(serviceListener, loadingTheadPool);

        final ServiceManager clientServiceManager = new ServiceManager(loadClientServices);
        clientServiceManager.addListener(serviceListener, loadingTheadPool);
        final ServiceManager processingServiceManager = new ServiceManager(processingServices);
        ;

        processingServiceManager.addListener(serviceListener, processingTheadPool);

        Runtime.getRuntime().addShutdownHook(createShutdownHook(loadContractServices, loadingTheadPool, asList(contractServiceManager, clientServiceManager, processingServiceManager)));

        LOGGER.info("************* Starting Contracts import **************");
        contractServiceManager.startAsync();
        contractsCountDownLatch.await();
        contractServiceManager.stopAsync();

        LOGGER.info("************* Starting Clients import **************");
        clientServiceManager.startAsync();
        clientsCountDownLatch.await();
        clientServiceManager.stopAsync();

        if (!loadDataOnly) {
            LOGGER.info("************* Starting GoOne, RuleEngine & ExportBI in parallel **************");
            processingServiceManager.startAsync();
            processingCountDownLatch.await();
            processingServiceManager.stopAsync();
        }

        loadingTheadPool.shutdownNow();
    }

    private Thread createShutdownHook(final List<Service> services, final ExecutorService multiThreadedExecutor, final List<ServiceManager> serviceManagers) {
        return new Thread() {
            public void run() {
                // Give the services 5 seconds to stop to ensure that we are responsive to shutdown
                // requests.
                try {
                    LOGGER.info("************* Stopping Cassandra stress batch **************");
                    for (Service service : services) {
                        service.stopAsync();
                    }
                    for (ServiceManager serviceManager : serviceManagers) {
                        serviceManager.stopAsync().awaitStopped(5, TimeUnit.SECONDS);
                    }
                    LOGGER.info("************* Exit **************");
                    multiThreadedExecutor.shutdown();
                } catch (TimeoutException timeout) {
                    LOGGER.error("Timeout for stopping services !!!");
                }
            }
        };
    }

    protected List<Service> buildLoadContractsServiceChain(CountDownLatch countDownLatch, PropertyLoader propertyLoader, Keyspace keyspace) throws IOException {

        List<Service> services = new ArrayList<Service>();
        final ObjectMapper mapper = buildJacksonMapper();

        final List<String> contractFiles = asList(propertyLoader.getString(CONTRACTS_FILES).split(","));
        for (String contractFile : contractFiles) {
            services.add(new ContractLoaderStep(countDownLatch, keyspace, mapper, new ContractDataLoader(contractFile)));
        }
        return services;
    }

    protected List<Service> buildLoadClientsServiceChain(CountDownLatch countDownLatch, PropertyLoader propertyLoader, Keyspace keyspace) throws IOException {

        List<Service> services = new ArrayList<Service>();
        final ObjectMapper mapper = buildJacksonMapper();

        final List<String> clientFiles = asList(propertyLoader.getString(CLIENTS_FILES).split(","));
        for (int i = 0; i < clientFiles.size(); i++) {
            services.add(new ClientLoaderStep(countDownLatch, keyspace, mapper, new ClientDataLoader(clientFiles.get(i)), i + 1));
        }
        return services;
    }

    protected List<Service> buildProcessingServiceChain(CountDownLatch countDownLatch, Keyspace keyspace, RequestDao requestDao, int parallelism) throws IOException {

        List<Service> services = new ArrayList<Service>();
        final ObjectMapper mapper = buildJacksonMapper();

        for (int i = 0; i < parallelism; i++) {
            services.add(new GoOneStep(countDownLatch, i + 1, keyspace, mapper, requestDao));
            services.add(new RuleEngineStep(countDownLatch, i + 1, keyspace, mapper, requestDao));
            services.add(new ExportBIStep(countDownLatch, i + 1, keyspace, mapper, requestDao));
        }
        return services;
    }

    public static ObjectMapper buildJacksonMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }

    protected RequestDao buildRequestDao(PropertyLoader propertyLoader, Keyspace keyspace) throws IOException {
        final String sampleXOMInputPath = propertyLoader.getString(SAMPLE_XOM_INPUT_PATH);
        final String sampleXOMOutputPath = propertyLoader.getString(SAMPLE_XOM_OUTPUT_PATH);
        return new RequestDao(keyspace, sampleXOMInputPath, sampleXOMOutputPath);
    }
}
