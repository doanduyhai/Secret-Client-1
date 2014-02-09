package com.secret.client.cassandra;

import static me.prettyprint.hector.api.factory.HFactory.createColumn;

import com.secret.client.model.Contract;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.mutation.Mutator;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class ContractDao extends GenericCassandraDao<String,String,byte[]> {

    private static final String COLUMN_FAMILY = "Contracts";
    private ObjectMapper mapper;

    public ContractDao(Keyspace keyspace, ObjectMapper mapper) {
        super(keyspace);
        this.mapper = mapper;
        keySerializer = STRING_SRZ;
        columnNameSerializer = STRING_SRZ;
        valueSerializer = BYTE_SRZ;
        columnFamily = COLUMN_FAMILY;
    }

    public void insertContratForClient(Mutator<String> contratMutator, Contract contrat) {

        final String idMenage = contrat.getIdMen();
        final String numeroClient = contrat.getNumeroClient();
        final String partitionKey = idMenage+":"+numeroClient;
        final String identifiant = contrat.getIdentifiant();

        try {
            final byte[] payload = mapper.writeValueAsBytes(contrat);
            contratMutator.addInsertion(partitionKey, COLUMN_FAMILY, createColumn(identifiant, payload));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void findContractsForClient(String partitionKey) {
        findColumnsRange(partitionKey,null,false,10);
    }

}
