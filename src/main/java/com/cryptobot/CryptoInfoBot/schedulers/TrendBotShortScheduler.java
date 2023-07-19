package com.cryptobot.CryptoInfoBot.schedulers;

import com.cryptobot.CryptoInfoBot.repository.OrdersRepository;
import com.cryptobot.CryptoInfoBot.services.TrendBasedShortService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class TrendBotShortScheduler {


    final private TrendBasedShortService trendBasedShortService;
    final private OrdersRepository ordersRepository;
    private boolean shouldStartTrendShortBot;
    private float profitPercentage;
    private float maxLossPercentage;

    public TrendBotShortScheduler(TrendBasedShortService trendBasedShortService, OrdersRepository ordersRepository) {
        this.trendBasedShortService = trendBasedShortService;
        this.ordersRepository = ordersRepository;
    }

    public void setShouldStartTrendShortBot(boolean shouldStartTrendShortBot,float profitPercentage,float maxLossPercentage) {
        this.shouldStartTrendShortBot = shouldStartTrendShortBot;
        this.profitPercentage=profitPercentage;
        this.maxLossPercentage=maxLossPercentage;
    }
    public void setShouldStopTrendShortBot() {
        this.shouldStartTrendShortBot = false;
    }

    @Scheduled(cron = "0 0 */1 * * *")
    public void startTheTrendingShortBot() {
        if (shouldStartTrendShortBot)
            trendBasedShortService.makeAnOrder();
    }

    @Scheduled(cron = "0 */1 * * * * ")
    public void makeProfitOnShort() {
        if (shouldStartTrendShortBot) {
            if (ordersRepository.findAll().size() > 0) {
                trendBasedShortService.makeProfitOnShort(profitPercentage);
            }
        }
    }

    @Scheduled(cron = "0 */1 * * * * ")
    public void stopLosesOnShort() {
        if (shouldStartTrendShortBot) {
            if (ordersRepository.findAll().size() > 0) {
                trendBasedShortService.stopLosesOnShort(maxLossPercentage);
            }
        }
    }


}
