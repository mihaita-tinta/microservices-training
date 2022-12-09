package com.mih.training.invoice;

import com.mih.training.invoice.Account;
import com.mih.training.invoice.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
class AccountServiceTest {

    @Autowired
    AccountService service;

    @Test
    public void test() throws ExecutionException, InterruptedException {

        List<Account> actual = service.getAccounts()
                .get();
        assertNotNull(actual);

    }

}
