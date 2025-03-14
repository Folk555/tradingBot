package folk.tradingbot.trader.crons;

import folk.tradingbot.tinvestapi.TBankClient;
import folk.tradingbot.trader.dto.TraderPosition;
import folk.tradingbot.trader.repository.TraderPositionRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CleanStopLoseClosePositions {
    @Autowired
    private TraderPositionRepo traderPositionRepo;
    @Autowired
    private TBankClient tBankClient;
    private static Logger LOGGER = LogManager.getLogger(CleanStopLoseClosePositions.class);
    @Scheduled(cron = "0 0 4 * * 0")
    public void runTask() {
        LOGGER.info("Началось выполнение крона закрытия позиций из БД которые закрылись по стоп лосу");
        List<TraderPosition> allOpenTraderPositions = traderPositionRepo.getAllOpenTraderPositions();
        allOpenTraderPositions.forEach(traderPosition -> {
            String shareUid = traderPosition.getShareInstrumentId();
            int lotsPortfolio = tBankClient.getLotCountInPortfolioByInstrumentId(shareUid);
            if (lotsPortfolio == 0) {
                traderPosition.setClosed(true);
                traderPositionRepo.save(traderPosition);
            }
        });
        LOGGER.info("Крона закрытия позиций из БД которые закрылись по стоп лосу завершил работу");
    }
}
