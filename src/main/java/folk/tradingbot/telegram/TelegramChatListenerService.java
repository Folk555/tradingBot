package folk.tradingbot.telegram;

import folk.tradingbot.telegram.configs.TelegramConfigs;
import folk.tradingbot.telegram.models.TelegramChat;
import folk.tradingbot.telegram.models.TelegramUpdateMessage;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
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
    private TelegramConfigs telegramConfigs;
    @Autowired
    private TelegramClient telegramClient;

    @Getter
    @Setter
    private Set<String> targetChatName;
    private Map<String, Long> chatIdByName = new HashMap<>();

    @PostConstruct
    private void init() {
        String[] split = telegramConfigs.tradingChatName.split(";");
        targetChatName = new HashSet<>(List.of(split));
        List<TelegramChat> mainChatList = telegramClient.getMainChatList(40);
        mainChatList.stream().filter(telegramChat -> targetChatName.contains(telegramChat.getChatName()))
                .forEach(telegramChat -> chatIdByName.put(telegramChat.getChatName(), telegramChat.getId()));
    }

    public void processMessage(TelegramUpdateMessage updateMessage) {
        Long selfUserId = telegramClient.getMyUser().getUserId();
        Long senderId = updateMessage.getMessageSenderId();
        if (Objects.equals(selfUserId, senderId))
            return;
        if (!chatIdByName.containsValue(senderId))
            return;
        String messageContent = updateMessage.getMessageContent();
        telegramClient.sendMessage(346L, messageContent);
    }
}
