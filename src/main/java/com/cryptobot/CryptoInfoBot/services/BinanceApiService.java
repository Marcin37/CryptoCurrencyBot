package com.cryptobot.CryptoInfoBot.services;


import com.binance.connector.client.impl.SpotClientImpl;
import com.cryptobot.CryptoInfoBot.repository.CryptoRepository;
import com.cryptobot.CryptoInfoBot.repository.OrdersRepository;
import com.cryptobot.CryptoInfoBot.singleton.CryptoCoin;
import com.cryptobot.CryptoInfoBot.singleton.Transaction;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.math.RoundingMode;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Random;

@Service
public class BinanceApiService {
    final private String BASE_URL = "https://testnet.binance.vision";
    final private String API_KEY;
    final private String SECRET_KEY;
    final private CryptoRepository cryptoRepository;
    final private OrdersRepository ordersRepository;
    private WebClient webClient;

    @Autowired
    private BinanceApiService(@Value("${api.key}") String API_KEY, @Value("${secret.key}") String SECRET_KEY, CryptoRepository cryptoRepository, OrdersRepository ordersRepository) {
        this.API_KEY = API_KEY;
        this.SECRET_KEY = SECRET_KEY;
        this.cryptoRepository = cryptoRepository;
        this.ordersRepository = ordersRepository;
        this.webClient = buildWebClient();
    }


    private WebClient buildWebClient() {
        WebClient client = WebClient.builder()
                .defaultHeader("X-MBX-APIKEY", API_KEY)
                .build();
        return client;
    }



