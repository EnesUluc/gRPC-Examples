package com.gRPC.stock_trading_server.service;

import com.gRPC.OrderSummary;
import com.gRPC.StockOrder;
import com.gRPC.StockTradingServiceGrpc;
import com.gRPC.stock_trading_server.entity.Stock;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;
import com.gRPC.stock_trading_server.repository.StockRepository;

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
    public StreamObserver<StockOrder> bulkStockOrder(StreamObserver<OrderSummary> responseObserver) {

        // Anonymous inner class observer to handle each inbound stream item (StockOrder) from the client
        return new StreamObserver<>() {
            private int totalOrders = 0;
            private double totalAmount = 0;
            private int successCount = 0;

            @Override
            public void onNext(StockOrder stockOrder) {
                totalOrders++;

                Stock stock = stockRepository.findByStockName(stockOrder.getStockName());

                // If the stock is not found, stop this request, then it will continue with the other streaming request
                if(stock == null){
                    System.out.println("Stock not found");
                    return;
                }

                totalAmount += stock.getPrice() * stockOrder.getQuantity();
                successCount++;
                System.out.println("Received order: " + stockOrder);

            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Server unable to process the request");
            }

            @Override
            public void onCompleted() {
                OrderSummary summary = OrderSummary.newBuilder()
                        .setTotalOrders(totalOrders)
                        .setSuccessCount(successCount)
                        .setTotalPrice(totalAmount)
                        .build();

                // Send the summary response back to the client and close the server stream
                responseObserver.onNext(summary);
                responseObserver.onCompleted();
            }
        };
    }
}
