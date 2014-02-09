package com.secret.client.model;

import static org.fest.assertions.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import com.google.common.collect.ImmutableMap;

@RunWith(MockitoJUnitRunner.class)
public class MenageTest {

    @Test
    public void should_randomize_menage() throws Exception {
        //Given
        Map<String,Client> clients = ImmutableMap.of("client",new Client("client", "conjoint"), "conjoint",new Client("conjoint", "client"));
        Map<String,List<Contract>> contrats = ImmutableMap.of("client",Arrays.asList(new Contract("client","contrat1"),new Contract("client","contrat2")),"conjoint",Arrays.asList(new Contract("conjoint","contrat3")));

        Menage menage = new Menage(clients,contrats);

        //When
        menage.randomize();
        final Map<String,Client> clientsMap = menage.getClientsMap();
        final Map<String, List<Contract>> contratsMap = menage.getContratsMap();

        //Then
        assertThat(clientsMap).hasSize(2);
        final List<String> numeroClients = new ArrayList<String>(clientsMap.keySet());
        for(String numeroClient:numeroClients) {
            final Client client = clientsMap.get(numeroClient);
            final Client conjoint = clientsMap.get(client.getConjoint());

            final String randomNumeroClient = client.getNumeroClient();
            assertThat(randomNumeroClient).isEqualTo(numeroClient);
            assertThat(randomNumeroClient).isNotEqualTo("client");
            assertThat(randomNumeroClient).isNotEqualTo("conjoint");

            final String randomNumeroConjointFromClient = client.getConjoint();
            assertThat(randomNumeroConjointFromClient).isNotEqualTo("client").isNotEqualTo("conjoint");

            final List<Contract> contratsClient = contratsMap.get(numeroClient);
            assertThat(contratsClient.size()).isGreaterThan(0);
            for(Contract contrat:contratsClient) {
                assertThat(contrat.getIdentifiant()).isNotEqualTo("contrat1").isNotEqualTo("contrat2").isNotEqualTo("contrat3");
                assertThat(contrat.getNumeroClient()).isEqualTo(numeroClient);
            }

            assertThat(conjoint).isNotNull();
            final String randomNumeroConjoint = conjoint.getNumeroClient();
            assertThat(randomNumeroConjoint).isNotEqualTo("client");
            assertThat(randomNumeroConjoint).isNotEqualTo("conjoint");
            assertThat(randomNumeroConjoint).isEqualTo(randomNumeroConjointFromClient);

            assertThat(clientsMap.containsKey(conjoint.getNumeroClient()));
            assertThat(conjoint.getConjoint()).isEqualTo(numeroClient);

            final List<Contract> contratsConjoint = contratsMap.get(randomNumeroConjoint);
            assertThat(contratsConjoint.size()).isGreaterThan(0);
            for(Contract contrat:contratsConjoint) {
                assertThat(contrat.getIdentifiant()).isNotEqualTo("contrat1").isNotEqualTo("contrat2").isNotEqualTo("contrat3");
                assertThat(contrat.getNumeroClient()).isEqualTo(randomNumeroConjoint);
            }
        }
    }

    @Test
    public void should_randomize_for_one_client() throws Exception {
        //Given
        Map<String,Client> clients = ImmutableMap.of("client",new Client("client",null));
        Map<String,List<Contract>> contrats =ImmutableMap.of("client",Arrays.asList(new Contract("client","contrat1"),new Contract("client","contrat2")));

        Menage menage = new Menage(clients,contrats);

        //When
        menage.randomize();
        final Map<String,Client> clientsMap = menage.getClientsMap();
        final Map<String, List<Contract>> contratsMap = menage.getContratsMap();

        //Then
        assertThat(clientsMap).hasSize(1);


        final String numeroClient = clientsMap.keySet().iterator().next();
        final Client client = clientsMap.get(numeroClient);
        final Client conjoint = clientsMap.get(client.getConjoint());

        final String randomNumeroClient = client.getNumeroClient();
        assertThat(randomNumeroClient).isEqualTo(numeroClient);
        assertThat(randomNumeroClient).isNotEqualTo("client");
        assertThat(client.getConjoint()).isNull();

        final List<Contract> contratsClient = contratsMap.get(numeroClient);
        assertThat(contratsClient).hasSize(2);
        for(Contract contrat:contratsClient) {
            assertThat(contrat.getIdentifiant()).isNotEqualTo("contrat1").isNotEqualTo("contrat2").isNotEqualTo("contrat3");
            assertThat(contrat.getNumeroClient()).isEqualTo(numeroClient);
        }
    }
}
