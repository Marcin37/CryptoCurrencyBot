package com.cryptobot.CryptoInfoBot.services;

import com.cryptobot.CryptoInfoBot.repository.CryptoRepository;
import com.cryptobot.CryptoInfoBot.repository.OrdersRepository;
import com.cryptobot.CryptoInfoBot.singleton.CryptoCoin;
import com.cryptobot.CryptoInfoBot.singleton.Period;
import com.cryptobot.CryptoInfoBot.singleton.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Expression;
import java.time.LocalDate;

@EnableScheduling
@Service
public class ScalpingService {

    final private CryptoRepository cryptoRepository;
    final private BinanceApiService binanceApiService;
    final private OrdersRepository ordersRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ScalpingService(CryptoRepository cryptoRepository, BinanceApiService binanceApiService, OrdersRepository ordersRepository) {
        this.cryptoRepository = cryptoRepository;
        this.binanceApiService = binanceApiService;
        this.ordersRepository = ordersRepository;
    }



    public void makeAnOrder(float startingPrice, double maxLossPercentage) {
        Transaction lastTransaction = ordersRepository.findTopByOrderByOrderIdDesc();
        if (lastTransaction != null) {
            if (lastTransaction.getSide().equals("SELL")) {
                scalpBuy();
            } else {
                startingPrice = lastTransaction.getPriceInUSDT();
                scalpSell(startingPrice, maxLossPercentage);
            }
        } else {
            if(binanceApiService.getUserAsset("ETH")>0.1){
                scalpSell(startingPrice, maxLossPercentage);
            }
            else{
                scalpBuy();
            }

        }
    }

    private void scalpSell(float startingPrice, double maxLossPercentage) {
        float currentPrice = cryptoRepository.findTopByOrderByIdDesc().getPrice();
        if (startingPrice - (startingPrice * maxLossPercentage) >= currentPrice) {
            binanceApiService.makeAnOrder("SELL");
        }
        if (startingPrice + 1 < currentPrice) {
            binanceApiService.makeAnOrder("SELL");
        }

    }

    private void scalpBuy() {
        float currentPrice = cryptoRepository.findTopByOrderByIdDesc().getPrice();
        Transaction lastTransaction = ordersRepository.findTopByOrderByOrderIdDesc();
        if (lastTransaction.getSide().equals("SELL")) {
            float soldForPrice = lastTransaction.getPriceInUSDT();
            if (soldForPrice - 1 > currentPrice) {
                binanceApiService.makeAnOrder("BUY");
            }
        }
    }
}
