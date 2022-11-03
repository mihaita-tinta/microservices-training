package com.mih.training.invoice.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Invoice {

    @Id
    @GeneratedValue
    private Long id;
    private String ublId;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getUblId() {
        return ublId;
    }

    public void setUblId(String ublId) {
        this.ublId = ublId;
    }
}
