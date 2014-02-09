package com.secret.client.init;

import static org.fest.assertions.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import com.secret.client.model.Client;
import com.secret.client.model.Contract;

@RunWith(MockitoJUnitRunner.class)
public class CsvDataLoaderTest {

    private CsvDataLoader loader = new CsvDataLoader();

    @Test
    public void should_load_clients_from_csv() throws Exception {
        //Given
        String clientsCSVTestFile = "src/test/resources/small_clients.csv";

        //When
        final Map<String,Client> clientsMap = loader.loadClients(clientsCSVTestFile);

        //Then
        assertThat(clientsMap).hasSize(5);
        assertThat(clientsMap.keySet()).contains("91000596632","91001070221","91001276760","91001413502","91001429252");
        assertThat(extractProperty("idMen",String.class).from(clientsMap.values()))
                .contains("91000596632", "91001070221", "91001276760", "91001413502", "91001429252");
    }

    @Test
    public void should_load_contrats_from_csv() throws Exception {
        //Given
        String contratsCSVTestFile = "src/test/resources/small_contrats.csv";

        //When
        final Map<String,List<Contract>> contratsMap = loader.loadContrats(contratsCSVTestFile);

        //Then
        assertThat(contratsMap).hasSize(5);
        assertThat(contratsMap.keySet()).contains("91000095004","91000163758","91000274052","91000585152","91000596632");
        assertThat(contratsMap.get("91000095004").get(0).getIdMen()).isEqualTo("10478228251292368896");
        assertThat(contratsMap.get("91000163758").get(0).getIdMen()).isEqualTo("88869812558057177088");
        assertThat(contratsMap.get("91000274052").get(0).getIdMen()).isEqualTo("82826648660985348096");
        assertThat(contratsMap.get("91000585152").get(0).getIdMen()).isEqualTo("201815350675704217600");
        assertThat(contratsMap.get("91000596632").get(0).getIdMen()).isEqualTo("91000596632");
    }
}
