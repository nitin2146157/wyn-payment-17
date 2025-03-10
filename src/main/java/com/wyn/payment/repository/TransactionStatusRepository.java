package com.wyn.payment.repository;

import com.wyn.payment.entity.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionStatusRepository extends JpaRepository<TransactionStatus,Integer> {

    public enum Status {
        INITIALIZED("INITIALIZED"),
        PENDING("PENDING"),
        PROCESSED("PROCESSED"),
        FAILED("FAILED"),
        EXPIRED("EXPIRED");

        private final String name;

        private Status(String s) {
            name = s;
        }

        public String toString(){
            return name;
        }
    }

    public TransactionStatus findByName(String name);

    public List<TransactionStatus> findByIdIn(List<Integer> ids);
}
