package folk.tradingbot.trader.crons;

import folk.tradingbot.telegram.TelegramClient;
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
public class DayliStatistic {
    @Autowired
    private TraderPositionRepo traderPositionRepo;
    @Autowired
    private TelegramClient telegramClient;
    private static Logger LOGGER = LogManager.getLogger(DayliStatistic.class);
    @Scheduled(cron = "0 0 9 * * *")
    public void runTask() {
        LOGGER.info("Началось выполнение крона DayliStatistic");
        List<TraderPosition> allOpenTraderPositions = traderPositionRepo.getAllOpenTraderPositions();
        allOpenTraderPositions.forEach(traderPosition -> {
            int openPositionCount = allOpenTraderPositions.size();
            telegramClient.sendMessageToMainChat("Количество открытых позиций: " + openPositionCount);
        });
        LOGGER.info("Крон DayliStatistic закрнчил работу");
    }
}
