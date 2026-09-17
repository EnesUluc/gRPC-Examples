package com.gRPC.client.service;

import com.gRPC.*;
import com.gRPC.StockRequest;
import com.gRPC.StockResponse;
import com.gRPC.StockTradingServiceGrpc;
import com.gRPC.client.entity.StockResponseDto;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Service;

import java.util.List;

// In the client side, we just need to '@Service' annotation.
// But in the server side, we need to use '@GrpcService' annotation.
@Service
public class StockClientService {
    private final StockTradingServiceGrpc.StockTradingServiceStub stub;


    public StockClientService(GrpcChannelFactory channelFactory) {
        Channel channel = channelFactory.createChannel("stockService");
        // Blocking stub is the best option for unary communication (request <-> response)
        this.stub = StockTradingServiceGrpc.newStub(channel);
    }

    public void subscribeStockPrice(String name) {
        StockRequest request = StockRequest.newBuilder()
                .setStockName(name)
                .build();

        stub.subscribeStockPrice(request, new StreamObserver<StockResponse>() {

            @Override
            public void onNext(StockResponse stockResponseDto) {
                System.out.println(stockResponseDto);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stock Price Stream subscribed.");
            }
        });
    }

}
