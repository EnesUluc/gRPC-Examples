package com.gRPC.stock_trading_server.service;

import com.gRPC.*;
import com.gRPC.stock_trading_server.entity.Stock;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;
import com.gRPC.stock_trading_server.repository.StockRepository;

import java.util.List;

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

    private StockResponse toDto(Stock stock) {
        return StockResponse.newBuilder()
                .setStockName(stock.getStockName())
                .setPrice(stock.getPrice())
                .setTimestamp(stock.getLastUpdated().toString())
                .build();
    }
}
