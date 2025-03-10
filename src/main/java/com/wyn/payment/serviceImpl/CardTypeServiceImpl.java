package com.wyn.payment.serviceImpl;

import com.wyn.payment.entity.CardType;
import com.wyn.payment.repository.CardTypeRepository;
import com.wyn.payment.service.CardTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CardTypeServiceImpl implements CardTypeService {

    @Autowired
    private CardTypeRepository cardTypeRepository;

    @Override
    @Transactional
    public List<CardType> findAll() {
        return cardTypeRepository.findAll();
    }
}
