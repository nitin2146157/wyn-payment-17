package com.wyn.payment.service;

import com.wyn.payment.entity.ClientDetail;
import com.wyn.payment.entity.TransactionInfo;
import com.wyn.payment.entity.TransactionStatus;

import java.util.List;
import java.util.Optional;

public interface TransactionService {

    public String authenticateClient(ClientDetail clientDetail, String userName, String referenceNumber);

    public TransactionInfo findIfClientIsAuthenticated(String hashKey);

    Optional<TransactionInfo> findById(Integer id);

    public Optional<TransactionStatus> findByTransactionStatusId(Integer id);

    public TransactionStatus findByTransactionStatusName(String transactionStatusName);

    public TransactionInfo updateTransactionInfo(TransactionInfo transactionInfo);

    public List<TransactionStatus> findByTransactionStatusIdIn(List<Integer> id);

    public TransactionInfo findIfRequestParamsExists(ClientDetail clientDetail, String userName, String referenceNumber, TransactionStatus transactionStatus);

}
