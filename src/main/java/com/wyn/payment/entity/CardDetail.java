package com.wyn.payment.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.type.YesNoConverter;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;


@Entity
@Table(name = "card_detail")
public class CardDetail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotEmpty(message = "Card Holder name must not be empty")
    @Size(max = 100, message = "Card Holder name must be between 0 and 100 characters")
    @Column(name = "card_name")
    private String cardName;

    @NotEmpty(message = "Card number must not be empty")
    @Size(max = 50, message = "Card Number must be between 0 and 50 characters")
    @Column(name = "card_number")
    private String cardNumber;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "card_type_id")
    private CardType cardType;

    @NotEmpty
    @Column(name = "expiry_mm")
    private String expiryMm;

    @NotEmpty
    @Column(name = "expiry_yy")
    private String expiryYy;

    @NotEmpty
    @Column(name = "currency")
    private String currency;

    @Digits(integer = 10, fraction = 0, message = "Amount must be a valid number")
    @Column(name = "amount")
    private String amount;

    @Size(max = 255, message = "Notes must be between 0 and 255 characters")
    @Column(name = "notes")
    private String notes;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "split")
    private boolean split;

    @Convert(converter = YesNoConverter.class)
    @Column(name = "active")
    private boolean active;

    @OneToOne
    @JoinColumn(name = "transaction_info_id")
    private TransactionInfo transactionInfo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_ts")
    private Date createdTs;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_ts")
    private Date modifiedTs;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public String getExpiryMm() {
        return expiryMm;
    }

    public void setExpiryMm(String expiryMm) {
        this.expiryMm = expiryMm;
    }

    public String getExpiryYy() {
        return expiryYy;
    }

    public void setExpiryYy(String expiryYy) {
        this.expiryYy = expiryYy;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isSplit() {
        return split;
    }

    public void setSplit(boolean split) {
        this.split = split;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public TransactionInfo getTransactionInfo() {
        return transactionInfo;
    }

    public void setTransactionInfo(TransactionInfo transactionInfo) {
        this.transactionInfo = transactionInfo;
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
}