package com.cryptobot.CryptoInfoBot.singleton;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.HashSet;

@Entity
public class CryptoCoin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Getter
    @Setter
    private LocalDateTime date;
    @Getter
    @Setter
    private float price;
    @Setter
    @Getter
    private String symbol;
    
    public  CryptoCoin(){}
    public CryptoCoin(String symbol,float price, LocalDateTime date){
        this.symbol=symbol;
        this.price=price;
        this.date=date;
    }

    @Override
    public String toString() {
        return String.valueOf(price);
    }
}
