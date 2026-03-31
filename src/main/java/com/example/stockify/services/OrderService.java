package com.example.stockify.services;

import com.example.stockify.dto.BuyOrderRequestDTO;
import com.example.stockify.dto.StockResponseDTO;
import com.example.stockify.entities.*;
import com.example.stockify.enums.TransactionType;
import com.example.stockify.exception.InsufficientBalanceException;
import com.example.stockify.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PortfolioRepository portfolioRepository;
    private final OrderRepository orderRepository;
    private final StockService stockService;
    private final TransactionService transactionService;
    private final ModelMapper modelMapper;
    private final MarketTimeService marketTimeService;

    public OrderService(UserRepository userRepository,
                        WalletRepository walletRepository,
                        PortfolioRepository portfolioRepository,
                        OrderRepository orderRepository,
                        StockService stockService,
                        TransactionService transactionService,
                        ModelMapper modelMapper,
                        MarketTimeService marketTimeService) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.portfolioRepository = portfolioRepository;
        this.orderRepository = orderRepository;
        this.stockService = stockService;
        this.transactionService = transactionService;
        this.modelMapper = modelMapper;
        this.marketTimeService = marketTimeService;
    }

    @Transactional
    public BuyOrderRequestDTO buyStock(String username, BuyOrderRequestDTO request) {

        if (request.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        if ("LIMIT".equalsIgnoreCase(request.getOrderType()) && request.getPrice() <= 0) {
            throw new RuntimeException("Price must be greater than 0 for LIMIT orders");
        }

        UserEntity user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        WalletEntity wallet = walletRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        StockResponseDTO stock = stockService.getStock(request.getSymbol(), "1d", "1m");

        if (stock == null || stock.getMeta() == null) {
            throw new RuntimeException("Stock data unavailable");
        }

        double currentPrice = stock.getMeta().getPrice();
        double executionPrice;
        String orderStatus;

        boolean marketOpen = marketTimeService.isMarketOpen();

        if ("MARKET".equalsIgnoreCase(request.getOrderType())) {
            if (marketOpen) {
                orderStatus = "EXECUTED";
                executionPrice = currentPrice;
            } else {
                orderStatus = "PENDING";
                executionPrice = currentPrice; // reserve funds at current price
            }
        } else if ("LIMIT".equalsIgnoreCase(request.getOrderType())) {
            if (!marketOpen || currentPrice > request.getPrice()) {
                orderStatus = "PENDING";
                executionPrice = request.getPrice(); // reserve funds for pending
            } else {
                orderStatus = "EXECUTED";
                executionPrice = Math.min(currentPrice, request.getPrice());
            }
        } else {
            throw new RuntimeException("Invalid order type");
        }

        double totalAmount = executionPrice * request.getQuantity();
        if (wallet.getAmount() < totalAmount) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        wallet.setAmount(wallet.getAmount() - totalAmount);
        walletRepository.save(wallet);


        if ("EXECUTED".equals(orderStatus)) {
            updatePortfolio(user, request.getSymbol(), request.getQuantity(), executionPrice);

            transactionService.createTransaction(
                    user,
                    request.getSymbol(),
                    request.getQuantity(),
                    executionPrice,
                    TransactionType.BUY
            );
        }


        OrderEntity order = new OrderEntity();
        order.setUsers(user);
        order.setSymbol(request.getSymbol());
        order.setQuantity(request.getQuantity());
        order.setOrderType(request.getOrderType());
        order.setPrice(executionPrice);
        order.setStatus(orderStatus);

        OrderEntity savedOrder = orderRepository.save(order);

        BuyOrderRequestDTO dto = modelMapper.map(savedOrder, BuyOrderRequestDTO.class);
        dto.setStatus(savedOrder.getStatus()); // Ensure status is returned to frontend

        return dto;
    }

    public void updatePortfolio(UserEntity user, String stock, int qty, double price) {
        PortfolioEntity portfolio = portfolioRepository.findByUserAndStockName(user, stock)
                .orElse(null);

        if (portfolio != null) {
            int totalQty = portfolio.getQuantity() + qty;
            double newAvg = ((portfolio.getAveragePrice() * portfolio.getQuantity()) + (price * qty)) / totalQty;
            portfolio.setQuantity(totalQty);
            portfolio.setAveragePrice((float) newAvg);
            portfolioRepository.save(portfolio);
        } else {
            PortfolioEntity newPortfolio = new PortfolioEntity();
            newPortfolio.setUser(user);
            newPortfolio.setStockName(stock);
            newPortfolio.setQuantity(qty);
            newPortfolio.setAveragePrice((float) price);
            portfolioRepository.save(newPortfolio);
        }
    }
}