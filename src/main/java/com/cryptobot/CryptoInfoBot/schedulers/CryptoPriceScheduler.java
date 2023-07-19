package com.cryptobot.CryptoInfoBot.schedulers;

import com.cryptobot.CryptoInfoBot.services.BinanceApiService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class CryptoPriceScheduler {

    private final BinanceApiService binanceApiService;

    public CryptoPriceScheduler(BinanceApiService binanceApiService) {
        this.binanceApiService = binanceApiService;
    }

    @Scheduled(fixedRate = 60000)
    public void getETHLastPrice() {
        binanceApiService.getETHLastPrice();
    }
}
