package com.wyn.payment.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(name = "card_type")
public class CardType implements Serializable {


    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String cardTypeName;

//    @OneToOne
//    @JoinColumn(name = "card_detail_id")
//    private CardDetail cardDetail;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCardTypeName() {
        return cardTypeName;
    }

    public void setCardTypeName(String cardTypeName) {
        this.cardTypeName = cardTypeName;
    }

//    public CardDetail getCardDetail() {
//        return cardDetail;
//    }
//
//    public void setCardDetail(CardDetail cardDetail) {
//        this.cardDetail = cardDetail;
//    }
}
