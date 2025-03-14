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
    private double maxSingleLotSum = 10000.0;

    @Autowired
    private TBankClient bankClient;
    @Autowired
    private TBankClient tBankClient;

    public synchronized String buyShares(TraderPosition traderPosition) {
        String instrumentId = traderPosition.getShareInstrumentId();
        double maxPricePerShare = traderPosition.getStartPrice() * 1.02;
        int lengthAfterDot = String.valueOf(traderPosition.getStartPrice()).split("\\.")[1].length();
        BigDecimal bd = new BigDecimal(Double.toString(maxPricePerShare));
        maxPricePerShare = bd.setScale(lengthAfterDot, RoundingMode.HALF_UP).doubleValue();

        double currentRubPricePerShare = tBankClient.getCurrentSharePriceFromBroker(instrumentId);
        if (currentRubPricePerShare > maxPricePerShare) {
            LOGGER.warn("Цена за акцию превышает максимальную допустимую цену за акцию");
            return null;
        }

        int lot = tBankClient.getShareByInstrumentId(instrumentId).getLot();
        double currentRubPricePerLot = lot * currentRubPricePerShare;
        int buyCountLots = (int) (sumBuy / currentRubPricePerLot);
        String dBMessage = null;
        String shareBuyResponse = null;
        if ((buyCountLots >= 1)) {
            LOGGER.trace("MainTraderGate пытается купить {} на сумму {}, где цена за акцию не превышает {}",
                    traderPosition.getTicker(), sumBuy, maxPricePerShare);
            shareBuyResponse = bankClient.buyShares(instrumentId, maxPricePerShare, buyCountLots);
        } else if (currentRubPricePerLot <= maxSingleLotSum) {
            LOGGER.trace("MainTraderGate пытается купить 1 лот {}, где цена за акцию не превышает {}",
                    traderPosition.getTicker(), maxSingleLotSum);
            shareBuyResponse = bankClient.buyShares(instrumentId, maxPricePerShare, 1);
        } else if (currentRubPricePerLot > maxSingleLotSum) {
            String message = "Цена за лот " + currentRubPricePerLot +
                    " превышает максимально допустимую ботом цену лота " + maxSingleLotSum;
            dBMessage = "Покупка не удалась\n" + message;
            LOGGER.warn(dBMessage);
        }

        if ((dBMessage != null) || !shareBuyResponse.contains("order_id")) {
            if (dBMessage == null)
                dBMessage = shareBuyResponse;
            LOGGER.warn("Покупка не удалась {}", dBMessage);
            traderPosition.setErrorCreate("покупка не удалась\n" + dBMessage);
            traderPosition.setClosed(true);
            return null;
        }
        return shareBuyResponse;
    }

    /**
     * @return ID созданного стоп лоса
     */
    public synchronized String createStopLose(TraderPosition traderPosition) {
        return bankClient.createPostStopLose(traderPosition);
    }

    /**
     * @return ID созданного тейк профита
     */
    public synchronized String createPostTakeProfit(TraderPosition traderPosition) {
        return bankClient.createPostTakeProfit(traderPosition);
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
