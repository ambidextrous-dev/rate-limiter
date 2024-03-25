package com.ambidextrous.ratelimiter.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BitcoinService {
    RestTemplate restTemplate;
    private final String bitcoinApiUrl;

    @Autowired
    public BitcoinService(RestTemplate restTemplate, @Value("${bitcoin.api.url}") String bitcoinApiUrl) {
        this.restTemplate = restTemplate;
        this.bitcoinApiUrl = bitcoinApiUrl;
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
