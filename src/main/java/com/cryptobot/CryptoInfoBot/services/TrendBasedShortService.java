package com.cryptobot.CryptoInfoBot.services;

import com.cryptobot.CryptoInfoBot.repository.CryptoRepository;
import com.cryptobot.CryptoInfoBot.repository.OrdersRepository;
import com.cryptobot.CryptoInfoBot.singleton.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TrendBasedShortService {

    final private CryptoRepository cryptoRepository;
    final private BinanceApiService binanceApiService;
    final private OrdersRepository ordersRepository;

    @Autowired
    private TrendBasedShortService(CryptoRepository cryptoRepository, BinanceApiService binanceApiService, OrdersRepository ordersRepository) {
        this.cryptoRepository = cryptoRepository;
        this.binanceApiService = binanceApiService;
        this.ordersRepository = ordersRepository;
    }

    public void makeAnOrder() {
        float staringPrice = cryptoRepository.findTopByOrderByIdAsc().getPrice();
        Long totalPrice = cryptoRepository.totalPrice();
        if (totalPrice / cryptoRepository.findAll().size() < staringPrice && binanceApiService.getUserAsset("ETH") > 0.1) {
            binanceApiService.makeAnOrder("SELL");
            Transaction transaction = ordersRepository.findTopByOrderByOrderIdDesc();
            transaction.setPosition("SHORT");
            ordersRepository.save(transaction);
        }
    }


    public void makeProfitOnShort(float profitPercentage) {
        float startingPrice = cryptoRepository.findTopByOrderByIdAsc().getPrice();
        float profit = startingPrice - (profitPercentage * startingPrice);
        if (cryptoRepository.findTopByOrderByIdDesc().getPrice() <= startingPrice && ordersRepository.findTopByOrderByOrderIdDesc().getPosition().equals("SHORT")) {
            binanceApiService.makeAnOrder("BUY");
        }
    }

    public void stopLosesOnShort(float maxLossPercentage) {
        float startingPrice = cryptoRepository.findTopByOrderByIdAsc().getPrice();
        float profit = (maxLossPercentage * startingPrice) + startingPrice;
        if (cryptoRepository.findTopByOrderByIdDesc().getPrice() >= profit && ordersRepository.findTopByOrderByOrderIdDesc().getPosition().equals("SHORT")) {
            binanceApiService.makeAnOrder("BUY");
        }
    }

}

