package folk.tradingbot.trader.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TraderPosition extends CrudRepository<TraderPosition, Integer> {

}
