package com.mih.training.invoice.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class InvoiceRepositoryTest {

    @Autowired
    InvoiceRepository repository;

    @Test
    public void test() {
        Invoice invoice = new Invoice();
        repository.saveAndFlush(invoice);
        assertNotNull(invoice.getId());
    }
}
