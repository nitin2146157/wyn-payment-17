package com.wyn.payment.serviceImpl;

import com.wyn.payment.entity.ClientDetail;
import com.wyn.payment.entity.TransactionInfo;
import com.wyn.payment.entity.TransactionStatus;
import com.wyn.payment.repository.ClientDetailRepository;
import com.wyn.payment.repository.TransactionInfoRepository;
import com.wyn.payment.repository.TransactionStatusRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.wyn.payment.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Autowired
    private TransactionInfoRepository transactionInfoRepository;
    @Autowired
    private TransactionStatusRepository transactionStatusRepository;
    @Autowired
    private ClientDetailRepository clientDetailRepository;


    @Override
    @Transactional
    public String authenticateClient(ClientDetail clientDetail, String userName, String referenceNumber) {
        if (clientDetail != null) {
            TransactionStatus transactionStatus = transactionStatusRepository.findByName(TransactionStatusRepository.Status.INITIALIZED.toString());

            // Find if there exists previous transaction calls with same parameters which can be reused, if exists simply return
            TransactionInfo transactionInfo = findIfRequestParamsExists(clientDetail, userName, referenceNumber, transactionStatus);

            if (transactionInfo == null) {
                // No previous transaction calls with same parameters, so create a new one
                transactionInfo = new TransactionInfo();

                String hashKey = clientDetail.getClientName() + userName + referenceNumber + Calendar.getInstance().getTimeInMillis();

                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String hashKeyMd5 = passwordEncoder.encode(hashKey);
                LOGGER.info("Encoded hashKeyMd5: {}", hashKeyMd5);

                transactionInfo.setClientDetail(clientDetail);
                transactionInfo.setUserName(userName);
                transactionInfo.setReferenceNumber(referenceNumber);
                transactionInfo.setTransactionStatus(transactionStatus);
                transactionInfo.setHashKey(hashKeyMd5);
                transactionInfo.setCreatedTs(Calendar.getInstance().getTime());
                transactionInfo.setActive(true);

                if (transactionInfoRepository.save(transactionInfo) != null) {
                    LOGGER.info("TransactionInfo saved with hashKeyMd5: {}", transactionInfo.getHashKey());
                    return hashKeyMd5;
                }
                return null;
            } else {
                return transactionInfo.getHashKey();
            }
        }
        return null;
    }

    @Override
    @Transactional
    public TransactionInfo findIfClientIsAuthenticated(String hashKey) {
        TransactionStatus transactionStatus = transactionStatusRepository.findByName(TransactionStatusRepository.Status.INITIALIZED.toString());
        List<TransactionInfo> transactionInfoList = transactionInfoRepository.findByHashKeyAndTransactionStatus(hashKey, transactionStatus);

        if (!transactionInfoList.isEmpty()) {
            for (TransactionInfo transactionInfo : transactionInfoList) {
                return isTransactionInfoValid(transactionInfo);
            }
        }
        return null;
    }

    @Override
    @Transactional
    public TransactionStatus findByTransactionStatusName(String transactionStatusName) {
        return transactionStatusRepository.findByName(transactionStatusName);
    }

    @Override
    @Transactional
    public TransactionInfo updateTransactionInfo(TransactionInfo transactionInfo) {
        return transactionInfoRepository.save(transactionInfo);
    }

    @Override
    @Transactional
    public Optional<TransactionInfo> findById(Integer id) {
        return transactionInfoRepository.findById(id);
    }

    @Override
    @Transactional
    public List<TransactionStatus> findByTransactionStatusIdIn(List<Integer> id) {
        return transactionStatusRepository.findByIdIn(id);
    }

    @Override
    @Transactional
    public Optional<TransactionStatus> findByTransactionStatusId(Integer id) {
        return transactionStatusRepository.findById(id);
    }

    @Override
    @Transactional
    public TransactionInfo findIfRequestParamsExists(ClientDetail clientDetail, String userName, String referenceNumber, TransactionStatus transactionStatus) {
        List<TransactionInfo> transactionInfoList = transactionInfoRepository.findByClientDetailAndReferenceNumberAndUserNameAndTransactionStatusOrderByCreatedTsDesc(clientDetail, referenceNumber, userName, transactionStatus);
        if (transactionInfoList.isEmpty())
            return null;
        else
            return isTransactionInfoValid(transactionInfoList.get(0));
    }

    private TransactionInfo isTransactionInfoValid(TransactionInfo transactionInfo) {
        if (transactionInfo.getCreatedTs() != null) {
            long millis = Calendar.getInstance().getTimeInMillis() - transactionInfo.getCreatedTs().getTime();
            if (millis <= 1800000) { // 30*60*1000 (30 minutes)
                return transactionInfo;
            } else {
                return null;
            }
        }
        return null;
    }

    public TransactionInfo createObject(TransactionInfo object){


        ClientDetail clientDetail = object.getClientDetail();
        if (clientDetail == null) {
            clientDetail = new ClientDetail();
        }

        clientDetail.setClientDesc(object.getClientDetail().getClientDesc());
        clientDetail.setClientName(object.getClientDetail().getClientName());

        ClientDetail details = clientDetailRepository.save(clientDetail);

        TransactionStatus transactionStatus = new TransactionStatus();
        transactionStatus.setName(object.getTransactionStatus().getName());
        TransactionStatus status = transactionStatusRepository.save(transactionStatus);

        object.setClientDetail(details);
        object.setTransactionStatus(status);

        return transactionInfoRepository.save(object);
    }

}

