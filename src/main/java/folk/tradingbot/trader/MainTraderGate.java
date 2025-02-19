package folk.tradingbot.trader;

import folk.tradingbot.tinvestapi.TBankClient;
import folk.tradingbot.trader.dto.TraderPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MainTraderGate {

    private double sumBuy = 2000.0;

    @Autowired
    private TBankClient bankClient;

    public synchronized void buyShares(TraderPosition traderPosition) {
        String instrumentId = traderPosition.getShareInstrumentId();
        double maxPricePerShare = traderPosition.getStartPrice() * 1.02;
        BigDecimal bd = new BigDecimal(Double.toString(maxPricePerShare));
        maxPricePerShare = bd.setScale(5, RoundingMode.HALF_UP).floatValue();

        String res = bankClient.buyShares(instrumentId, maxPricePerShare, sumBuy);
        if (!res.contains("OrderId")) {
            traderPosition.setErrorCreate("покупка не удалась\n" + res);
            traderPosition.setClosed(true);
        }
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
