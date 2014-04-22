package com.secret.client.csv;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import com.secret.client.model.Contract;

@RunWith(MockitoJUnitRunner.class)
public class ContractDataLoaderTest {

    @Test
    public void should_load_data_from_sample() throws Exception {
        //Given
        final ContractDataLoader loader = new ContractDataLoader("src/test/resources/small_contrats.csv");

        //When
        List<Contract> contracts = new ArrayList<Contract>();
        while (loader.hasNext()) {
            contracts.add(loader.next());
        }

        //Then
        assertThat(contracts).hasSize(5);
        assertThat(contracts).extracting("idMen").containsExactly("10478228251292368896",
                "88869812558057177088",
                "82826648660985348096",
                "201815350675704217600",
                "91000596632");
    }
}
