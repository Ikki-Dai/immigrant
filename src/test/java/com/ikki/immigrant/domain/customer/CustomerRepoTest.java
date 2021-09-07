package com.ikki.immigrant.domain.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.BitSet;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CustomerRepoTest {

    @Autowired
    CustomerRepository customerRepository;

    public void save() {
        Customer customer = new Customer();

        BitSet bitSet = new BitSet();
        bitSet.set(8);
        customer.setCustomerName(bitSet);
        customerRepository.save(customer);
    }


}
