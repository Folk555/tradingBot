package folk.tradingbot.tinvestapi;

import folk.tradingbot.trader.dto.TraderPosition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TBankClientTest {

    @Autowired
    TBankClient tBankClient;

    @Test
    void createPostTakeProfit() {
        TraderPosition traderPosition = new TraderPosition();
        traderPosition.setName("\uD83D\uDCC8 ПАО «Элемент»");
        traderPosition.setTicker("ELMT");
        traderPosition.setProfitPrice(0.1705F);
        traderPosition.setStopPrice(0.14F);
        traderPosition.setShareInstrumentId("f1b89b92-51e2-4db5-89df-17c228aa41fd");

        tBankClient.createPostTakeProfit(traderPosition);
    }
}