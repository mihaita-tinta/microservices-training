package com.mih.training.invoice.convert;

import com.mih.training.invoice.model.InvoiceXml;
import com.mih.training.invoice.repository.Invoice;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
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
        // FIXME deserialize Invoice and validate
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Invoice.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            Invoice invoice = null;

            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(xsd);
            Validator validator = schema.newValidator();

        } catch (JAXBException | SAXException e) {
            log.warn("convert - invalid XML, exception: " + e.getMessage(), e);
            throw new IllegalArgumentException("Invalid xml");
        }
        return null;
    }

}
