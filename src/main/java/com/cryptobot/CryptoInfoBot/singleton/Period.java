package com.cryptobot.CryptoInfoBot.singleton;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;


@Entity
public class Period {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long periodId;
    @Getter
    @Setter
    private int periodLengthInHours;
    @Getter
    @Setter
    private float SMA;
    @Getter
    @Setter
    private float Rsi;
    @Getter
    @Setter
    private LocalDate date;
    @OneToMany
    private List<CryptoCoin> cryptoCoins;
    public Period(){

    }

    public Period(long periodId, int periodLengthInHours, float SMA, float rsi, LocalDate date) {
        this.periodId = periodId;
        this.periodLengthInHours = periodLengthInHours;
        this.SMA = SMA;
        Rsi = rsi;
        this.date = date;
    }
}
