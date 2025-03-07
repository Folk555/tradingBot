package folk.tradingbot.trader.repository;

import folk.tradingbot.trader.dto.TraderPosition;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

public class TraderPositionRepoImpl implements TraderPositionRepoCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public TraderPosition getLastOpenTraderPositionByTicker(String ticker) {
        String query = "from TraderPosition WHERE isClosed = false AND ticker = :ticker";
        List<TraderPosition> traderPositions = em.createQuery(query, TraderPosition.class).setParameter("ticker", ticker).getResultList();
        return !traderPositions.isEmpty() ? traderPositions.getFirst() : null;
    }

    @Override
    public List<TraderPosition> getAllTraderPositions() {
        String query = "from TraderPosition";
        return em.createQuery(query, TraderPosition.class).getResultList();
    }

    @Override
    public List<TraderPosition> getAllOpenTraderPositions() {
        String query = "from TraderPosition WHERE isClosed = false";
        return em.createQuery(query, TraderPosition.class).getResultList();
    }
}
