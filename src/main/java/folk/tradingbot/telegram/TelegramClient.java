package folk.tradingbot.telegram;

import folk.tradingbot.telegram.handlers.TelegramResultHandler;
import folk.tradingbot.telegram.handlers.TelegramResultObjectHandler;
import folk.tradingbot.telegram.handlers.TelegramUpdateHandler;
import folk.tradingbot.telegram.models.OrderedChat;
import folk.tradingbot.telegram.models.TelegramChat;
import folk.tradingbot.telegram.models.TelegramUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Обертка над оригинальным телеграмм клиентом TDLib
 * клиент для работы с телеграммом
 */
@PropertySource("file:application.properties")
public class TelegramClient {

    private final TelegramUpdateHandler updateHandler;
    private final Client client;
    private ArrayList<TelegramChat> chatList = new ArrayList<>();
    private TelegramUser myUser;
    @Value("${telegram.selfChatId}")
    private Long selfChat;
    private static Logger LOGGER = LogManager.getLogger(TelegramClient.class);

    /**
     * Не используй этот конструктор, он нужен для юнит тестов
     */
    @Deprecated
    public TelegramClient(TelegramUpdateHandler updateHandler, Client client) {
        this.updateHandler = updateHandler;
        this.client = client != null ? client : Client.create(updateHandler, null, null);
    }

    public TelegramClient(TelegramUpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
        client = Client.create(updateHandler, null, null);
    }

    public void send(TdApi.Function query, TelegramResultHandler resultHandler) {
        client.send(query, resultHandler);
    }

    public List<TelegramChat> getMainChatList(final int limit) {
        NavigableSet<OrderedChat> chatOrderList = updateHandler.getChatOrderList();

        TdApi.LoadChats tdApiFun = new TdApi.LoadChats(new TdApi.ChatListMain(), limit - chatList.size());
        sendAndWaitAns(tdApiFun, new TdApi.Ok());
        //далее handler обработает ответ и запишет в переменную updateHandler.chats

        java.util.Iterator<OrderedChat> iter = chatOrderList.iterator();
        for (int i = 0; i < limit && i < chatOrderList.size(); i++) {
            long chatId = iter.next().getChatId();
            TdApi.Chat chat = updateHandler.getChats().get(chatId);
            synchronized (chat) {
                TelegramChat telegramChat = new TelegramChat(chat);
                LOGGER.trace("получен чат {}", telegramChat);
                chatList.add(telegramChat);
            }
        }
        return chatList;
    }

    public TelegramUser getMyUser() {
        if (myUser == null) {
            TdApi.User responseTdapiObject = sendAndWaitAns(new TdApi.GetMe(), new TdApi.User());
            TelegramUser telegramUser = new TelegramUser(responseTdapiObject);
            myUser = responseTdapiObject == null ? null : telegramUser;
        }
        return myUser;
    }

    private <T extends TdApi.Object> T sendAndWaitAns(TdApi.Function tdApiFun, T tdApiResultObject) {
        try {
            Lock lock = new ReentrantLock();
            Condition condition = lock.newCondition();
            lock.lock();
            var resultObjectHandler = new TelegramResultObjectHandler<T>(tdApiResultObject.getConstructor(), lock, condition);
            client.send(tdApiFun, resultObjectHandler);
            condition.await();
            lock.unlock();
            return resultObjectHandler.getResponseTdapiObject();
        } catch (Exception e) {
            System.out.println("!!!!!!!!!!!!!ВНИМАНИЕ!!!!!!!");
            System.out.println("Возникло исключение в методе sendAndWaitAns");
            throw new RuntimeException(e);
        }
    }

    public void sendMessageToChat(Long targetChatId, String msg) {
        TdApi.FormattedText text = new TdApi.FormattedText(msg, null);
        TdApi.InputMessageContent content = new TdApi.InputMessageText(text, null, true);
        TdApi.SendMessage query = new TdApi.SendMessage(targetChatId, 0, null,
                null, null, content);
        client.send(query, null);
    }

    public void sendMessageToMainChat(String msg) {
        TdApi.FormattedText text = new TdApi.FormattedText(msg, null);
        TdApi.InputMessageContent content = new TdApi.InputMessageText(text, null, true);
        TdApi.SendMessage query = new TdApi.SendMessage(selfChat, 0, null,
                null, null, content);
        client.send(query, null);
    }

    public String[] getLastMessagesTxtFromChat(Long chatId, int messagesCount) {
        TdApi.GetChatHistory tdApiFun = new TdApi.GetChatHistory(chatId, 0, 0, messagesCount, false);
        TdApi.Messages lastMessage = sendAndWaitAns(tdApiFun, new TdApi.Messages());
        Long startId = lastMessage.messages[0].id;
        tdApiFun = new TdApi.GetChatHistory(chatId, startId, 0, messagesCount, false);
        TdApi.Messages lastMessages = sendAndWaitAns(tdApiFun, new TdApi.Messages());

        System.out.println("\n\n\n\n");
        String[] txtArray = Arrays.stream(lastMessages.messages)
                .filter(message -> message.content.getConstructor() == TdApi.MessagePhoto.CONSTRUCTOR)
                .map(message -> {
                    return ((TdApi.MessagePhoto)message.content).caption.text;
                })
                .toArray(String[]::new);
        return txtArray;
    }

    public String getMessageById(Long chatId, Long id) {
        TdApi.GetChatHistory tdApiFun = new TdApi.GetChatHistory(chatId, id, -1, 1, false);
        TdApi.Messages responseTdapiObject = sendAndWaitAns(tdApiFun, new TdApi.Messages());

        return ((TdApi.MessagePhoto) responseTdapiObject.messages[0].content).caption.text;
    }

}
