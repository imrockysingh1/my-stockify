package com.example.stockify.services;

import com.example.stockify.entities.OrderEntity;
import com.example.stockify.entities.PortfolioEntity;
import com.example.stockify.entities.WalletEntity;
import com.example.stockify.enums.TransactionType;
import com.example.stockify.repositories.OrderRepository;
import com.example.stockify.repositories.PortfolioRepository;
import com.example.stockify.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CancelEndOfDayPendingOrdersService {

    private final OrderRepository orderRepository;
    private final PortfolioRepository portfolioRepository;
    private final WalletRepository walletRepository;

    public CancelEndOfDayPendingOrdersService(OrderRepository orderRepository,
                                              PortfolioRepository portfolioRepository,
                                              WalletRepository walletRepository) {
        this.orderRepository = orderRepository;
        this.portfolioRepository = portfolioRepository;
        this.walletRepository = walletRepository;
    }

    @Scheduled(cron = "0 30 15 * * MON-FRI", zone = "Asia/Kolkata")
    @Transactional
    public void cancelEndOfDayPendingOrders() {
        List<OrderEntity> pendingOrders = orderRepository.findByStatus("PENDING");

        for (OrderEntity order : pendingOrders) {
            try {
                if (order.getType() == TransactionType.SELL) {
                    PortfolioEntity portfolio = portfolioRepository
                            .findByUserAndStockName(order.getUsers(), order.getSymbol())
                            .orElse(null);

                    if (portfolio != null) {
                        portfolio.setReservedQuantity(
                                portfolio.getReservedQuantity() - order.getQuantity()
                        );
                        portfolioRepository.save(portfolio);
                    }
                }

                if (order.getType() == TransactionType.BUY) {
                    WalletEntity wallet = walletRepository
                            .findById(order.getUsers().getUsername())
                            .orElse(null);

                    if (wallet != null) {
                        double refund = order.getPrice() * order.getQuantity();
                        wallet.setAmount(wallet.getAmount() + refund);
                        walletRepository.save(wallet);
                    }
                }

                order.setStatus("CANCELLED");
                orderRepository.save(order);

            } catch (Exception e) {
                System.out.println("Error cancelling order: " + order.getId());
                e.printStackTrace();
            }
        }
    }
}