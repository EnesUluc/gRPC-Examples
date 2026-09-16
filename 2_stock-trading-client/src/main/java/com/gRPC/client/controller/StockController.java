package com.gRPC.client.controller;

import com.gRPC.StockResponse;
import com.gRPC.StockResponseList;
import com.gRPC.client.entity.StockResponseDto;
import com.gRPC.client.service.StockClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StockController {
    private StockClientService clientService;


    public StockController(StockClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/stock")
    public StockResponseDto getStockPrice(@RequestParam("name") String name){
        return clientService.getStockPrice(name);
    }

    @GetMapping("/stocks")
    public List<StockResponseDto> getAllStockPrice(){
        return clientService.getAllStockPrice();
    }

    @GetMapping("/stock/update")
    public StockResponseDto updateStockPrice(@RequestParam("name") String name, @RequestParam("price")Double price){
        return clientService.updateStockPrice(name, price);
    }

}
