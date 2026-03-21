package com.example.stockify.services;

import com.example.stockify.entities.TransactionEntity;
import com.example.stockify.entities.UserEntity;
import com.example.stockify.enums.TransactionType;
import com.example.stockify.enums.TradeType;
import com.example.stockify.repositories.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void createTransaction(UserEntity username,
                                  String stock,
                                  int qty,
                                  double price,
                                  TransactionType type) {

        TransactionEntity txn = new TransactionEntity();

        txn.setUser(username);
        txn.setStockName(stock);
        txn.setQuantity(qty);
        txn.setPrice((float) price);
        txn.setAmount((float) (qty * price));
        txn.setType(type);
        txn.setTradeType(TradeType.DELIVERY);

        transactionRepository.save(txn);
    }
}