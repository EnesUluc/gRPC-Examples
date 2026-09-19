package com.gRPC.client.controller;

import com.gRPC.client.entity.StockRequestDto;
import com.gRPC.client.service.StockClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class StockController {
    private StockClientService clientService;


    public StockController(StockClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/bulk")
    public void bulkOrder(@RequestBody List<StockRequestDto> requestDtos){
        clientService.startLiveTracking(requestDtos);
    }


}
