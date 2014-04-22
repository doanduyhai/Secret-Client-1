package com.secret.client.business;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import com.secret.client.cassandra.ContractDao;
import com.secret.client.csv.ContractDataLoader;
import com.secret.client.model.Contract;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.mutation.Mutator;

@RunWith(MockitoJUnitRunner.class)
public class ContractLoaderStepTest {

    private ContractLoaderStep step;

    @Mock
    private Keyspace keyspace;

    @Mock
    private ContractDao contractDao;

    @Mock
    private ContractDataLoader loader;

    @Mock
    private Mutator<String> contractMutator;

    @Before
    public void setUp() {
        step = new ContractLoaderStep(null, keyspace, null, loader);
        step.loggingInterval = 1;
        step.batchInsertDelay = 1;
        step.maximumRowSize = 100;
        step.contractDao = contractDao;
        when(contractDao.createMutator()).thenReturn(contractMutator);
    }

    @Test
    public void should_insert_contracts() throws Exception {
        //Given
        step.batchInsertSize = 1;
        step.batchInsertDelay = 1;
        Contract contract = new Contract();

        when(loader.hasNext()).thenReturn(true, false);
        when(loader.next()).thenReturn(contract);

        //When
        step.run();

        //Then
        verify(contractDao).insertContratForClient(contractMutator,contract);
    }

    @Test
    public void should_insert_contracts_and_flush() throws Exception {
        //Given
        step.batchInsertSize = 0;
        step.batchInsertDelay = 1;
        Contract contract = new Contract();

        when(loader.hasNext()).thenReturn(true, false);
        when(loader.next()).thenReturn(contract);

        //When
        step.run();

        //Then
        verify(contractDao).insertContratForClient(contractMutator,contract);
        verify(contractMutator).execute();
    }
}
