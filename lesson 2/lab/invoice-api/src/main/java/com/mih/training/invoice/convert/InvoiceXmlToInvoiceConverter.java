package com.mih.training.invoice.convert;

import com.mih.training.invoice.model.InvoiceXml;
import com.mih.training.invoice.repository.Invoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

@Component
public class InvoiceXmlToInvoiceConverter implements Converter<InvoiceXml, Invoice> {
    private static final Logger log = LoggerFactory.getLogger(InvoiceXmlToInvoiceConverter.class);

    @Override
    public Invoice convert(InvoiceXml source) {
        return null;
    }

}
