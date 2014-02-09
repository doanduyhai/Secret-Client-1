package com.secret.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

public class Menage {
    private Map<String,Client> clientsMap;
    private Map<String,List<Contract>> contratsMap;

    public Menage(Map<String,Client> clientsMap,Map<String,List<Contract>> contratsMap) {
        this.clientsMap = clientsMap;
        this.contratsMap = contratsMap;
    }

    public void randomize() {
        Map<String,Client> newClientsMap = new HashMap<String, Client>();
        Map<String,List<Contract>> newContratsMap = new HashMap<String, List<Contract>>();

        String randomNumeroClient = RandomStringUtils.randomNumeric(20);
        final Client client = clientsMap.values().iterator().next();
        final String oldNumeroClient = client.getNumeroClient();
        client.setNumeroClient(randomNumeroClient);
        newClientsMap.put(randomNumeroClient, client);

        if(contratsMap.containsKey(oldNumeroClient)) {
            updateNumeroClientForContrats(newContratsMap, randomNumeroClient, oldNumeroClient);
        }

        final String oldNumeroConjoint = client.getConjoint();

        if(StringUtils.isNotBlank(oldNumeroConjoint)) {
            randomizeConjoint(newClientsMap, newContratsMap, randomNumeroClient, client, oldNumeroConjoint);
        }

        this.clientsMap = newClientsMap;
        this.contratsMap = newContratsMap;
    }

    public Map<String, Client> getClientsMap() {
        return clientsMap;
    }

    public Map<String, List<Contract>> getContratsMap() {
        return contratsMap;
    }

    private void randomizeConjoint(Map<String, Client> newClientsMap, Map<String, List<Contract>> newContratsMap,
                                   String randomNumeroClient, Client client, String oldNumeroConjoint) {
        String randomNumeroConjoint = RandomStringUtils.randomNumeric(20);
        client.setConjoint(randomNumeroConjoint);
        final Client conjoint = clientsMap.get(oldNumeroConjoint);
        if(conjoint != null) {
            conjoint.setNumeroClient(randomNumeroConjoint);
            conjoint.setConjoint(randomNumeroClient);
            newClientsMap.put(randomNumeroConjoint,conjoint);

            if(contratsMap.containsKey(oldNumeroConjoint)) {
                updateNumeroClientForContrats(newContratsMap, randomNumeroConjoint, oldNumeroConjoint);
            }
        }
    }

    private void updateNumeroClientForContrats(Map<String, List<Contract>> newContratsMap, String randomNumeroClient,
                                               String oldNumeroClient) {
        for(Contract contrat:contratsMap.get(oldNumeroClient)) {
            contrat.setNumeroClient(randomNumeroClient);
            contrat.setIdentifiant(RandomStringUtils.randomNumeric(20));
            if(!newContratsMap.containsKey(randomNumeroClient)) {
                newContratsMap.put(randomNumeroClient, new ArrayList<Contract>());
            }
            newContratsMap.get(randomNumeroClient).add(contrat);
        }
    }
}
