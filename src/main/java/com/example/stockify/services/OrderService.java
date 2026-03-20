package com.example.stockify.services;

import com.example.stockify.dto.BuyOrderRequestDTO;
import com.example.stockify.dto.StockResponseDTO;
import com.example.stockify.entities.*;
import com.example.stockify.enums.TransactionType;
//import com.example.stockify.helpers.Helper.*;
import com.example.stockify.repositories.*;
import jakarta.transaction.Transactional;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
//import com.example.stockify.helpers.Helper.*;

@Service
public class OrderService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PortfolioRepository portfolioRepository;
    private final OrderRepository orderRepository;
    private final StockService stockService;
    private final TransactionService transactionService;
    private final ModelMapper modelMapper;

    public OrderService(UserRepository userRepository,
                        WalletRepository walletRepository,
                        PortfolioRepository portfolioRepository,
                        OrderRepository orderRepository,
                        StockService stockService,
                        TransactionService transactionService, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.portfolioRepository = portfolioRepository;
        this.orderRepository = orderRepository;
        this.stockService = stockService;
        this.transactionService = transactionService;
        this.modelMapper = modelMapper;

    }

    @Transactional
    public BuyOrderRequestDTO buyStock(String username, BuyOrderRequestDTO request) {

        UserEntity user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        WalletEntity wallet = walletRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        StockResponseDTO stock = stockService.getStock(request.getSymbol(), "1d", "1m");

        double currentPrice = stock.getMeta().getPrice();

        double executionPrice;

        if ("MARKET".equalsIgnoreCase(request.getOrderType())) {
            executionPrice = currentPrice;
        } else {
            if ("LIMIT".equalsIgnoreCase(request.getOrderType()) && currentPrice > request.getPrice()) {

                OrderEntity toSaveOrder = modelMapper.map(request , OrderEntity.class);
                OrderEntity savedOrder = orderRepository.save((toSaveOrder));

//                OrderEntity order = new OrderEntity();
//                order.setUsers(user);
//                order.setSymbol(request.getSymbol());
//                order.setQuantity(request.getQuantity());
//                order.setOrderType(orderType);
//                order.setPrice(limitPrice);
//                order.setStatus("PENDING");

//                orderRepository.save(order);

                return modelMapper.map(savedOrder,BuyOrderRequestDTO.class); //till here
            }
            executionPrice = request.getPrice();
        }

        double totalAmount = executionPrice * request.getQuantity();

        if (wallet.getAmount() < totalAmount) {
            throw new RuntimeException("Insufficient balance");
        }

        // ✅ Deduct wallet
        wallet.setAmount(wallet.getAmount() - totalAmount);

        updatePortfolio(user, request.getSymbol(), request.getQuantity(), executionPrice);


        OrderEntity order = new OrderEntity();
        order.setUsers(user);
        order.setSymbol(request.getSymbol());
        order.setQuantity(request.getQuantity());
        order.setOrderType(request.getOrderType());
        order.setPrice(executionPrice);
        order.setStatus("EXECUTED");

        orderRepository.save(order);

        // ✅ Save Transaction
        transactionService.createTransaction(
                user,
                request.getSymbol(),
                request.getQuantity(),
                executionPrice,
                TransactionType.BUY
        );
    return request;}
    public void updatePortfolio(UserEntity user,
                                String stock,
                                int qty,
                                double price) {

        PortfolioEntity portfolio = portfolioRepository
                .findByUserAndStockName(user, stock)
                .orElse(null);

        if (portfolio != null) {

            int totalQty = portfolio.getQuantity() + qty;

            double newAvg =
                    ((portfolio.getAveragePrice() * portfolio.getQuantity())
                            + (price * qty)) / totalQty;

            portfolio.setQuantity(totalQty);
            portfolio.setAveragePrice((float) newAvg);
            portfolio.setInvestment((float) (totalQty * newAvg));

        } else {

            PortfolioEntity newPortfolio = new PortfolioEntity();

            newPortfolio.setUser(user);
            newPortfolio.setStockName(stock);
            newPortfolio.setQuantity(qty);
            newPortfolio.setAveragePrice((float) price);
            newPortfolio.setInvestment((float) (qty * price));

            portfolioRepository.save(newPortfolio);
        }
    }
}