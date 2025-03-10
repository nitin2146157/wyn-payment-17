package com.wyn.payment.serviceImpl;

import com.wyn.payment.entity.ClientDetail;
import com.wyn.payment.repository.ClientDetailRepository;
import com.wyn.payment.service.ClientDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ClientDetailServiceImpl implements ClientDetailService {


    @Autowired
    private ClientDetailRepository clientDetailRepository;

    @Override
    @Transactional
    public Optional<ClientDetail> findById(Integer clientId) {
        return clientDetailRepository.findById(clientId);
    }

}
