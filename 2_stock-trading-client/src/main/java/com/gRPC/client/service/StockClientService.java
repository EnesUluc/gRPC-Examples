package com.gRPC.client.service;

import com.gRPC.*;
import com.gRPC.Empty;
import com.gRPC.StockRequest;
import com.gRPC.StockResponse;
import com.gRPC.StockResponseList;
import com.gRPC.StockTradingServiceGrpc;
import com.gRPC.StockUpdateRequest;
import com.gRPC.client.entity.StockResponseDto;
import io.grpc.Channel;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Service;

import java.util.List;

// In the client side, we just need to '@Service' annotation.
// But in the server side, we need to use '@GrpcService' annotation.
@Service
public class StockClientService {
    private final StockTradingServiceGrpc.StockTradingServiceBlockingStub stub;


    public StockClientService(GrpcChannelFactory channelFactory) {
        Channel channel = channelFactory.createChannel("stockService");
        // Blocking stub is the best option for unary communication (request <-> response)
        this.stub = StockTradingServiceGrpc.newBlockingStub(channel);
    }

    // StockResponse getStockPrice(StockRequest)
    public StockResponseDto getStockPrice(String stockName){
        StockRequest request = StockRequest.newBuilder().setStockName(stockName).build();
        StockResponse response = stub.getStockPrice(request);
        return new StockResponseDto(response.getStockName(), response.getPrice(), response.getTimestamp());
    }

    public List<StockResponseDto> getAllStockPrice() {
        Empty request = Empty.newBuilder().build();
        StockResponseList response = stub.getAllStockPrice(request);

        return response.getStockList().stream().map(r -> new StockResponseDto(r.getStockName(), r.getPrice(), r.getTimestamp())).toList();
    }

    public StockResponseDto updateStockPrice(String stockName, Double price){
        StockUpdateRequest request = StockUpdateRequest.newBuilder().setStockName(stockName).setPrice(price).build();
        StockResponse response = stub.updateStockPrice(request);
        return new StockResponseDto(response.getStockName(), response.getPrice(), response.getTimestamp());
    }

}
