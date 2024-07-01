package com.banking.service.Impl;

import com.banking.dto.TransactionDto;
import com.banking.entity.Transaction;
import com.banking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component

public class TransactionImpl implements TransactionService {

    @Autowired
     TransactionRepository transactionRepository;
    @Override
    public void saveTransaction(TransactionDto transactionDto) {
        Transaction transaction = Transaction.builder()
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .amount(transactionDto.getAmount())
                .status("Success")

                .build();
        transactionRepository.save(transaction);
        System.out.println("transaction saved successfully");

    }
}
