package com.gRPC.stock_trading_server.service;

import com.gRPC.*;
import com.gRPC.stock_trading_server.entity.Stock;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;
import com.gRPC.stock_trading_server.repository.StockRepository;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@GrpcService
public class StockTradingServiceImpl extends StockTradingServiceGrpc.StockTradingServiceImplBase {

    private final StockRepository stockRepository;

    StockTradingServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public void getStockPrice(StockRequest request, StreamObserver<StockResponse> responseObserver) {
        // stockId -> DB -> map response -> return
        String stockName = request.getStockName();
        Stock stock = stockRepository.findByStockName(stockName);
        responseObserver.onNext(toDto(stock));
        responseObserver.onCompleted();
    }

    @Override
    public void getAllStockPrice(Empty request, StreamObserver<StockResponseList> responseObserver) {
        // 1. Fetch all the records
        List<Stock> stocks = stockRepository.findAll();

        // 2. Map entities to dtos
        List<StockResponse> stockResponses = stocks.stream().map(this::toDto).toList();

        // 3. Use addAllStock(...) to add repeated field
        StockResponseList stockResponseList = StockResponseList.newBuilder().addAllStock(stockResponses).build();

        // Send the response to the client and close the streaming
        responseObserver.onNext(stockResponseList);
        responseObserver.onCompleted();
    }

    @Override
    public void updateStockPrice(StockUpdateRequest request, StreamObserver<StockResponse> responseObserver) {
        String stockName = request.getStockName();
        Stock stock = stockRepository.findByStockName(stockName);
        stock.setPrice(request.getPrice());
        Stock updatedStock = stockRepository.save(stock);
        responseObserver.onNext(toDto(updatedStock));
        responseObserver.onCompleted();

    }

    // In the real world implementation, a for loop is not used. This is just an example to show the logic behind the server streaming.
    // Instead of this, you need to use a message query (publish/subscribe architecture) to retrieve data instantly.
    // For example this server is listening en EventPublisher and the method signed @EventListener listens a message query like Kafka
    @Override
    public void subscribeStockPrice(StockRequest request, StreamObserver<StockResponse> responseObserver) {
        String stockName = request.getStockName();

        for (int i = 0; i < 10; i++) {
            StockResponse stockResponse = StockResponse.newBuilder()
                    .setStockName(stockName)
                    .setPrice(new Random().nextDouble(200))
                    .setTimestamp(Instant.now().toString())
                    .build();

            responseObserver.onNext(stockResponse);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                responseObserver.onError(e);
            }
        }
        responseObserver.onCompleted();
    }

    private StockResponse toDto(Stock stock) {
        return StockResponse.newBuilder()
                .setStockName(stock.getStockName())
                .setPrice(stock.getPrice())
                .setTimestamp(stock.getLastUpdated().toString())
                .build();
    }
}
