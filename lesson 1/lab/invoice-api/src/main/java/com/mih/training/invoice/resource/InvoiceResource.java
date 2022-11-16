package com.mih.training.invoice.resource;

import com.mih.training.invoice.repository.Invoice;
import com.mih.training.invoice.repository.InvoiceRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class InvoiceResource {

    private final InvoiceRepository invoiceRepository;

    public InvoiceResource(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping("/api/invoices")
    public List<Invoice> getInvoices(@AuthenticationPrincipal Principal currentUser) {
        return invoiceRepository.findAll();
    }


}
