package com.example.stockify.services;

import com.example.stockify.dto.BuyOrderRequestDTO;
import com.example.stockify.dto.OrderListDTO;
import com.example.stockify.dto.StockResponseDTO;
import com.example.stockify.entities.*;
import com.example.stockify.enums.TransactionType;
import com.example.stockify.exception.InsufficientBalanceException;
import com.example.stockify.exception.ResourceNotFoundException;
import com.example.stockify.exception.StockDataNotFoundException;
import com.example.stockify.exception.WalletNotFoundException;
import com.example.stockify.repositories.*;
import io.jsonwebtoken.RequiredTypeException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public BuyOrderRequestDTO sellStock(String username , BuyOrderRequestDTO request){
        if(request.getQuantity() <= 0 )
            throw new RuntimeException(("Quantity must be greater than 0 "));

        if("LIMIT".equalsIgnoreCase(request.getOrderType()) && request.getPrice() <= 0)
            throw new RuntimeException("Price must be greater than 0 for LIMIT orders");

        UserEntity user = userRepository
                .findById(username).orElseThrow(() -> new RuntimeException("user not found"));
        WalletEntity wallet = walletRepository.findById(username)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
        PortfolioEntity portfolio = portfolioRepository.findByUserAndStockName(user, request.getSymbol())
                .orElse(null);
        if(portfolio == null)
            throw new RuntimeException("No stocks found");
        StockResponseDTO stock = stockService.getStock(request.getSymbol(), "1d", "1m");

        if(stock ==null || stock.getMeta() ==null)
            throw new RequiredTypeException("Stock data Unavailable");

        double currentPrice = stock.getMeta().getPrice();
        double executionPrice ;
        String orderStatus;

        boolean marketOpen = marketTimeService.isMarketOpen();

        if("MARKET".equalsIgnoreCase(request.getOrderType())){
            if(marketOpen)
//            if (true)
                orderStatus = "EXECUTED";
            else
                orderStatus = "PENDING";
            executionPrice = currentPrice;
        }
        else if("LIMIT".equalsIgnoreCase(request.getOrderType())){
            if(!marketOpen || currentPrice < request.getPrice()){
                orderStatus = "PENDING";
                executionPrice = request.getPrice();
            }
            else {
                orderStatus ="EXECUTED";
                executionPrice = currentPrice;
            }
        }
        else{
            throw new RequiredTypeException("Invalid order type");
        }

        double totalAmount = executionPrice*request.getQuantity();
        if(portfolio.getAvailableQuantity() < request.getQuantity())
            throw new InsufficientBalanceException("Less quantity available");


        if ("EXECUTED".equals(orderStatus)) {
            wallet.setAmount(wallet.getAmount() + totalAmount);
            walletRepository.save(wallet);
            updatePortfolio(user, request.getSymbol(), request.getQuantity(), executionPrice , "SELL");

            transactionService.createTransaction(
                    user,
                    request.getSymbol(),
                    request.getQuantity(),
                    executionPrice,
                    TransactionType.SELL
            );
        }
        else if ("PENDING".equals(orderStatus)) {
            portfolio.setReservedQuantity(portfolio.getReservedQuantity() + request.getQuantity());
            portfolioRepository.save(portfolio);
        }
        OrderEntity order = new OrderEntity();
        order.setUsers(user);
        order.setSymbol(request.getSymbol());
        order.setQuantity(request.getQuantity());
        order.setOrderType(request.getOrderType());
        order.setPrice(executionPrice);
        order.setStatus(orderStatus);
        order.setType(TransactionType.SELL);

        OrderEntity savedOrder = orderRepository.save(order);

        BuyOrderRequestDTO dto = modelMapper.map(savedOrder, BuyOrderRequestDTO.class);
        dto.setStatus(savedOrder.getStatus());
        return dto;
    }

    @Transactional
    public BuyOrderRequestDTO buyStock(String username, BuyOrderRequestDTO request) {
        if (request.getQuantity() <= 0)
            throw new RuntimeException("Quantity must be greater than 0");
        if ("LIMIT".equalsIgnoreCase(request.getOrderType()) && request.getPrice() <= 0)
            throw new RuntimeException("Price must be greater than 0 for LIMIT orders");
        UserEntity user = userRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        WalletEntity wallet = walletRepository.findById(username)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
        StockResponseDTO stock = stockService.getStock(request.getSymbol(), "1d", "1m");
        if (stock == null || stock.getMeta() == null)
            throw new StockDataNotFoundException("Stock data unavailable");
        double currentPrice = stock.getMeta().getPrice();
        double executionPrice;
        String orderStatus;
        boolean marketOpen = marketTimeService.isMarketOpen();
        if ("MARKET".equalsIgnoreCase(request.getOrderType())) {
            if (marketOpen) orderStatus = "EXECUTED";
            else orderStatus = "PENDING";
            executionPrice = currentPrice;
        }
        else if ("LIMIT".equalsIgnoreCase(request.getOrderType())) {
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
            updatePortfolio(user, request.getSymbol(), request.getQuantity(), executionPrice , "BUY");

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
        order.setType(TransactionType.BUY);


        OrderEntity savedOrder = orderRepository.save(order);

        BuyOrderRequestDTO dto = modelMapper.map(savedOrder, BuyOrderRequestDTO.class);
        dto.setStatus(savedOrder.getStatus());

        return dto;
    }

    public void updatePortfolio(UserEntity user, String stock, int qty, double price , String type) {
        PortfolioEntity portfolio = portfolioRepository.findByUserAndStockName(user, stock)
                .orElse(null);
        if("SELL".equalsIgnoreCase(type)){
            if(portfolio == null) throw new RuntimeException("Portfolio not found");
            int totalQty = portfolio.getQuantity() - qty;
            if (totalQty < 0)
                throw new RuntimeException("Insufficient portfolio quantity");
            if(portfolio.getReservedQuantity() != 0)
                portfolio.setReservedQuantity(portfolio.getReservedQuantity() - qty);
            portfolio.setQuantity(totalQty);
            if(totalQty == 0) {
                portfolioRepository.delete(portfolio);
            } else {
                portfolioRepository.save(portfolio);
            }
        }
        else {
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

    public OrderListDTO getOrders(String username, String orderType) {
        List<OrderEntity> orders;
        if(orderType.equalsIgnoreCase("ALL")){
            orders = orderRepository.findByUsersUsername(username);

        }
        else if(orderType.equalsIgnoreCase("EXECUTED")){
            orders = orderRepository.findByUsersUsernameAndStatus(username,"EXECUTED");
        }
        else if(orderType.equalsIgnoreCase("PENDING")){
            orders = orderRepository.findByUsersUsernameAndStatus(username,"PENDING");
        }
        else{
            orders = orderRepository.findByUsersUsernameAndStatus(username,"CANCELLED");
        }
        List<BuyOrderRequestDTO> ordersDTO =  orders.stream()
                .map(order -> modelMapper.map(order, BuyOrderRequestDTO.class))
                .toList();

        long totalOrders = orderRepository.countByUsers_Username(username);
        long pendingOrders = orderRepository.countByUsers_UsernameAndStatus(username , "PENDING");
        long executedOrders = orderRepository.countByUsers_UsernameAndStatus(username, "EXECUTED");

        return OrderListDTO.builder()
                .orders(ordersDTO)
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .executedOrders(executedOrders)
                .build();


    }

    @Transactional
    public void deleteStocks(String username, Long id) {
        orderRepository.findById(id).orElseThrow(() ->new ResourceNotFoundException("Orders not found"));
        orderRepository.deleteByUsersUsernameAndId(username , id);
    }
}