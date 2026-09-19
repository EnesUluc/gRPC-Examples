package com.gRPC.client.service;

import com.gRPC.OrderStatus;
import com.gRPC.StockOrder;
import com.gRPC.StockTradingServiceGrpc;
import com.gRPC.client.entity.StockRequestDto;
import io.grpc.Channel;
import io.grpc.stub.StreamObserver;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockClientService {
    private final StockTradingServiceGrpc.StockTradingServiceStub stub;

    public StockClientService(GrpcChannelFactory channelFactory) {
        Channel channel = channelFactory.createChannel("stockService");
        // Client streaming operations require an asynchronous (non-blocking) stub.
        this.stub = StockTradingServiceGrpc.newStub(channel);
    }

    public void startLiveTracking(List<StockRequestDto> requestDtos) {
        StreamObserver<StockOrder> requestObserver = stub.liveTrading(new StreamObserver<OrderStatus>() {

            @Override
            public void onNext(OrderStatus orderStatus) {
                System.out.println("Server Response: " + orderStatus);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Server Error: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream Completed");
            }
        });

        // Sending multiple order request from client using a basic for loop
        for(StockRequestDto request : requestDtos) {
            StockOrder stock = StockOrder.newBuilder()
                    .setStockName(request.name())
                    .setOrderType(request.type())
                    .setQuantity(request.quantity())
                    .build();

            requestObserver.onNext(stock);
        }
    }

}
