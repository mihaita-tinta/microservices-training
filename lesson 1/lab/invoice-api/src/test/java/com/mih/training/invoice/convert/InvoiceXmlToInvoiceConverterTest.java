package com.mih.training.invoice.convert;

import com.mih.training.invoice.model.InvoiceXml;
import com.mih.training.invoice.repository.Invoice;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = InvoiceXmlToInvoiceConverter.class)
class InvoiceXmlToInvoiceConverterTest {
    private static final Logger log = LoggerFactory.getLogger(InvoiceXmlToInvoiceConverterTest.class);

    @Autowired
    InvoiceXmlToInvoiceConverter converter;

    @Mock
    InvoiceXml invoiceXml;

    @Autowired
    ApplicationContext context;
    @Mock
    IDType idType;

    @Test
    public void testValidate() {
        when(invoiceXml.getID()).thenReturn(idType);
        String EXPECTED = "A00095678";
        when(idType.getValue()).thenReturn(EXPECTED);

        Invoice invoice = converter.convert(invoiceXml);

        assertEquals(EXPECTED, invoice.getUblId());
    }
}
