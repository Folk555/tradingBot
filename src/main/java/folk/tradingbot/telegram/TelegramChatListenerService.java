package folk.tradingbot.telegram;

import folk.tradingbot.trader.CashFlowTrader;
import folk.tradingbot.telegram.models.TelegramChat;
import folk.tradingbot.telegram.models.TelegramUpdateMessage;
import folk.tradingbot.trader.FinamTrader;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Обработчик новых сообщений. Подписывает его на определенный чат,
 * добавляем логику этого чата и сервис будет обрабатывать каждое новое сообщение
 */
@Service
public class TelegramChatListenerService {
    @Autowired
    private TelegramClient telegramClient;
    @Autowired
    private CashFlowTrader cashFlowTrader;
    @Autowired
    private FinamTrader finamTrader;

    @Getter
    @Setter
    private Set<String> targetChatName;
    @Getter
    private Map<String, Long> targetChatIdByName = new HashMap<>();

    private String tgCashFlowName = "СИГНАЛЫ от CASHFLOW";
    private String tgChatGpt = "ChatGPT Midjourney :: BotHub Bot";
    private String tgFinamName = "Финам Торговые сигналы";

    private static Logger LOGGER = LogManager.getLogger(TelegramChatListenerService.class);

    @PostConstruct
    private void init() {
        LOGGER.info("Пост инициализация TelegramChatListenerService");
        targetChatName = new HashSet<>(List.of(tgCashFlowName, tgChatGpt, tgFinamName));
        List<TelegramChat> mainChatList = telegramClient.getMainChatList(40);
        mainChatList.stream().filter(telegramChat -> targetChatName.contains(telegramChat.getChatName()))
                .forEach(telegramChat -> targetChatIdByName.put(telegramChat.getChatName(), telegramChat.getId()));
        LOGGER.info("Завершена пост инициализация TelegramChatListenerService, слушаем ТГ каналы {}",
                targetChatIdByName.toString());
    }

    public void processMessage(TelegramUpdateMessage updateMessage) {
        Long selfUserId = telegramClient.getMyUser().getUserId();
        Long senderChatId = updateMessage.getMessageSenderId();
        String messageContent = updateMessage.getMessageContent();
        if (Objects.equals(selfUserId, senderChatId) || !targetChatIdByName.containsValue(senderChatId)) {
            LOGGER.trace("Пришло сообщение, которое пропускаем {}", updateMessage.getMessageContent());
            return;
        }
        if (targetChatIdByName.get(tgCashFlowName).equals(senderChatId)) {
            cashFlowTrader.cashFlow(messageContent);
        } else if (targetChatIdByName.get(tgFinamName).equals(senderChatId)) {
            finamTrader.finamTrader(messageContent);
        } else if (targetChatIdByName.get(tgChatGpt).equals(senderChatId)) {
            String message = "Пришло тестовое сообщение " + messageContent;
            LOGGER.info(message);
            telegramClient.sendMessageToMainChat(message);
        } else {
            LOGGER.error("косяк processMessage");
        }
    }

    public void processMessageDebug(Long chatId) {
        telegramClient.getLastMessagesTxtFromChat(chatId, 10);
        String messageContent = telegramClient.getMessageById(chatId, 4540334080L);

        cashFlowTrader.cashFlow(messageContent);
        System.out.println("Инвистиционные идеи:");
        cashFlowTrader.traderIdeaRepo.getAllTraderIdeas().forEach(System.out::println);
        System.out.println("\n");
        System.out.println("Трейдер позиции:");
        cashFlowTrader.traderPositionRepo.getAllTraderPositions().forEach(System.out::println);
        System.out.println("\n");
    }

}
