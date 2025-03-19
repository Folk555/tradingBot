package folk.tradingbot.trader;

import folk.tradingbot.Utils;
import folk.tradingbot.telegram.TelegramClient;
import folk.tradingbot.tinvestapi.TBankClient;
import folk.tradingbot.tinvestapi.dto.TBankShare;
import folk.tradingbot.tinvestapi.repository.ShareRepo;
import folk.tradingbot.trader.dto.TraderPosition;
import folk.tradingbot.trader.repository.TraderIdeaImplArrayList;
import folk.tradingbot.trader.repository.TraderIdeaRepo;
import folk.tradingbot.trader.repository.TraderPositionRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FinamTrader {

    private static Logger LOGGER = LogManager.getLogger(FinamTrader.class);

    @Autowired
    private TelegramClient telegramClient;
    @Autowired
    private TBankClient tBankClient;
    public TraderIdeaRepo traderIdeaRepo = new TraderIdeaImplArrayList();
    @Autowired
    public TraderPositionRepo traderPositionRepo;
    @Autowired
    private MainTraderGate mainTraderGate;
    @Autowired
    private ShareRepo shareRepo;

    public void finamTrader(String message) {
        if (message.contains("Идея: Long"))
            openPosition(message);
        else
            LOGGER.trace("ненужное сообщение получено от finamTrader {}", message);
    }

    private void openPosition(String message) {
        LOGGER.info("Начинаем открывать позицию от Финам");
        String name = message.split("\n")[0];
        String ticker = Utils.findGroupFromRegax(message, "(.*?)Тикер: #(\\w+)(.*)", 2);
        Float stopPrice = Float.parseFloat(Utils.findGroupFromRegax(message, "(.*)Стоп-приказ: (\\d+[.]?\\d*)( руб.)(.*)", 2));
        Float profitPrice = Float.parseFloat(Utils.findGroupFromRegax(message, "(.*)Цель: (\\d+[.]?\\d*)( руб.)(.*)", 2));
        String groupFromRegax = Utils.findGroupFromRegax(message, "(.*)Потенциал идеи: +(\\d+[,\\d]*)%(.*)", 2);
        Float profitPercent = Float.parseFloat(groupFromRegax.replace(',', '.'));
        Float startPrice = tBankClient.getSharePriceByTicker(ticker).floatValue();

        TraderPosition traderPosition = new TraderPosition(name, ticker, startPrice, profitPrice, profitPercent,
                null, stopPrice, false, LocalDateTime.now(), null, "Финам Торговые сигналы");
        LOGGER.trace("Новая позиция-кандидат {}", traderPosition);
        TBankShare shareByTicker = shareRepo.findSharesByTicker(ticker).getFirst();
        if (shareByTicker == null) {
            traderPosition.setClosed(true);
            traderPosition.setErrorCreate("Не смогли найти акцию в БД");
            LOGGER.warn("Не смогли найти акцию в БД");
        } else {
            traderPosition.setShareInstrumentId(shareByTicker.getUid());
            LOGGER.trace("Нашли акцию в БД");
            String res = mainTraderGate.buyShares(traderPosition);
            String stopLoseId = null;
            String takeProfitId = null;
            if (res != null) {
                stopLoseId = mainTraderGate.createStopLose(traderPosition);
                takeProfitId = mainTraderGate.createPostTakeProfit(traderPosition);
            }
            traderPosition.setStopLoseOrderId(stopLoseId);
            traderPosition.setTakeProfitOrderId(takeProfitId);
        }
        traderPositionRepo.save(traderPosition);
        telegramClient.sendMessageToMainChat("Открыли позицию\n" + traderPosition);
        LOGGER.info("Новая позиция Finam открыта {}", traderPosition);
    }

}