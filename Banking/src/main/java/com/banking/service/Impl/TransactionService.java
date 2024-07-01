package com.banking.service.Impl;

import com.banking.dto.TransactionDto;
import com.banking.entity.Transaction;
import org.springframework.stereotype.Service;

@Service
public interface TransactionService {

    void saveTransaction(TransactionDto transactionDto);
}
