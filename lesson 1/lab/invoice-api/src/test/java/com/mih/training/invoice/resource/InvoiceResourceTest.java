package com.mih.training.invoice.resource;

import com.mih.training.invoice.repository.Invoice;
import com.mih.training.invoice.repository.InvoiceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest
class InvoiceResourceTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    InvoiceRepository repository;

    @Test
    @WithMockUser(username = "asdas")
    public void testGetAll() throws Exception {

        Invoice invoice = new Invoice();
        invoice.setId(123L);
        when(repository.findAll()).thenReturn(asList(invoice));

        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/invoices"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("123"))
                .andDo(MockMvcResultHandlers.print());
    }
    @Test
    @WithMockUser
    public void testWithFilters() throws Exception {

        // TODO setup test conditions here
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/invoices?page=0")
                                .queryParam("page", "0")
                                .queryParam("limit", "10")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value("10"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithMockUser
    public void testById() throws Exception {

        // TODO setup test conditions here
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/invoices/{id}", "123"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("123"))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    @WithMockUser
    public void testInvoiceCanBePayed() throws Exception {

        Invoice invoice = new Invoice();
        invoice.setId(123L);
        when(repository.findAll()).thenReturn(asList(invoice));

        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/invoices/123/payments/verify"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.canBePayed").value("true"))
                .andExpect(jsonPath("$.account").isNotEmpty())
                .andDo(MockMvcResultHandlers.print());
    }

}
