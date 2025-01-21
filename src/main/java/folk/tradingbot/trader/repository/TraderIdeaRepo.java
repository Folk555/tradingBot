package folk.tradingbot.trader.repository;

import folk.tradingbot.trader.dto.TraderIdea;

import java.util.ArrayList;

public interface TraderIdeaRepo {
    void saveTraderIdea(TraderIdea traderIdea);
    ArrayList<TraderIdea> getAllTraderIdeas();
}
