package com.secret.client.cassandra;

import static me.prettyprint.hector.api.factory.HFactory.createColumn;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import com.secret.client.model.Client;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.mutation.Mutator;

public class ClientDao extends GenericCassandraDao<String, String, byte[]> {

    private static final String COLUMN_FAMILY = "Clients";
    private static final String PAYLOAD = "payload";

    private ObjectMapper mapper;

    public ClientDao(Keyspace keyspace, ObjectMapper mapper) {
        super(keyspace);
        this.mapper = mapper;
        keySerializer = STRING_SRZ;
        columnNameSerializer = STRING_SRZ;
        valueSerializer = BYTE_SRZ;
        columnFamily = COLUMN_FAMILY;
    }

    public String insertClient(Mutator<String> clientMutator, Client client) {

        final String idMenage = client.getIdMen();
        final String numeroClient = client.getNumeroClient();
        final String partitionKey = idMenage + ":" + numeroClient;

        try {
            final byte[] payload = mapper.writeValueAsBytes(client);
            clientMutator.addInsertion(partitionKey, COLUMN_FAMILY, createColumn(PAYLOAD, payload));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return partitionKey;
    }

    public byte[] readClient(String partitionKey) {
        return getValue(partitionKey, PAYLOAD);
    }


}
