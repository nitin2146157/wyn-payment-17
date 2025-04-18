package com.wyn.payment.serviceImpl;

import com.wyn.payment.entity.CardDetail;
import com.wyn.payment.entity.ClientDetail;
import com.wyn.payment.exception.CardDetailNotFound;
import com.wyn.payment.repository.CardDetailRepository;
import com.wyn.payment.repository.TransactionInfoRepository;
import com.wyn.payment.service.CardDetailService;
import com.wyn.payment.util.Gentools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CardDetailServiceImpl implements CardDetailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardDetailServiceImpl.class);

    @Autowired
    private CardDetailRepository cardDetailRepository;

    @Autowired
    private TransactionInfoRepository transactionInfoRepository;

    @Override
    @Transactional
    public CardDetail create(CardDetail cardDetail) {
        return cardDetailRepository.save(cardDetail);
    }

    @Override
    @Transactional
    public CardDetail findById(Optional<ClientDetail> clientDetail, int id) {
        return cardDetailRepository.findByTransactionInfo_ClientDetailAndId(clientDetail, id);
    }

    @Override
    @Transactional
    public List<CardDetail> findAll(Integer clientId) {
        return cardDetailRepository.findByTransactionInfo_ClientDetail_IdOrderByModifiedTsDescCreatedTsDesc(clientId);
    }

    @Override
    @Transactional(rollbackFor = CardDetailNotFound.class)
    public CardDetail update(CardDetail cardDetail) throws CardDetailNotFound {
        CardDetail updatedCardDetail = cardDetailRepository.findById(cardDetail.getId())
                .orElseThrow(CardDetailNotFound::new);

        updatedCardDetail.getTransactionInfo()
                .setTransactionStatus(cardDetail.getTransactionInfo().getTransactionStatus());
        updatedCardDetail.setModifiedTs(Calendar.getInstance().getTime());
        return updatedCardDetail;
    }

    // @Override
    // @Transactional(rollbackFor = CardDetailNotFound.class)
    // public CardDetail update(CardDetail cardDetail) throws CardDetailNotFound {
    // CardDetail updatedCardDetail =
    // cardDetailRepository.findById(cardDetail.getId())
    // .orElseThrow(CardDetailNotFound::new);

    // updatedCardDetail.getTransactionInfo()
    // .setTransactionStatus(cardDetail.getTransactionInfo().getTransactionStatus());
    // updatedCardDetail.setCardName(cardDetail.getCardName());// Ensure other
    // fields are updated
    // updatedCardDetail.setModifiedTs(Calendar.getInstance().getTime());
    // return cardDetailRepository.save(updatedCardDetail);// Save the changes
    // }

    @Override
    @Transactional
    public List<CardDetail> findByReferenceNo(String referenceNo) {
        return cardDetailRepository
                .findByTransactionInfo_ReferenceNumberOrderByModifiedTsDescCreatedTsDesc(referenceNo);
    }

    @Override
    @Transactional
    public int expireOldCardsbyDays(Integer days, Integer splitDays) {
        int cardExpiredCount = 0;
        LOGGER.info("CRON : expireOldCardsbyDays : Days {}", days);

        Date todayDate = Gentools.getDateFromDateTime(Calendar.getInstance().getTime());

        // Expire cards by days
        Calendar c = Calendar.getInstance();
        c.setTime(todayDate);
        c.add(Calendar.DATE, -days);
        Date expireDate = c.getTime();

        List<CardDetail> cardDetailList = cardDetailRepository
                .findByCreatedTsLessThanAndActiveAndSplitAndTransactionInfo_Active(expireDate, true, false, true);

        // Expire cards by split days
        c.setTime(todayDate);
        c.add(Calendar.DATE, -splitDays);
        expireDate = c.getTime();

        cardDetailList.addAll(cardDetailRepository
                .findByCreatedTsLessThanAndActiveAndSplitAndTransactionInfo_Active(expireDate, true, true, true));

        if (!cardDetailList.isEmpty()) {
            for (CardDetail cardDetail : cardDetailList) {
                expireCard(cardDetail);
                cardExpiredCount++;
                LOGGER.info("Expired card id: {} Transaction Info id: {}", cardDetail.getId(),
                        cardDetail.getTransactionInfo().getId());
            }
        } else {
            LOGGER.info("No cards to expire");
        }
        return cardExpiredCount;
    }

    @Override
    @Transactional
    public void expireCard(CardDetail cardDetail) {
        try {
            String cardNumber = cardDetail.getCardNumber();
            String last4Digits = cardNumber.length() == 15 ? cardNumber.substring(11, 15)
                    : cardNumber.substring(12, 16);
            String maskedCardNumber = cardNumber.length() == 15 ? "XXXXXXXXXXX" + last4Digits
                    : "XXXXXXXXXXXX" + last4Digits;

            cardDetail.setActive(false);
            cardDetail.setCardNumber(maskedCardNumber);
            cardDetail.setModifiedTs(Calendar.getInstance().getTime());

            cardDetail.getTransactionInfo().setActive(false);
            cardDetail.getTransactionInfo().setModifiedTs(Calendar.getInstance().getTime());

            transactionInfoRepository.save(cardDetail.getTransactionInfo());
            cardDetailRepository.save(cardDetail);
        } catch (Exception e) {
            LOGGER.error("Error in expire card", e);
        }
    }
}