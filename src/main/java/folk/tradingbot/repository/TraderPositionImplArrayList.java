package folk.tradingbot.repository;

import folk.tradingbot.dto.TraderPosition;

import java.util.ArrayList;
import java.util.Optional;

public class TraderPositionImplArrayList implements TraderPositionRepo{
    private ArrayList<TraderPosition> list = new ArrayList<>();


    @Override
    public void saveTraderPosition(TraderPosition traderPosition) {
        Optional<TraderPosition> first =
                list.stream().filter(pos -> pos.getId().equals(traderPosition.getId())).findFirst();
        if (first.isPresent())
            list.set(Math.toIntExact(traderPosition.getId()), traderPosition);
        else
            list.add(traderPosition);
    }

    @Override
    public ArrayList<TraderPosition> getAllTraderPosition() {
        return list;
    }

    @Override
    public TraderPosition getLastOpenTraderPositionByTicker(String ticker) {
        return list.stream().filter(traderPosition -> !traderPosition.isClosed())
                .filter(traderPosition -> traderPosition.getTicker().equals(ticker)).findFirst().orElse(null);
    }
}
