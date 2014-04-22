package com.secret.client.cassandra;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.secret.client.property.PropertyLoader;
import com.google.common.base.Optional;
import me.prettyprint.cassandra.model.AllOneConsistencyLevelPolicy;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;

public class HectorBootstrapper {

    private static final String LOGIN = "cassandra.login";
    private static final String PASSWORD = "cassandra.password";
    private static final String HOSTNAMES = "cassandra.hostnames";
    private static final String THRIFT_PORT = "cassandra.thrift.port";
    private static final String CLUSTER_NAME = "cassandra.cluster";
    private static final String KEYSPACE_NAME = "cassandra.keyspace";
    private static final String DEFAULT_CLUSTER_NAME = "Horus Cluster";
    private static final String DEFAULT_KEYSPACE_NAME = "precoSimuMassBatch";

    private static final String CLIENT_TABLE = "Clients";
    private static final String CONTRAT_TABLE = "Contracts";
    private static final String PROGRESS_TABLE = "progress";
    private static final String REQUEST_TABLE = "odmResIO";


    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    private Cluster cluster;
    private Keyspace keyspace;

    private PropertyLoader propertyLoader = new PropertyLoader();

    public void init() {
        final String cassandraLogin = propertyLoader.getString(LOGIN);
        final String cassandraPassword = propertyLoader.getString(PASSWORD);
        final String cassandraHosts = propertyLoader.getString(HOSTNAMES);
        String cassandraCluster = propertyLoader.getString(CLUSTER_NAME);
        String cassandraKeyspace = propertyLoader.getString(KEYSPACE_NAME);
        cassandraCluster = Optional.fromNullable(cassandraCluster).or(DEFAULT_CLUSTER_NAME);
        cassandraKeyspace = Optional.fromNullable(cassandraKeyspace).or(DEFAULT_KEYSPACE_NAME);

        final Integer cassandraThriftPort = propertyLoader.getInt(THRIFT_PORT);
        Map<String, String> credentials = null;
        if (StringUtils.isNotBlank(cassandraLogin)) {
            credentials = new HashMap<String,String>();
            credentials.put(USERNAME_KEY, cassandraLogin);
            credentials.put(PASSWORD_KEY, cassandraPassword);
        }

        CassandraHostConfigurator hostConfigurator = new CassandraHostConfigurator(cassandraHosts);
        hostConfigurator.setPort(cassandraThriftPort);
        hostConfigurator.setAutoDiscoverHosts(true);
        cluster = HFactory.getOrCreateCluster(cassandraCluster, hostConfigurator, credentials);
        keyspace = HFactory.createKeyspace(cassandraKeyspace, cluster, new AllOneConsistencyLevelPolicy());


    }

    public Cluster getCluster() {
        return cluster;
    }

    public Keyspace getKeyspace() {
        return keyspace;
    }


    public void truncateTables() {
        cluster.truncate(keyspace.getKeyspaceName(),CLIENT_TABLE);
        cluster.truncate(keyspace.getKeyspaceName(),CONTRAT_TABLE);
        cluster.truncate(keyspace.getKeyspaceName(), PROGRESS_TABLE);
        cluster.truncate(keyspace.getKeyspaceName(),REQUEST_TABLE);
    }
}
