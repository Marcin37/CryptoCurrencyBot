package com.cryptobot.CryptoInfoBot.controllers;


import com.cryptobot.CryptoInfoBot.schedulers.ScalpBotScheduler;
import com.cryptobot.CryptoInfoBot.schedulers.TrendBotLongScheduler;
import com.cryptobot.CryptoInfoBot.schedulers.TrendBotShortScheduler;
import com.cryptobot.CryptoInfoBot.services.BinanceApiService;
import org.springframework.web.bind.annotation.*;

@RestController
public class BotController {

    private final BinanceApiService binanceApiService;
    private final TrendBotLongScheduler trendBotLongScheduler;
    private final ScalpBotScheduler scalpBotScheduler;
    private final TrendBotShortScheduler trendBotShortScheduler;

    public BotController(BinanceApiService binanceApiService, TrendBotLongScheduler trendBotLongScheduler, ScalpBotScheduler scalpBotScheduler, TrendBotShortScheduler trendBotShortScheduler) {
        this.binanceApiService = binanceApiService;
        this.trendBotLongScheduler = trendBotLongScheduler;
        this.scalpBotScheduler = scalpBotScheduler;
        this.trendBotShortScheduler = trendBotShortScheduler;
    }


    @PostMapping("/startScalping")
    @ResponseBody
    public void startScalping(@RequestParam float startingPrice, @RequestParam double maxLossPercentage) {
        scalpBotScheduler.setShouldStartTheScalpingBot(true, startingPrice, maxLossPercentage);
    }

    @PostMapping("/stopScalping")
    public void stopScalping() {
        scalpBotScheduler.setShouldStopTheScalpingBot();
    }

    @PostMapping("/startTrendLongBot")
    @ResponseBody
    public void startTrendLongBot(@RequestParam float profitPercentage, @RequestParam float maxLossPercentage) {
        trendBotLongScheduler.setShouldStartTrendLongBot(true, profitPercentage, maxLossPercentage);
    }

    @PostMapping("/stopTrendLongBot")
    public void stopTrendBot() {
        trendBotLongScheduler.setShouldStopTrendLongBot();
    }

    @PostMapping("/startTrendShortBot")
    @ResponseBody
    public void startTrendShortBot(@RequestParam float profitPercentage, @RequestParam float maxLossPercentage) {
        trendBotShortScheduler.setShouldStartTrendShortBot(true, profitPercentage, maxLossPercentage);
    }

    @PostMapping("/stopTrendShortBot")
    public void stopTrendShortBot() {
        trendBotShortScheduler.setShouldStopTrendShortBot();
    }

    @GetMapping("/orderbuy")
    public Object makeAnOrderBuy() {
        return binanceApiService.makeAnOrder("BUY");
    }

    @GetMapping("/ordersell")
    public Object makeAnOrderSell() {
        return binanceApiService.makeAnOrder("SELL");
    }

    @GetMapping("/userAsset")
    public Object getUserAsset() {
        return binanceApiService.getUserAsset("ETH");
    }

    @GetMapping("/getAccountInfo")
    public Object getAccountInfo() {
        return binanceApiService.getAccountInfo();
    }


}
