package com.wyn.payment.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "transaction_status")
public class TransactionStatus implements Serializable {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name")
    private String name;

//    @OneToOne(mappedBy = "transactionStatus")
//    private TransactionInfo transactionInfo;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public TransactionInfo getTransactionInfo() {
//        return transactionInfo;
//    }
//
//    public void setTransactionInfo(TransactionInfo transactionInfo) {
//        this.transactionInfo = transactionInfo;
//    }


}
