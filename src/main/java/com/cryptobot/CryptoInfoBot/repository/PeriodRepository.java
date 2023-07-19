package com.cryptobot.CryptoInfoBot.repository;

import com.cryptobot.CryptoInfoBot.singleton.Period;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PeriodRepository extends JpaRepository<Period,Long> {
    Period findTopByOrderByPeriodIdDesc();
    Period findTopByOrderByPeriodIdAsc();

  
}
