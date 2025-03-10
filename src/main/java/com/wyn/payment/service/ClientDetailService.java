package com.wyn.payment.service;

import com.wyn.payment.entity.ClientDetail;

import java.util.Optional;

public interface ClientDetailService {

    public Optional<ClientDetail> findById(Integer clientId);
}
