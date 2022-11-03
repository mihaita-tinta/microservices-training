package com.mih.training.invoice.convert;

import com.mih.training.invoice.model.InvoiceXml;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = InvoiceXmlConverter.class)
class InvoiceXmlConverterTest {
    private static final Logger log = LoggerFactory.getLogger(InvoiceXmlConverterTest.class);

    @Autowired
    InvoiceXmlConverter converter;

    @Test
    public void testValidate() throws FileNotFoundException {
        File input = ResourceUtils.getFile("classpath:UBL-2.3/xml/UBL-Invoice-2.0-Example.xml");

        InvoiceXml invoice = converter.convert(input);

        assertEquals("A00095678", invoice.getID().getValue());
    }
}
