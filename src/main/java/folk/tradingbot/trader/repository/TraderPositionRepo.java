package folk.tradingbot.trader.repository;

import folk.tradingbot.trader.dto.TraderPosition;

import java.util.ArrayList;

public interface TraderPositionRepo {
    void saveTraderPosition(TraderPosition traderPosition);
    ArrayList<TraderPosition> getAllTraderPosition();
    TraderPosition getLastOpenTraderPositionByTicker(String ticker);
}
