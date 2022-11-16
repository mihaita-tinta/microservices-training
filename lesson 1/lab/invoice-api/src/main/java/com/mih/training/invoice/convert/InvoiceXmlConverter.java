package com.mih.training.invoice.convert;

import com.mih.training.invoice.model.InvoiceXml;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.util.JAXBSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
            JAXBContext jaxbContext = JAXBContext.newInstance(InvoiceXml.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            InvoiceXml invoice = (InvoiceXml) jaxbUnmarshaller.unmarshal(source);

            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(xsd);
            Validator validator = schema.newValidator();
            validator.validate(new JAXBSource(jaxbContext, invoice));
            return invoice;
        } catch (JAXBException | SAXException e) {
            log.warn("convert - invalid XML, exception: " + e.getMessage(), e);
            throw new IllegalArgumentException("Invalid xml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
