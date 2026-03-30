package com.example.stockify.services;

import com.example.stockify.entities.OrderEntity;
import com.example.stockify.entities.WalletEntity;
import com.example.stockify.enums.TransactionType;
import com.example.stockify.repositories.OrderRepository;
import com.example.stockify.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderSchedulerService {

    private final OrderRepository orderRepository;
    private final StockService stockService;
    private final WalletRepository walletRepository;
    private final OrderService orderService;
    private final TransactionService transactionService;

    public OrderSchedulerService(OrderRepository orderRepository,
                                 StockService stockService,
                                 WalletRepository walletRepository,
                                 OrderService orderService,
                                 TransactionService transactionService) {
        this.orderRepository = orderRepository;
        this.stockService = stockService;
        this.walletRepository = walletRepository;
        this.orderService = orderService;
        this.transactionService = transactionService;
    }

    // Runs every 10 seconds
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void processPendingOrders() {

        List<OrderEntity> pendingOrders = orderRepository.findByStatus("PENDING");

        for (OrderEntity order : pendingOrders) {

            try {
                double currentPrice = stockService
                        .getStock(order.getSymbol(), "1d", "1m")
                        .getMeta()
                        .getPrice();

                // BUY condition
                if (currentPrice <= order.getPrice()) {
                    System.out.println("currentPrice   -------------" + currentPrice);
                    System.out.println("Order price     -------------" + order.getPrice());

                    double executionPrice = currentPrice;
                    double totalAmount = executionPrice * order.getQuantity();

//                    var wallet = walletRepository.findById(order.getUsers().getUsername())
//                            .orElseThrow();
                    WalletEntity wallet = walletRepository.findById(order.getUsers().getUsername())
                            .orElseThrow();

                    // Check balance
                    if (wallet.getAmount() < totalAmount) {
                        System.out.println("Insufficient funds, adding missing amount...");

                        // Add funds to wallet
                        double needed = totalAmount - wallet.getAmount();
                        wallet.setAmount(wallet.getAmount() + needed + 1000); // optional extra buffer
                        walletRepository.save(wallet);

                        System.out.println("Wallet updated, new balance: " + wallet.getAmount());
                    }

                    // Deduct money
                    wallet.setAmount(wallet.getAmount() - totalAmount);
                    walletRepository.save(wallet);

                    // Update portfolio
                    orderService.updatePortfolio(
                            order.getUsers(),
                            order.getSymbol(),
                            order.getQuantity(),
                            executionPrice
                    );

                    // Update order
                    order.setStatus("EXECUTED");
                    order.setPrice(executionPrice);
                    orderRepository.save(order);

                    // Save transaction
                    transactionService.createTransaction(
                            order.getUsers(),
                            order.getSymbol(),
                            order.getQuantity(),
                            executionPrice,
                            TransactionType.BUY
                    );
                }

            } catch (Exception e) {
                System.out.println("Error processing order: " + order.getId());
                e.printStackTrace();
            }
        }
    }
}