package com.secret.client;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import java.io.IOException;
import java.util.ArrayList;
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
        checkArgument(args.length == 6, "Usage: SampleGenerator <input_client> <input_contract> <output_client> <output_contract> <number_of_clients_to_generate> <split_number>");
        final String inputClient = args[0];
        final String inputContract = args[1];
        final String outputClient = args[2];
        final String outputContract = args[3];
        final String clientsNb = args[4];
        final String splitNumber = args[5];

        checkArgument(isNotBlank(inputClient), "<input_client> should not be blank");
        checkArgument(isNotBlank(inputContract), "<input_contract> should not be blank");
        checkArgument(isNotBlank(outputClient), "<output_client> should not be blank");
        checkArgument(isNotBlank(outputContract), "<output_contract> should not be blank");
        checkArgument(isNotBlank(clientsNb), "<number_of_clients_to_generate> should not be blank");
        checkArgument(isNotBlank(splitNumber), "<split_number> should not be blank");

        final int targetClientsNb = Integer.parseInt(clientsNb);
        final int splitsCount = Integer.parseInt(splitNumber);

        final SamplesGenerator generator = new SamplesGenerator();
        generator.generate(inputClient, inputContract, outputClient, outputContract, targetClientsNb, splitsCount);
    }

    private void generate(String inputClient, String inputContract, String outputClient, String outputContract, int targetClientsNb, int splitsCount) throws IOException {
        CsvDataLoaderOld csvDataLoader = new CsvDataLoaderOld();
        final Map<String, Client> clientsMap = csvDataLoader.loadClients(inputClient);
        final Map<String, List<Contract>> contratsMap = csvDataLoader.loadContrats(inputContract);

        List<ClientDataWriter> clientWriters = new ArrayList<ClientDataWriter>();
        List<ContractDataWriter> contractWriters = new ArrayList<ContractDataWriter>();
        for (int i = 1; i <= splitsCount; i++) {
            clientWriters.add(new ClientDataWriter(outputClient.replaceAll("(.+)\\.([^.]+)", "$1" + i + ".$2")));
            contractWriters.add(new ContractDataWriter(outputContract.replaceAll("(.+)\\.([^.]+)", "$1" + i + ".$2")));
        }

        long count = 1;
        try {
            final RandomDataIterator iterator = new RandomDataIterator(targetClientsNb, clientsMap, contratsMap);

            while (iterator.hasNext()) {

                Long currentFileIndex = count % splitsCount;
                final ClientDataWriter currentClientWriter = clientWriters.get(currentFileIndex.intValue());
                final ContractDataWriter currentContractWriter = contractWriters.get(currentFileIndex.intValue());

                final Menage menage = iterator.next();
                final Client client = menage.getClientsMap().values().iterator().next();
                currentClientWriter.writeClients(client);
                if (menage.getContratsMap().containsKey(client.getNumeroClient())) {
                    currentContractWriter.writeContracts(menage.getContratsMap().get(client.getNumeroClient()));
                }
                count++;

            }
        } finally {
            for (int i = 0; i < splitsCount; i++) {
                clientWriters.get(i).flush();
                contractWriters.get(i).flush();
            }
        }


    }
}
