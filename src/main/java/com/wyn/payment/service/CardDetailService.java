package com.wyn.payment.service;

import com.wyn.payment.entity.CardDetail;
import com.wyn.payment.entity.ClientDetail;
import com.wyn.payment.exception.CardDetailNotFound;

import java.util.List;
import java.util.Optional;

public interface CardDetailService {

    public CardDetail create(CardDetail cardDetail);
    public List<CardDetail> findAll(Integer clientId);
    public CardDetail update(CardDetail cardDetail) throws CardDetailNotFound;
    public CardDetail findById(Optional<ClientDetail> clientDetail, int id);
    public int expireOldCardsbyDays(Integer days, Integer splitDays);
    public List<CardDetail> findByReferenceNo(String referenceNo);
    public void expireCard(CardDetail cardDetail);

}
