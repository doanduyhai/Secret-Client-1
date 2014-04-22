package com.secret.client;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.secret.client.csv.ClientDataWriter;
import com.secret.client.csv.ContractDataWriter;
import com.secret.client.csv.CsvDataLoaderOld;
import com.secret.client.model.Client;
import com.secret.client.model.Contract;
import com.secret.client.model.Menage;
import com.secret.client.random.RandomDataIterator;

public class SamplesGenerator {

    public static void main(String[] args) throws InterruptedException, IOException {
        checkArgument(args.length == 6, "Usage: SampleGenerator <input_client> <input_contract> <output_client> <output_contract> <size> <max_search_interval>");
        final String inputClient = args[0];
        final String inputContract = args[1];
        final String outputClient = args[2];
        final String outputContract = args[3];
        final String size = args[4];
        final String searchInterval = args[5];

        checkArgument(isNotBlank(inputClient), "<input_client> should not be blank");
        checkArgument(isNotBlank(inputContract), "<input_contract> should not be blank");
        checkArgument(isNotBlank(outputClient), "<output_client> should not be blank");
        checkArgument(isNotBlank(outputContract), "<output_contract> should not be blank");
        checkArgument(isNotBlank(size), "<size> should not be blank");
        checkArgument(isNotBlank(searchInterval), "<max_search_interval> should not be blank");

        final int targetSize = Integer.parseInt(size);
        final int maxSearchInterval = Integer.parseInt(searchInterval);

        final SamplesGenerator generator = new SamplesGenerator();
        generator.generate(inputClient, inputContract, outputClient, outputContract, targetSize, maxSearchInterval);
    }

    private void generate(String inputClient, String inputContract, String outputClient, String outputContract, int targetSize, int maxSearchInterval) throws IOException {
        CsvDataLoaderOld csvDataLoader = new CsvDataLoaderOld();
        final Map<String, Client> clientsMap = csvDataLoader.loadClients(inputClient);
        final Map<String, List<Contract>> contratsMap = csvDataLoader.loadContrats(inputContract);

        ClientDataWriter clientWriter = new ClientDataWriter(outputClient);
        ContractDataWriter contractWriter = new ContractDataWriter(outputContract);

        try {
            final RandomDataIterator iterator = new RandomDataIterator(targetSize, clientsMap, contratsMap);

            while (iterator.hasNext()) {
                List<Client> clients = new ArrayList<Client>();
                List<Contract> contracts = new ArrayList<Contract>();

                for (int i = 0; i < maxSearchInterval; i++) {
                    final Menage menage = iterator.next();
                    final Client client = menage.getClientsMap().values().iterator().next();
                    if (menage.getContratsMap().containsKey(client.getNumeroClient())) {
                        clients.add(client);
                        contracts.addAll(menage.getContratsMap().get(client.getNumeroClient()));
                    }
                }
                Collections.shuffle(contracts);

                clientWriter.writeClients(clients);
                contractWriter.writeContracts(contracts);
            }
        } finally {
            clientWriter.flush();
            contractWriter.flush();
        }


    }
}
