package com.cryptobot.CryptoInfoBot.services;

import com.cryptobot.CryptoInfoBot.repository.CryptoRepository;
import com.cryptobot.CryptoInfoBot.repository.OrdersRepository;
import com.cryptobot.CryptoInfoBot.repository.PeriodRepository;
import com.cryptobot.CryptoInfoBot.singleton.Period;
import com.cryptobot.CryptoInfoBot.singleton.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
public class TrendBasedLongService {

    final private CryptoRepository cryptoRepository;
    final private BinanceApiService binanceApiService;
    final private OrdersRepository ordersRepository;
    final private PeriodRepository periodsRepository;
    @Autowired
    private TrendBasedLongService(CryptoRepository cryptoRepository, BinanceApiService binanceApiService, OrdersRepository ordersRepository, PeriodRepository periodsRepository) {
        this.cryptoRepository = cryptoRepository;
        this.binanceApiService = binanceApiService;
        this.ordersRepository = ordersRepository;
        this.periodsRepository = periodsRepository;
    }

    public void calculateRSI() {

            float differenceInPrice=0;
            float lastPrice=0;
            float lossSum=0;
            float gainSum=0;
            float averageLoss=0;
            float averageGain=0;
            for (int i = 0; i < cryptoRepository.findAll().size(); i++) {
                if (i>1) {
                    differenceInPrice=lastPrice-cryptoRepository.findAll().get(i).getPrice();
                    if (differenceInPrice>0){
                        gainSum+=differenceInPrice;
                    }
                    else{
                        lossSum+=differenceInPrice;
                    }
                }
                lastPrice = cryptoRepository.findAll().get(i).getPrice();
            }
            averageGain=gainSum/24;
            averageLoss=(-1)*lossSum/24;
            float RSI=100-100/(1+averageGain/averageLoss);
            Period period=new Period();
            period.setDate(LocalDate.now());
            period.setPeriodLengthInHours(24);
            period.setRsi(RSI);
            period.setSMA(calculateSMA());
            periodsRepository.save(period);
            cryptoRepository.deleteAll();

    }
    private float calculateSMA(){
        return (float)cryptoRepository.totalPrice()/24;
    }

    public void makeAnOrder() {
        if (periodsRepository.findTopByOrderByPeriodIdDesc().getRsi()>=75 && binanceApiService.getUserAsset("USDT") > 1) {
            binanceApiService.makeAnOrder("BUY");
            Transaction transaction = ordersRepository.findTopByOrderByOrderIdDesc();
            transaction.setPosition("LONG");
            ordersRepository.save(transaction);
        }
        else if(periodsRepository.findAll().size()==30){
            if (periodsRepository.findTopByOrderByPeriodIdDesc().getSMA()>periodsRepository.findTopByOrderByPeriodIdAsc().getSMA()){
                binanceApiService.makeAnOrder("BUY");
                Transaction transaction = ordersRepository.findTopByOrderByOrderIdDesc();
                transaction.setPosition("LONG");
                ordersRepository.save(transaction);
            }
        }
    }

    public void makeProfitOnLong(float profitPercentage) {
        float startingPrice = cryptoRepository.findTopByOrderByIdAsc().getPrice();
        float profit = (profitPercentage * startingPrice) + startingPrice;
        if (cryptoRepository.findTopByOrderByIdDesc().getPrice() >= profit && ordersRepository.findTopByOrderByOrderIdDesc().getPosition().equals("LONG")) {
            binanceApiService.makeAnOrder("SELL");
        }
    }

    public void stopLosesOnLong(float maxLossPercentage) {
        float startingPrice = cryptoRepository.findTopByOrderByIdAsc().getPrice();
        float maxLoss = startingPrice - (maxLossPercentage * startingPrice);
        if (cryptoRepository.findTopByOrderByIdDesc().getPrice() <= maxLoss && ordersRepository.findTopByOrderByOrderIdDesc().getPosition().equals("LONG")) {
            binanceApiService.makeAnOrder("SELL");
        }
    }


}
