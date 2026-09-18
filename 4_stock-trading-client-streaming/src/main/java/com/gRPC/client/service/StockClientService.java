package com.gRPC.client.service;

import com.gRPC.OrderSummary;
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

    /**
     * Streams a list of stock order requests to the server.
     *
     * @param requestDtos List of order request DTOs to be transmitted
     */
    public void placeBulkOrder(List<StockRequestDto> requestDtos){
        // Observer to handle the single summary response (OrderSummary) and potential errors from the server
        StreamObserver<OrderSummary> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(OrderSummary orderSummary) {
                System.out.println("Total Orders: " + orderSummary.getTotalOrders());
                System.out.println("Successful Orders: " + orderSummary.getSuccessCount());
                System.out.println("Total Amount: " + orderSummary.getTotalPrice());
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Order Summary Received error from Server: " + t.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed, server is done sending summary!");
            }
        };

        // Initiate the streaming RPC and obtain the request observer for pushing stream messages
        StreamObserver<StockOrder> requestObserver = stub.bulkStockOrder(responseObserver);

        try{
            // Stream payload items using a clean for-each loop instead ıf Stream.map()
            for(StockRequestDto requestDto:  requestDtos){
                requestObserver.onNext(convertToStockOrder(requestDto));
            }
            // Notify the server that client streaming is completed
            requestObserver.onCompleted();
        }catch (Exception e){
            requestObserver.onError(e);
        }
    }

    private StockOrder convertToStockOrder(StockRequestDto requestDto){
        return StockOrder.newBuilder()
                .setOrderType(requestDto.type())
                .setStockName(requestDto.name())
                .setQuantity(requestDto.quantity())
                .build();
    }

}
