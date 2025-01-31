package folk.tradingbot.trader.repository;

import folk.tradingbot.trader.dto.TraderPosition;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TraderPositionRepo extends CrudRepository<TraderPosition, Integer>, TraderPositionRepoCustom {
    TraderPosition getTraderPositionById(int id);
}
