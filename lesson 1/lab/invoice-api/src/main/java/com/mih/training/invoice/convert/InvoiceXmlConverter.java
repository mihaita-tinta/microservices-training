package com.mih.training.invoice.convert;

import com.mih.training.invoice.model.InvoiceXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

@Component
public class InvoiceXmlConverter implements Converter<File, InvoiceXml> {
    private static final Logger log = LoggerFactory.getLogger(InvoiceXmlConverter.class);

    private final File xsd;

    public InvoiceXmlConverter() throws FileNotFoundException {
        this.xsd = ResourceUtils.getFile("classpath:UBL-2.3/xsd/maindoc/UBL-Invoice-2.3.xsd");
    }

    @Override
    public InvoiceXml convert(File source) {
        return null;
    }

}
