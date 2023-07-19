package com.cryptobot.CryptoInfoBot.repository;

import com.cryptobot.CryptoInfoBot.singleton.CryptoCoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CryptoRepository extends JpaRepository<CryptoCoin, Long> {
    CryptoCoin findTopByOrderByIdDesc();

    CryptoCoin findTopByOrderByIdAsc();
    List<CryptoCoin> findFirst24ByOrderByIdDesc();
    @Query(value = "SELECT sum(price) FROM CryptoCoin")
    Long totalPrice();
}
