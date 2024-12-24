package folk.tradingbot.telegram.models;

import lombok.Getter;
import org.drinkless.tdlib.TdApi;

/**
 * копипаст из примера-проекта
 * класс отвечает за представление классической упорядоченности чатов
 */
@Getter
public class OrderedChat implements Comparable<OrderedChat> {
    private final long chatId;
    private final TdApi.ChatPosition position;

    public OrderedChat(long chatId, TdApi.ChatPosition position) {
        this.chatId = chatId;
        this.position = position;
    }

    @Override
    public int compareTo(OrderedChat o) {
        if (this.position.order != o.position.order) {
            return o.position.order < this.position.order ? -1 : 1;
        }
        if (this.chatId != o.chatId) {
            return o.chatId < this.chatId ? -1 : 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        OrderedChat o = (OrderedChat) obj;
        return this.chatId == o.chatId && this.position.order == o.position.order;
    }
}