package com.wyn.payment.entity;

import jakarta.persistence.*;
import org.hibernate.type.YesNoConverter;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

@Entity
@Table(name = "transaction_info")
public class TransactionInfo implements Serializable {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name="client_detail_id")
    private ClientDetail clientDetail;

    @OneToOne
    @JoinColumn(name="transaction_status_id")
    private TransactionStatus transactionStatus;

    @Column(name="reference_number")
    private String referenceNumber;

    @Column(name="user_name")
    private String userName;

    @Column(name="hashkey")
    private String hashKey;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_ts")
    private Date createdTs;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_ts")
    private Date modifiedTs;

    @Convert(converter = YesNoConverter.class)
    @Column(name="active")
    private boolean active;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ClientDetail getClientDetail() {
        return clientDetail;
    }

    public void setClientDetail(ClientDetail clientDetail) {
        this.clientDetail = clientDetail;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(Optional<TransactionStatus> transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public Date getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(Date createdTs) {
        this.createdTs = createdTs;
    }

    public Date getModifiedTs() {
        return modifiedTs;
    }

    public void setModifiedTs(Date modifiedTs) {
        this.modifiedTs = modifiedTs;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
