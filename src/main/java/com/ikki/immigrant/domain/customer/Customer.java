package com.ikki.immigrant.domain.customer;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.BitSet;

@Table
@Getter
@Setter
@Slf4j
public class Customer {

    @Id
    private long id;
    private BitSet customerName;
}
