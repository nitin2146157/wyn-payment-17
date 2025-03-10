package com.wyn.payment.repository;

import com.wyn.payment.entity.ClientDetail;
import com.wyn.payment.entity.TransactionInfo;
import com.wyn.payment.entity.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionInfoRepository extends JpaRepository<TransactionInfo,Integer> {

    Optional<TransactionInfo> findById(Integer id);
     List<TransactionInfo> findByHashKeyAndTransactionStatus(String hashKey, TransactionStatus transactionStatus);
     List<TransactionInfo> findByReferenceNumber(String referenceNumber);
     List<TransactionInfo> findByClientDetailAndReferenceNumberAndUserNameAndTransactionStatusOrderByCreatedTsDesc(ClientDetail clientDetail, String referenceNumber, String userName, TransactionStatus transactionStatus);
}

