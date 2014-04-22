package com.secret.client.csv;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import com.secret.client.model.Client;

@RunWith(MockitoJUnitRunner.class)
public class ClientDataLoaderTest {

    @Test
    public void should_load_data_from_sample() throws Exception {
        //Given
        final ClientDataLoader loader = new ClientDataLoader("src/test/resources/small_clients.csv");

        //When
        List<Client> clients = new ArrayList<Client>();
        while (loader.hasNext()) {
            clients.add(loader.next());
        }

        //Then
        assertThat(clients).hasSize(5);
        assertThat(clients).extracting("idMen").containsExactly("91000596632", "91001070221", "91001276760", "91001413502", "91001429252");
    }
}
