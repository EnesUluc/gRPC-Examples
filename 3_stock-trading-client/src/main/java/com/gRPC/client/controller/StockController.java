package com.gRPC.client.controller;

import com.gRPC.client.service.StockClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class StockController {
    private StockClientService clientService;


    public StockController(StockClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/stocks")
    public void subscribeStockPrices(){
        clientService.subscribeStockPrice("TABLE");
    }


}
