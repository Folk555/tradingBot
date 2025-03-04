package folk.tradingbot.trader;

import folk.tradingbot.tinvestapi.TBankClient;
import folk.tradingbot.trader.dto.TraderPosition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MainTraderGate {

    private static Logger LOGGER = LogManager.getLogger(MainTraderGate.class);

    private double sumBuy = 2000.0;

    @Autowired
    private TBankClient bankClient;

    public synchronized String buyShares(TraderPosition traderPosition) {
        String instrumentId = traderPosition.getShareInstrumentId();
        double maxPricePerShare = traderPosition.getStartPrice() * 1.02;
        int lengthAfterDot = String.valueOf(traderPosition.getStartPrice()).split("\\.")[1].length();
        BigDecimal bd = new BigDecimal(Double.toString(maxPricePerShare));
        maxPricePerShare = bd.setScale(lengthAfterDot, RoundingMode.HALF_UP).doubleValue();

        LOGGER.trace("MainTraderGate пытается купить {} на сумму {}, где цена за акцию не превышает {}",
                traderPosition.getTicker(), sumBuy, maxPricePerShare);
        String res = bankClient.buyShares(instrumentId, maxPricePerShare, sumBuy);
        if (!res.contains("order_id")) {
            LOGGER.warn("Покупка не удалась {}", res);
            traderPosition.setErrorCreate("покупка не удалась\n" + res);
            traderPosition.setClosed(true);
            return null;
        }
        return res;
    }

    /**
     * @return ID созданного стоп лоса
     */
    public synchronized String createStopLose(TraderPosition traderPosition) {
        return bankClient.createPostStopLose(traderPosition);
    }

    /**
     * @return ID нового стоп лоса
     */
    public synchronized void cancelStopLose(TraderPosition traderPosition) {
        bankClient.cancelPostStopLose(traderPosition);
    }

    public synchronized void sellShares(TraderPosition traderPosition) {
        String instrumentId = traderPosition.getShareInstrumentId();
        int lots = bankClient.getLotCountInPortfolioByInstrumentId(instrumentId);
        String res = bankClient.saleShares(instrumentId, lots);
        if (!res.contains("OrderId")) {
            traderPosition.setErrorCreate("покупка не удалась\n" + res);
        }
    }
}
