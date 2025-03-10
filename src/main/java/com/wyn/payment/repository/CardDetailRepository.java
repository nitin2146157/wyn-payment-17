package com.wyn.payment.repository;

import com.wyn.payment.entity.CardDetail;
import com.wyn.payment.entity.ClientDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardDetailRepository extends JpaRepository<CardDetail, Integer> {


    List<CardDetail> findByCreatedTsLessThanAndActiveAndSplitAndTransactionInfo_Active(Date date1, boolean active, boolean split, boolean transActive);

    List<CardDetail> findByTransactionInfo_ReferenceNumberOrderByModifiedTsDescCreatedTsDesc(String refNo);

    List<CardDetail> findByTransactionInfo_ClientDetail_IdOrderByModifiedTsDescCreatedTsDesc(Integer clientId);

    CardDetail findByTransactionInfo_ClientDetailAndId(Optional<ClientDetail> clientDetail, Integer cardId);

    List<CardDetail> findAll();

}
