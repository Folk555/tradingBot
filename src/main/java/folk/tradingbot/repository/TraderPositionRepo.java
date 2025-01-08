package folk.tradingbot.repository;

import folk.tradingbot.dto.TraderPosition;

import java.util.ArrayList;

public interface TraderPositionRepo {
    void saveTraderPosition(TraderPosition traderPosition);
    ArrayList<TraderPosition> getAllTraderPosition();
    TraderPosition getLastOpenTraderPositionByTicker(String ticker);
}
