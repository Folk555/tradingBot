package folk.tradingbot.telegram;

import folk.tradingbot.Utils;
import folk.tradingbot.telegram.handlers.TelegramResultHandler;
import folk.tradingbot.telegram.handlers.TelegramResultObjectHandler;
import folk.tradingbot.telegram.handlers.TelegramUpdateHandler;
import folk.tradingbot.telegram.models.OrderedChat;
import folk.tradingbot.telegram.models.TelegramChat;
import folk.tradingbot.telegram.models.TelegramUser;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Обертка над оригинальным телеграмм клиентом TDLib
 * клиент для работы с телеграммом
 */
public class TelegramClient {

    private final TelegramUpdateHandler updateHandler;
    private final Client client;
    private ArrayList<TelegramChat> chatList = new ArrayList<>();
    private TelegramUser myUser;

    /**
     * Не используй этот конструктор, он нужен для юнит тестов
     */
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
        if (limit > chatOrderList.size()) {
            TdApi.LoadChats tdApiFun = new TdApi.LoadChats(new TdApi.ChatListMain(), limit - chatList.size());
            TdApi.Ok res = sendAndWaitAns(tdApiFun, new TdApi.Ok());
            return getMainChatList(limit);
        }

        java.util.Iterator<OrderedChat> iter = chatOrderList.iterator();
        for (int i = 0; i < limit && i < chatOrderList.size(); i++) {
            long chatId = iter.next().getChatId();
            TdApi.Chat chat = updateHandler.getChats().get(chatId);
            synchronized (chat) {
                chatList.add(new TelegramChat(chat));
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
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(Long targetChatId, String msg) {
        TdApi.FormattedText text = new TdApi.FormattedText(msg, null);
        TdApi.InputMessageContent content = new TdApi.InputMessageText(text, null, true);
        TdApi.SendMessage query = new TdApi.SendMessage(458696774, 0, null,
                null, null, content);
        client.send(query, null);
    }

}
