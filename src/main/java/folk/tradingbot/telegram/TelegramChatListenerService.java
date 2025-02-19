package folk.tradingbot.telegram;

import folk.tradingbot.trader.CashFlowTrader;
import folk.tradingbot.telegram.configs.TelegramConfigs;
import folk.tradingbot.telegram.models.TelegramChat;
import folk.tradingbot.telegram.models.TelegramUpdateMessage;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Обработчик новых сообщений. Подписывает его на определенный чат,
 * добавляем логику этого чата и сервис будет обрабатывать каждое новое сообщение
 */
@Service
public class TelegramChatListenerService {

    @Autowired
    private TelegramConfigs telegramConfigs;
    @Autowired
    private TelegramClient telegramClient;
    @Autowired
    private CashFlowTrader cashFlowTrader;

    @Getter
    @Setter
    private Set<String> targetChatName;
    @Getter
    private Map<String, Long> targetChatIdByName = new HashMap<>();

    @PostConstruct
    private void init() {
        String[] split = telegramConfigs.tradingChatName.split(";");
        targetChatName = new HashSet<>(List.of(split));
        List<TelegramChat> mainChatList = telegramClient.getMainChatList(40);
        mainChatList.stream().filter(telegramChat -> targetChatName.contains(telegramChat.getChatName()))
                .forEach(telegramChat -> targetChatIdByName.put(telegramChat.getChatName(), telegramChat.getId()));
    }

    public void processMessage(TelegramUpdateMessage updateMessage) {
        //System.out.println(updateMessage.getMessage());
        Long selfUserId = telegramClient.getMyUser().getUserId();
        Long senderChatId = updateMessage.getMessageSenderId();
        if (Objects.equals(selfUserId, senderChatId)) {
            return;
        }
        if (!targetChatIdByName.containsValue(senderChatId)) {
            //System.out.println("senderChatId: " + senderChatId);
            String collect = targetChatIdByName.keySet().stream()
                    .map(key -> key + "=" + targetChatIdByName.get(key))
                    .collect(Collectors.joining(", ", "{", "}"));
            //System.out.println(collect);
            return;
        }
        String messageContent = updateMessage.getMessageContent();
        telegramClient.sendMessageToMainChat("Пришло сообщение\n" + messageContent);

        cashFlowTrader.cashFlow(messageContent);
        //System.out.println("Трейдер позиции:");
        cashFlowTrader.traderPositionRepo.getAllTraderPositions().forEach(System.out::println);
        //System.out.println("\n");
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
