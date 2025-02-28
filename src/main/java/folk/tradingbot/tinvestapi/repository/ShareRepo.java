package folk.tradingbot.tinvestapi.repository;

import folk.tradingbot.tinvestapi.dto.TBankShare;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareRepo extends CrudRepository<TBankShare, Integer> {
    List<TBankShare> findSharesByTicker(String ticker);
    TBankShare findByIsin(String isin);
}
