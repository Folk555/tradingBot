package folk.tradingbot.repository;

import folk.tradingbot.dto.TraderIdea;

import java.util.ArrayList;

public class TraderIdeaImplArrayList implements TraderIdeaRepo{
    private ArrayList<TraderIdea> list = new ArrayList<>();

    @Override
    public void saveTraderIdea(TraderIdea traderIdea) {
        list.add(traderIdea);
    }

    @Override
    public ArrayList<TraderIdea> getAllTraderIdeas() {
        return list;
    }

}
