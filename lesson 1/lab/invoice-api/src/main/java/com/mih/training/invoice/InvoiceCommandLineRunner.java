package com.mih.training.invoice;

import com.mih.training.invoice.convert.InvoiceXmlConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.stream.Stream;

@Service
public class InvoiceCommandLineRunner implements CommandLineRunner {

    private final InvoiceXmlConverter converter;

    public InvoiceCommandLineRunner(InvoiceXmlConverter converter) {
        this.converter = converter;
    }

    @Override
    public void run(String... args) throws Exception {

        if (args.length == 0) {
            return;
        }
        File input = new File(args[0]);
        if (input.isDirectory()) {
            Stream.of(input.listFiles())
                    .forEach(converter :: convert);
        }
    }
}
