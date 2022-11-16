package com.mih.training.invoice;

import com.mih.training.invoice.convert.InvoiceXmlConverter;
import com.mih.training.invoice.model.InvoiceXml;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = InvoiceCommandLineRunner.class, args = "target/test-classes/xml")
class InvoiceCommandLineRunnerTest {

    @MockBean
    InvoiceXmlConverter converter;

    @Captor
    ArgumentCaptor<File> file;

    @Mock
    InvoiceXml xml1;

    @Autowired
    InvoiceCommandLineRunner runner;

    @Test
    public void test() {
        verify(converter).convert(file.capture());
        assertEquals("UBL-Invoice-2.1-Example.xml", file.getValue().getName());
    }

}
