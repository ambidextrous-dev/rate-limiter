package com.ambidextrous.ratelimiter.controller;

import com.ambidextrous.ratelimiter.service.BitcoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BitcoinController {
    BitcoinService bitcoinService;

    @Autowired
    public BitcoinController(BitcoinService bitcoinService) {
        this.bitcoinService = bitcoinService;
    }

    //Rate Limited
    @GetMapping("/bitcoin")
    public String getBitcoinPrice() {
        return bitcoinService.getCurrentPrice();
    }

}
