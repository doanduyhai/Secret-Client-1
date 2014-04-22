package com.secret.client.cassandra;

import static org.apache.commons.lang.RandomStringUtils.randomNumeric;
import java.util.HashMap;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import com.secret.client.model.Client;
import me.prettyprint.cassandra.model.AllOneConsistencyLevelPolicy;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

@RunWith(MockitoJUnitRunner.class)
public class ClientDaoTest {
    @Test
    public void should_insert_datas() throws Exception {
        //Given
        CassandraHostConfigurator hostConfigurator = new CassandraHostConfigurator("192.168.1.32");
        hostConfigurator.setPort(9160);
        hostConfigurator.setAutoDiscoverHosts(true);
        final Cluster cluster = HFactory.getOrCreateCluster("Test Cluster", hostConfigurator, new HashMap<String, String>());
        final Keyspace keyspace = HFactory.createKeyspace("test", cluster, new AllOneConsistencyLevelPolicy());

        ClientDao dao = new ClientDao(keyspace, new ObjectMapper());

        final Mutator<String> mutator = dao.createMutator();
        //When
        for (int i = 0; i < 500; i++) {
            String idMen = randomNumeric(8);
            String numeroClient = randomNumeric(10);
            Client client = new Client();
            client.setIdMen(idMen);
            client.setNumeroClient(numeroClient);

            dao.insertClient(mutator, client);
        }

        mutator.execute();
        //Then

    }
}
