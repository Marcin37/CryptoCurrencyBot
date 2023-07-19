package com.cryptobot.CryptoInfoBot.schedulers;

import com.cryptobot.CryptoInfoBot.repository.OrdersRepository;
import com.cryptobot.CryptoInfoBot.services.TrendBasedLongService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class TrendBotLongScheduler {


    final private TrendBasedLongService trendBasedLongService;
    final private OrdersRepository ordersRepository;
    private boolean shouldStartTrendLongBot;
    private float profitPercentage;
    private float maxLossPercentage;

    public TrendBotLongScheduler(TrendBasedLongService trendBasedLongService, OrdersRepository ordersRepository) {
        this.trendBasedLongService = trendBasedLongService;
        this.ordersRepository = ordersRepository;
    }

    public void setShouldStartTrendLongBot(boolean shouldStartTrendLongBot,float profitPercentage,float maxLossPercentage) {
        this.shouldStartTrendLongBot = shouldStartTrendLongBot;
        this.profitPercentage=profitPercentage;
        this.maxLossPercentage=maxLossPercentage;
    }

    public void setShouldStopTrendLongBot() {
        this.shouldStartTrendLongBot = false;
    }

    @Scheduled(fixedRate = 86400000,initialDelay = 6000)
    public void startTheTrendingLongBot() {
        if (shouldStartTrendLongBot && ordersRepository.findAll().size()==0)
            trendBasedLongService.makeAnOrder();
    }
    @Scheduled(fixedRate = 86300000,initialDelay = 86300000)
    public void calculatePeriod() {
        if (shouldStartTrendLongBot)
            trendBasedLongService.calculateRSI();
    }

    @Scheduled(fixedRate = 6000,initialDelay = 6000)
    public void makeProfitOnLong() {
        if (shouldStartTrendLongBot) {
            if (ordersRepository.findAll().size() > 0) {
                trendBasedLongService.makeProfitOnLong(profitPercentage);
            }
        }
    }

    @Scheduled(fixedRate = 6000,initialDelay = 6000)
    public void stopLosesOnLong() {
        if (shouldStartTrendLongBot) {
            if (ordersRepository.findAll().size() > 0) {
                trendBasedLongService.stopLosesOnLong(maxLossPercentage);
            }
        }
    }


}



