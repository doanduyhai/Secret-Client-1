package com.secret.client.random;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import com.secret.client.model.Client;
import com.secret.client.model.Contract;
import com.secret.client.model.Menage;

public class RandomDataIterator implements Iterator<Menage>{

    private List<Menage> menages;

    private Iterator<Menage> iterator;

    private int count=1;
    private int targetClientNumber;


    public RandomDataIterator(int targetClientNumber,Map<String,Client> clientsMap,Map<String,List<Contract>> contratsMap) {
        this.targetClientNumber = targetClientNumber;
        this.menages = generateMenageList(clientsMap, contratsMap);
        this.iterator = this.menages.iterator();
    }


    private List<Menage> generateMenageList(Map<String,Client> clientsMap,Map<String,List<Contract>> contratsMap) {
        List<Menage> menages = new ArrayList<Menage>();

        Map<String,Client> mutableClientsMap = new HashMap<String, Client>(clientsMap);
        Map<String,List<Contract>> mutableContratsMap = new HashMap<String, List<Contract>>(contratsMap);


        for(String numeroClient:clientsMap.keySet()) {
            Map<String,Client> menageClientsMap = new HashMap<String, Client>();
            Map<String,List<Contract>> menageContratsMap = new HashMap<String, List<Contract>>();

            if(mutableClientsMap.containsKey(numeroClient)) {
                final Client client = processClient(mutableClientsMap, mutableContratsMap, numeroClient,
                    menageClientsMap,menageContratsMap);

                final String numeroConjoint = client.getConjoint();
                if(StringUtils.isNotBlank(numeroConjoint) && mutableClientsMap.containsKey(numeroConjoint)) {
                    processClient(mutableClientsMap, mutableContratsMap, numeroConjoint, menageClientsMap,
                                                          menageContratsMap);
                }
                menages.add(new Menage(menageClientsMap,menageContratsMap));
            }
        }
        return menages;
    }

    private Client processClient(Map<String, Client> clientsMap, Map<String, List<Contract>> contratsMap,
                                 String numeroClient, Map<String, Client> menageClientsMap, Map<String, List<Contract>> menageContratsMap) {
        final Client client = clientsMap.get(numeroClient);
        menageClientsMap.put(numeroClient, client);
        clientsMap.remove(numeroClient);

        if(contratsMap.containsKey(numeroClient)) {
            menageContratsMap.put(numeroClient,contratsMap.get(numeroClient));
            contratsMap.remove(numeroClient);
        }
        return client;
    }

    public List<Menage> getMenages() {
        return menages;
    }

    @Override
    public boolean hasNext() {
        boolean hasNext;
        if(count > targetClientNumber) {
            hasNext = false;
        } else {
            if(!iterator.hasNext()) {
                iterator = menages.iterator();
            }
            hasNext = iterator.hasNext();
        }
        return hasNext;
    }

    @Override
    public Menage next() {
        if(!hasNext() || count>targetClientNumber) {
            return null;
        } else {
            final Menage menage = iterator.next();
            menage.randomize();
            count++;
            return menage;
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Operation not supported");
    }
}