    public Object getAccountInfo() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String message = "recvWindow=50000&timestamp=" + timestamp;
        String signature = sign(message);
        return webClient.get().uri(BASE_URL + "/api/v3/account?" + message + "&signature=" + signature).retrieve().bodyToFlux(LinkedHashMap.class).blockFirst();
    }

    public float getUserAsset(String symbol) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String message = "recvWindow=50000&timestamp=" + timestamp;
        String signature = sign(message);
        Object asset = Objects.requireNonNull(webClient
                .get()
                .uri(BASE_URL + "/api/v3/account?" + message + "&signature=" + signature)
                .retrieve()
                .bodyToFlux(LinkedHashMap.class)
                .blockFirst())
                .get("balances");
        ArrayList<LinkedHashMap> assets = (ArrayList) asset;
        for (LinkedHashMap o : assets) {
            if (o.get("asset").equals(symbol)) {
                String test = o.get("free").toString();
                return Float.parseFloat(o.get("free").toString());
            }
        }
        return 0f;
    }

    public String getUserAssetInString(String symbol) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String message = "recvWindow=50000&timestamp=" + timestamp;
        String signature = sign(message);
        Object asset = Objects.requireNonNull(webClient.get().uri(BASE_URL + "/api/v3/account?" + message + "&signature=" + signature).retrieve().bodyToFlux(LinkedHashMap.class).blockFirst()).get("balances");
        ArrayList<LinkedHashMap> assets = (ArrayList) asset;
        for (LinkedHashMap o : assets) {
            if (o.get("asset").equals(symbol)) {
                return o.get("free").toString();
            }
        }
        return null;
    }




    public void getETHLastPrice() {
        CryptoCoin cryptoCoin = webClient
                .get()
                .uri(BASE_URL + "/api/v3/ticker/price?symbol=ETHUSDT")
                .retrieve()
                .bodyToFlux(CryptoCoin.class)
                .blockFirst();
        assert cryptoCoin != null;
        cryptoCoin.setDate(LocalDateTime.now());
        cryptoRepository.save(cryptoCoin);
    }





    public LinkedHashMap<String, Object> makeAnOrder(String typeOfOrder) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<String, Object>();
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.DOWN);
        SpotClientImpl client = new SpotClientImpl(API_KEY, SECRET_KEY, BASE_URL);

        String availableCrypto;
        String market = "";
        if (typeOfOrder.toUpperCase().equals("SELL")) {
            float userAssetEth = getUserAsset("ETH");
            if (userAssetEth > 0.3) {
                availableCrypto = getUserAssetInString("ETH"); //because float has issues with rounding values
                market = "quantity";
            } else {
                throw new RuntimeException("There is not enough ETH available");
            }
        } else {
            float userAssetUsdt = getUserAsset("USDT");
            if (userAssetUsdt > 10) {
                availableCrypto = getUserAssetInString("USDT");
                market = "quoteOrderQty";
            } else {
                throw new RuntimeException("There is not enough USDT available");
            }

        }
        parameters.put("recvWindow", "50000");
        parameters.put("symbol", "ETHUSDT");
        parameters.put("side", typeOfOrder.toUpperCase());
        parameters.put("type", "MARKET");
        parameters.put(market, availableCrypto);
        parameters.put("timestamp", timestamp);
        String result = client.createTrade().newOrder(parameters);
        ObjectMapper mapper = new ObjectMapper();
        try {
            LinkedHashMap<String, Object> resultMap = mapper.readValue(result, LinkedHashMap.class);
            Transaction transaction = getTransaction(typeOfOrder, resultMap);
            ordersRepository.save(transaction);
            return resultMap;
        } catch (JacksonException js) {
            throw new RuntimeException("This string is not suitable to be converted to JSON object", js);
        }

    }


    private Transaction getTransaction(String typeOfOrder, LinkedHashMap<String, Object> resultMap) {
        Transaction transaction = new Transaction();

        transaction.setOrderId(Long.parseLong(resultMap.get("orderId").toString()));
        transaction.setSide(resultMap.get("side").toString());
        transaction.setSymbol(resultMap.get("symbol").toString());
        transaction.setStatus(resultMap.get("status").toString());
        transaction.setDateTime(LocalDateTime.now());

        if (typeOfOrder == "SELL") {
            transaction.setBoughtQty((Float.parseFloat(resultMap.get("cummulativeQuoteQty").toString())));
            transaction.setSoldQty((Float.parseFloat(resultMap.get("executedQty").toString())));
        } else {
            transaction.setBoughtQty((Float.parseFloat(resultMap.get("executedQty").toString())));
            transaction.setSoldQty((Float.parseFloat(resultMap.get("cummulativeQuoteQty").toString())));
        }
        try {
            ArrayList fills = (ArrayList) resultMap.get("fills");
            LinkedHashMap<String, String> fillsMap = (LinkedHashMap<String, String>) fills.get(0);
            transaction.setPriceInUSDT(Float.parseFloat(fillsMap.get("price")));
            transaction.setCommisionAsset(fillsMap.get("commissionAsset"));
        } catch (Exception e) {
            throw new RuntimeException("Conversion of 'fills' field failed", e);
        }


        makeANotification(typeOfOrder, transaction);


        return transaction;
    }

    private void makeANotification(String typeOfOrder, Transaction transaction) {
        System.setProperty("java.awt.headless", "false");
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
            TrayIcon trayIcon = new TrayIcon(image, "");
            try {
                tray.add(trayIcon);
                trayIcon.displayMessage("Transaction of type " + typeOfOrder + " has been made", transaction.toString(), TrayIcon.MessageType.INFO);
            } catch (AWTException awtException) {
                throw new RuntimeException(awtException);
            }
        }
        System.setProperty("java.awt.headless", "true");
    }

    private String sign(String url) {
        try {
            Mac shaMac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
            shaMac.init(keySpec);
            final byte[] macData = shaMac.doFinal(url.getBytes());
            return Hex.encodeHexString(macData);
        } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
            throw new RuntimeException("Unable to use this algorithm", noSuchAlgorithmException);
        } catch (InvalidKeyException invalidKeyException) {
            throw new RuntimeException("Key is invalid", invalidKeyException);
        }

    }

}
