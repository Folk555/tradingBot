package folk.tradingbot.trader;

import folk.tradingbot.Utils;
import folk.tradingbot.telegram.TelegramClient;
import folk.tradingbot.tinvestapi.dto.TBankShare;
import folk.tradingbot.tinvestapi.repository.ShareRepo;
import folk.tradingbot.trader.dto.TraderIdea;
import folk.tradingbot.trader.dto.TraderIdeaStatus;
import folk.tradingbot.trader.dto.TraderPosition;
import folk.tradingbot.trader.repository.TraderIdeaImplArrayList;
import folk.tradingbot.trader.repository.TraderIdeaRepo;
import folk.tradingbot.trader.repository.TraderPositionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class CashFlowTrader {

    @Autowired
    private TelegramClient telegramClient;
    public TraderIdeaRepo traderIdeaRepo = new TraderIdeaImplArrayList();
    @Autowired
    public TraderPositionRepo traderPositionRepo;
    @Autowired
    private MainTraderGate mainTraderGate;
    @Autowired
    private ShareRepo shareRepo;

    public void cashFlow(String message) {
        if (message.contains("Инвестиционная идея")) 
            createTraderIdea(message);
        else if (message.contains("ПОКУПКА LONG!"))
            openPosition(message);
        else if (message.contains("Фиксирую"))
            reopenTraderPosition(message);
        else if (message.contains("Несколько мыслей по")
                || (message.contains("Российский фондовый рынок завершил в"))
                || (message.contains("Друзья! На Ютуб канале"))
                || (message.contains("вступай в Premium"))) {
            System.out.println("ненужное сообщение получено от cashflow");
        } else {
            telegramClient.sendMessageToMainChat("Неожиданное сообщение в CASHFLOW\n\n" + message);
        }
    }

    private void createTraderIdea(String message) {
        String regex = "(.*)Инвестиционная идея: (.*) #.*";
        String name = Utils.findGroupFromRegax(message, regex, 2);
        String ticker = Utils.findGroupFromRegax(message, "(.*?)#(\\w+)(.*)", 2);
        String goalsString = message.substring(message.indexOf("Цели:"));
        String[] split = goalsString.split("цель:");
        ArrayList<Integer> goals = new ArrayList<>();
        for (int i = 1; i < split.length; i++) {
            String groupFromRegax = Utils.findGroupFromRegax(split[i], " (\\d*)Р(.*)", 1);
            goals.add(Integer.parseInt(groupFromRegax));
        }

        TraderIdea traderIdea = new TraderIdea((long) traderIdeaRepo.getAllTraderIdeas().size(), name, ticker, goals,
                TraderIdeaStatus.DRAFT);
        traderIdeaRepo.saveTraderIdea(traderIdea);
    }

    private void openPosition(String message) {
        String name = Utils.findGroupFromRegax(message, "(.*)ПОКУПКА LONG!\n(\\W+)\n(.*)", 2);
        String ticker = Utils.findGroupFromRegax(message, "(.*?)#(\\w+)(.*)", 2);
        Float startPrice = Float.parseFloat(Utils.findGroupFromRegax(message, "(.*)ВХОД: (\\d+[.]?\\d*)(Р|P)+(.*)", 2));
        Float stopPrice = Float.parseFloat(Utils.findGroupFromRegax(message, "(.*)Стоп: (\\d+[.]?\\d*)(Р|P)(.*)", 2));
        Float profitPrice = Float.parseFloat(Utils.findGroupFromRegax(message, "(.*)Цель: (\\d+[.]?\\d*)(Р|P)(.*)", 2));
        Float profitPercent = Float.parseFloat(Utils.findGroupFromRegax(message, "(.*)\\+(\\d+[.]+\\d+)%(.*)", 2));

        TraderPosition traderPosition = new TraderPosition(name, ticker, startPrice, profitPrice, profitPercent,
                null, stopPrice, false, LocalDateTime.now(), null);
        TBankShare shareByTicker = shareRepo.findSharesByTicker(ticker).getFirst();
        if (shareByTicker == null) {
            traderPosition.setClosed(true);
            traderPosition.setErrorCreate("Не смогли найти акцию");
        } else {
            traderPosition.setShareInstrumentId(shareByTicker.getUid());
            mainTraderGate.buyShares(traderPosition);
            String stopLoseId = mainTraderGate.createStopLose(traderPosition);
            traderPosition.setStopLoseOrderId(stopLoseId);
        }
        traderPositionRepo.save(traderPosition);
        telegramClient.sendMessageToMainChat("Открыли позицию\n" + traderPosition);
    }

    private void reopenTraderPosition(String message) {
        String ticker = Utils.findGroupFromRegax(message, "(.*)#(\\w+)(.*)", 2);
        Float closedProfitProcent = Float.parseFloat(Utils.findGroupFromRegax(message,
                "(.*)это \\+(\\d+[.]+\\d+)%(.*)", 2));

        TraderPosition traderPosition = traderPositionRepo.getLastOpenTraderPositionByTicker(ticker);
        if (traderPosition == null)
            return;
        TraderPosition newTraderPosition = new TraderPosition(traderPosition);
        traderPosition.setClosed(true);
        traderPosition.setCloseProfitPercent(closedProfitProcent);
        traderPosition.setCloseTime(LocalDateTime.now());
        traderPositionRepo.save(traderPosition);
        mainTraderGate.cancelStopLose(traderPosition);
        String msgToMainChat;
        if (message.contains("Фиксирую") && !message.contains("финальную")) {
            Float startPrice = Float.parseFloat(Utils.findGroupFromRegax(message, "(.*)по (\\d+[.]?\\d*)(Р|P)(.*)", 2));
            Float stopPrice = Float.parseFloat(Utils.findGroupFromRegax(message, "(.*)в безубыток(.*)Р(.*)", 2));
            LocalDateTime now = LocalDateTime.now();
            newTraderPosition.setOpenTime(now);
            newTraderPosition.setStartPrice(startPrice);
            newTraderPosition.setStopPrice(stopPrice);
            newTraderPosition.setProfitPrice(null);

            String stopLoseId = mainTraderGate.createStopLose(newTraderPosition);
            newTraderPosition.setStopLoseOrderId(stopLoseId);

            traderPositionRepo.save(newTraderPosition);
            msgToMainChat = "Переставили стоп лос, новая позиция\n" + newTraderPosition;
        } else if (message.contains("финальную")) {
            mainTraderGate.sellShares(traderPosition);
            msgToMainChat = "закрыли позицию с прибылью\n" + traderPosition;
        } else {
            msgToMainChat = "ошибка при переоткрытии позиции\n" + traderPosition;
            telegramClient.sendMessageToMainChat(msgToMainChat);
        }
        telegramClient.sendMessageToMainChat(msgToMainChat);
    }

}