package folk.tradingbot.repository;

import folk.tradingbot.dto.TraderIdea;

import java.util.ArrayList;

public interface TraderIdeaRepo {
    void saveTraderIdea(TraderIdea traderIdea);
    ArrayList<TraderIdea> getAllTraderIdeas();
}
