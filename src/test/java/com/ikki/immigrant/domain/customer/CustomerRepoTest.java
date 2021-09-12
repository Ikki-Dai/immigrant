package com.ikki.immigrant.domain.customer;

import com.ikki.immigrant.infrastructure.config.DataJdbcConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;

import java.util.BitSet;

@DataJdbcTest
@Import(DataJdbcConfiguration.class)
public class CustomerRepoTest {

    @Autowired
    CustomerRepository customerRepository;

    @Test
    void save() {
        Customer customer = new Customer();
        BitSet bitSet = new BitSet();
        bitSet.set(8);
        customer.setCustomerName(bitSet);
        customerRepository.save(customer);
    }


}
