package folk.tradingbot.trader.repository;

import folk.tradingbot.trader.dto.TraderPosition;

import java.util.List;

public interface TraderPositionRepoCustom {
    TraderPosition getLastOpenTraderPositionByTicker(String ticker);
    List<TraderPosition> getAllTraderPositions();
}
