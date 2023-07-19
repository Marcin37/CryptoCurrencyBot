package com.cryptobot.CryptoInfoBot.repository;

import com.cryptobot.CryptoInfoBot.singleton.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<Transaction,Long> {
    Transaction findTopByOrderByOrderIdDesc();
}
