package com.secret.client.random;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import com.secret.client.model.Client;
import com.secret.client.model.Contract;
import com.secret.client.model.Menage;

@RunWith(MockitoJUnitRunner.class)
public class RandomDataIteratorTest {

    private RandomDataIterator iterator;

    @Test
    public void should_build_menage_list() throws Exception {
        //Given
        final Client client1 = new Client("client1", "conjoint1");
        final Client conjoint1 = new Client("conjoint1", "client1");
        final Client client2 = new Client("client2", null);
        final Contract contrat1 = new Contract("client1", "contrat1");
        final Contract contrat2 = new Contract("client1", "contrat2");
        final Contract contrat3 = new Contract("conjoint1", "contrat3");
        final Contract contrat4 = new Contract("client2", "contrat4");

        Map<String, Client> clientsMap = of("client1", client1, "conjoint1", conjoint1, "client2", client2);
        Map<String, List<Contract>> contratsMap = of("client1", asList(contrat1, contrat2), "conjoint1",
                asList(contrat3), "client2", asList(contrat4));

        final RandomDataIterator iterator = new RandomDataIterator(1000, clientsMap, contratsMap);

        //When
        final List<Menage> menages = iterator.getMenages();

        //Then
        assertThat(menages).hasSize(2);
        final Menage menage1 = menages.get(0);
        final Menage menage2 = menages.get(1);

        assertThat(menage1.getClientsMap()).containsKey("client1").containsKey("conjoint1").containsValue(client1).containsValue(conjoint1);

        final Map<String, List<Contract>> builtContratsMap1 = menage1.getContratsMap();
        assertThat(builtContratsMap1).containsKey("client1").containsKey("conjoint1");
        assertThat(builtContratsMap1).containsValue(asList(contrat1, contrat2)).containsValue(asList(contrat3));

        assertThat(menage2.getClientsMap()).containsKey("client2").containsValue(client2);

        final Map<String, List<Contract>> builtContratsMap2 = menage2.getContratsMap();
        assertThat(builtContratsMap2).containsKey("client2");
        assertThat(builtContratsMap2).containsValue(asList(contrat4));
    }

    @Test
    public void should_iterate_on_menage_until_reaching_target_client_number() throws Exception {
        //Given
        final Client client1 = new Client("client1", null, "menage1");
        final Client client2 = new Client("client2", null, "menage2");
        final Client client3 = new Client("client3", null, "menage3");
        final Contract contrat1 = new Contract("client1", "contrat1", "menage1");
        final Contract contrat2 = new Contract("client2", "contrat2", "menage2");
        final Contract contrat3 = new Contract("client3", "contrat31", "menage31");
        final Contract contrat4 = new Contract("client3", "contrat32", "menage32");

        Map<String, Client> clientsMap = of("client1", client1, "client2", client2, "client3", client3);
        Map<String, List<Contract>> contratsMap = of("client1", asList(contrat1), "client2", asList(contrat2), "client3", asList(contrat3, contrat4));

        //When
        final RandomDataIterator iterator = new RandomDataIterator(5, clientsMap, contratsMap);

        //Then
        assertThat(iterator.hasNext()).isTrue();
        final Menage menage1 = iterator.next();

        assertThat(iterator.hasNext()).isTrue();
        final Menage menage2 = iterator.next();

        assertThat(iterator.hasNext()).isTrue();
        final Menage menage3 = iterator.next();

        assertThat(iterator.hasNext()).isTrue();
        final Menage menage4 = iterator.next();

        assertThat(iterator.hasNext()).isTrue();
        final Menage menage5 = iterator.next();

        assertThat(iterator.hasNext()).isFalse();
        assertThat(iterator.next()).isNull();

        assertThat(menage1.getClientsMap()).hasSize(1);
        assertThat(menage1.getClientsMap().values().iterator().next().getIdMen()).isEqualTo("menage1");
        assertThat(menage1.getContratsMap()).hasSize(1);
        final List<Contract> contratsList1 = menage1.getContratsMap().values().iterator().next();
        assertThat(contratsList1).hasSize(1);
        assertThat(contratsList1.get(0).getIdMen()).isEqualTo("menage1");

        assertThat(menage2.getClientsMap()).hasSize(1);
        assertThat(menage2.getClientsMap().values().iterator().next().getIdMen()).isEqualTo("menage2");
        assertThat(menage2.getContratsMap()).hasSize(1);
        final List<Contract> contratsList2 = menage2.getContratsMap().values().iterator().next();
        assertThat(contratsList2).hasSize(1);
        assertThat(contratsList2.get(0).getIdMen()).isEqualTo("menage2");

        assertThat(menage3.getClientsMap()).hasSize(1);
        assertThat(menage3.getClientsMap().values().iterator().next().getIdMen()).isEqualTo("menage3");
        assertThat(menage3.getContratsMap()).hasSize(1);
        final List<Contract> contratsList3 = menage3.getContratsMap().values().iterator().next();
        assertThat(contratsList3).hasSize(2);
        assertThat(contratsList3.get(0).getIdMen()).isEqualTo("menage31");
        assertThat(contratsList3.get(1).getIdMen()).isEqualTo("menage32");

        assertThat(menage4.getClientsMap()).hasSize(1);
        assertThat(menage4.getClientsMap().values().iterator().next().getIdMen()).isEqualTo("menage1");
        assertThat(menage4.getContratsMap()).hasSize(1);
        final List<Contract> contratsList4 = menage4.getContratsMap().values().iterator().next();
        assertThat(contratsList4).hasSize(1);
        assertThat(contratsList4.get(0).getIdMen()).isEqualTo("menage1");

        assertThat(menage5.getClientsMap()).hasSize(1);
        assertThat(menage5.getClientsMap().values().iterator().next().getIdMen()).isEqualTo("menage2");
        assertThat(menage5.getContratsMap()).hasSize(1);
        final List<Contract> contratsList5 = menage5.getContratsMap().values().iterator().next();
        assertThat(contratsList5).hasSize(1);
        assertThat(contratsList5.get(0).getIdMen()).isEqualTo("menage2");
    }
}
