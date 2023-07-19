package com.cryptobot.CryptoInfoBot.schedulers;

import com.cryptobot.CryptoInfoBot.services.ScalpingService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableScheduling
@Service
public class ScalpBotScheduler {

    private final ScalpingService scalpingService;
    private boolean shouldStartTheScalpingBot;
    private float startingPrice;
    private double maxLossPercentage;

    public ScalpBotScheduler(ScalpingService scalpingService) {
        this.scalpingService = scalpingService;

    }

    public void setShouldStartTheScalpingBot(boolean shouldStartTheScalpingBot, float startingPrice, double maxLossPercentage) {
        this.shouldStartTheScalpingBot = shouldStartTheScalpingBot;
        this.startingPrice=startingPrice;
        this.maxLossPercentage=maxLossPercentage;
    }

    public void setShouldStopTheScalpingBot() {
        this.shouldStartTheScalpingBot = false;
    }

    @Scheduled(cron = "0 */1 * * * * ")
    public void startTheScalpingBot() {
        if (shouldStartTheScalpingBot)
            scalpingService.makeAnOrder(startingPrice, maxLossPercentage);
    }
}
