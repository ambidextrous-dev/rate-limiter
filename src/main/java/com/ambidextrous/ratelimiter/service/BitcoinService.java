package com.ambidextrous.ratelimiter.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BitcoinService {
    RestTemplate restTemplate;
    private final String bitcoinApiUrl = "https://api.coindesk.com/v1/bpi/currentprice.json";

    @Autowired
    public BitcoinService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getCurrentPrice() {
        JSONObject bitcoinResponseObj = new JSONObject(restTemplate.getForObject(bitcoinApiUrl, String.class));
        JSONObject responseBPI = bitcoinResponseObj.getJSONObject("bpi");
        JSONObject result = new JSONObject();

        for (String currencyCode : responseBPI.keySet()) {
            result.put("price_" + currencyCode, responseBPI.getJSONObject(currencyCode).getString("rate"));
        }

        return result.toString();
    }

}
