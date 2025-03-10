package com.wyn.payment.repository;

import com.wyn.payment.entity.ClientDetail;
import com.wyn.payment.entity.TransactionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientDetailRepository extends JpaRepository<ClientDetail, Integer> {

    Optional<ClientDetail> findById(Integer id);
}
