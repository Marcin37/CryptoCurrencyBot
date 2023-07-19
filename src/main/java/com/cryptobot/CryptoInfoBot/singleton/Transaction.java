package com.cryptobot.CryptoInfoBot.singleton;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Transaction {
    @Getter
    @Setter
    @Id
    private long orderId;
    @Getter
    @Setter
    private String symbol;
    @Getter
    @Setter
    private float soldQty;
    @Getter
    @Setter
    private float boughtQty;
    @Getter
    @Setter
    private String status;
    @Getter
    @Setter
    private String side;
    @Getter
    @Setter
    private String commisionAsset;
    @Getter
    @Setter
    private float priceInUSDT;
    @Setter
    @Getter
    private LocalDateTime dateTime;
    @Getter
    @Setter
    private String position;

    public Transaction() {
    }

    public Transaction(long orderId, String symbol, float soldQty, float boughtQty, String status, String side, String commission, LocalDateTime dateTime, float priceInUSDT, String position) {
        this.boughtQty = boughtQty;
        this.orderId = orderId;
        this.symbol = symbol;
        this.soldQty = soldQty;
        this.status = status;
        this.side = side;
        this.commisionAsset = commission;
        this.priceInUSDT = priceInUSDT;
        this.dateTime = dateTime;
        this.position = position;
    }

    @Override
    public String toString() {
        return "Transaction{ " +
                "orderId=" + orderId +
                ", symbol='" + symbol + '\'' +
                ", soldQty=" + soldQty +
                ", boughtQty=" + boughtQty +
                ", status='" + status + '\'' +
                ", side='" + side + '\'' +
                ", commisionAsset='" + commisionAsset + '\'' +
                ", priceInUSDT=" + priceInUSDT +
                ", dateTime=" + dateTime +
                '}';
    }
}
