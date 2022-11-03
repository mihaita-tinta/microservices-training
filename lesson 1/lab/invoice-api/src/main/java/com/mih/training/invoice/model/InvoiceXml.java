package com.mih.training.invoice.model;

import jakarta.xml.bind.annotation.XmlRootElement;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;

@XmlRootElement(name = "Invoice", namespace = "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2")
public class InvoiceXml extends InvoiceType {

}
