package com.secret.client;

import static com.secret.client.business.ExportBIService.EXPORT_BI_SERVICE_INSTANCES;
import static com.secret.client.business.GoOneService.GO_ONE_SERVICE_INSTANCES;
import static com.secret.client.business.RuleEngineService.RULE_ENGINE_SERVICE_INSTANCES;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import com.secret.client.business.DataLoaderService;
import com.secret.client.business.ExportBIService;
import com.secret.client.business.GoOneService;
import com.secret.client.business.RuleEngineService;
import com.secret.client.cassandra.HectorBootstrapper;
import com.secret.client.cassandra.RequestDao;
import com.secret.client.init.CsvDataLoader;
import com.secret.client.init.PropertyLoader;
import com.secret.client.model.Client;
import com.secret.client.model.Contract;
import com.secret.client.random.RandomDataIterator;
import me.prettyprint.hector.api.Keyspace;

public class CassandraStressMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraStressMain.class);

    private static final String TARGET_CLIENT_NUMBER = "target.client.number";
    private static final String SAMPLE_CLIENT_FILE_PATH = "sample.client.file.path";
    private static final String SAMPLE_CONTRAT_FILE_PATH = "sample.contrat.file.path";
    private static final String SAMPLE_XOM_INPUT_PATH = "sample.xom.input.path";
    private static final String SAMPLE_XOM_OUTPUT_PATH = "sample.xom.output.path";

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

        final RandomDataIterator randomDataIterator = buildRandomDataIterator(propertyLoader);
        final RequestDao requestDao = buildRequestDao(propertyLoader, keyspace);
        final List<Service> services = buildServiceChain(propertyLoader, keyspace, randomDataIterator, requestDao);

        final int goOneInstancesCount = propertyLoader.getInt(GO_ONE_SERVICE_INSTANCES);
        final int ruleEngineInstancesCount = propertyLoader.getInt(RULE_ENGINE_SERVICE_INSTANCES);
        final int exportBIInstancesCount = propertyLoader.getInt(EXPORT_BI_SERVICE_INSTANCES);
        int threadCount = goOneInstancesCount + ruleEngineInstancesCount + exportBIInstancesCount + 1;
        globalLatch = new CountDownLatch(threadCount);

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
        hectorBootstrapper.truncateTables();
        serviceManager.stopAsync();
        multiThreadedExecutor.shutdownNow();
    }

    protected List<Service> buildServiceChain(PropertyLoader propertyLoader, Keyspace keyspace, RandomDataIterator randomDataIterator, RequestDao
            requestDao) {

        List<Service> services = new ArrayList<Service>();
        final ObjectMapper mapper = buildJacksonMapper();
        services.add(new DataLoaderService(globalLatch, keyspace, mapper, randomDataIterator));
        final int goOneInstancesCount = propertyLoader.getInt(GO_ONE_SERVICE_INSTANCES);
        final int ruleEngineInstancesCount = propertyLoader.getInt(RULE_ENGINE_SERVICE_INSTANCES);
        final int exportBIInstancesCount = propertyLoader.getInt(EXPORT_BI_SERVICE_INSTANCES);

        for (int i = 1; i <= goOneInstancesCount; i++) {
            services.add(new GoOneService(globalLatch, i, keyspace, mapper, requestDao));
        }

        for (int i = 1; i <= ruleEngineInstancesCount; i++) {
            services.add(new RuleEngineService(globalLatch, i, keyspace, mapper, requestDao));
        }

        for (int i = 1; i <= exportBIInstancesCount; i++) {
            services.add(new ExportBIService(globalLatch, i, keyspace, mapper, requestDao));
        }
        return services;
    }


    protected RandomDataIterator buildRandomDataIterator(PropertyLoader propertyLoader) throws IOException {

        final Integer targetClientNumber = propertyLoader.getInt(TARGET_CLIENT_NUMBER);
        final String sampleClientFilePath = propertyLoader.getString(SAMPLE_CLIENT_FILE_PATH);
        final String sampleContratFilePath = propertyLoader.getString(SAMPLE_CONTRAT_FILE_PATH);

        CsvDataLoader csvDataLoader = new CsvDataLoader();
        final Map<String, Client> clientsMap = csvDataLoader.loadClients(sampleClientFilePath);
        final Map<String, List<Contract>> contratsMap = csvDataLoader.loadContrats(sampleContratFilePath);

        return new RandomDataIterator(targetClientNumber, clientsMap, contratsMap);
    }

    protected RequestDao buildRequestDao(PropertyLoader propertyLoader, Keyspace keyspace) throws IOException {
        final String sampleXOMInputPath = propertyLoader.getString(SAMPLE_XOM_INPUT_PATH);
        final String sampleXOMOutputPath = propertyLoader.getString(SAMPLE_XOM_OUTPUT_PATH);
        return new RequestDao(keyspace, sampleXOMInputPath, sampleXOMOutputPath);
    }

    public static ObjectMapper buildJacksonMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper;
    }
}
