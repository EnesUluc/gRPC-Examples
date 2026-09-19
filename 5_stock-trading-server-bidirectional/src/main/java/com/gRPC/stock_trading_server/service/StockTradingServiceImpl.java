package com.gRPC.stock_trading_server.service;

import com.gRPC.OrderStatus;
import com.gRPC.StockOrder;
import com.gRPC.StockTradingServiceGrpc;
import com.gRPC.stock_trading_server.entity.Stock;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;
import com.gRPC.stock_trading_server.repository.StockRepository;

import java.time.Instant;

/**
 * Server-side service handling incoming gRPC requests and processing business logic.
 */
@GrpcService
public class StockTradingServiceImpl extends StockTradingServiceGrpc.StockTradingServiceImplBase {

    private final StockRepository stockRepository;

    StockTradingServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public StreamObserver<StockOrder> liveTrading(StreamObserver<OrderStatus> responseObserver) {
        return new StreamObserver<StockOrder>() {

            @Override
            public void onNext(StockOrder stockOrder) {
                System.out.println("Received order: " + stockOrder);
                String status = "EXECUTED";
                String message = "Order placed successfully";

                if(stockOrder.getQuantity() <= 0) {
                    status = "FAILED";
                    message = "Invalid quantity";
                }

                OrderStatus orderStatus = buildOrderStatus(stockOrder, status, message);
                if (orderStatus != null) {
                    responseObserver.onNext(orderStatus);
                }
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
    
    private OrderStatus buildOrderStatus(StockOrder stockOrder, String status, String message) {
        Stock stock = stockRepository.findByStockName(stockOrder.getStockName());
        if(stock == null) {
            return null;
        }
        return OrderStatus.newBuilder()
                .setOrderName(stock.getStockName())
                .setStatus(status)
                .setMessage(message)
                .setTimestamp(Instant.now().toString())
                .setTotalAmount(stockOrder.getQuantity() *  stock.getPrice())
                .build();
    }
}
