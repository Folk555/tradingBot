package folk.tradingbot.telegram.handlers;

import lombok.Getter;
import org.drinkless.tdlib.TdApi;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * Хендлер для обработки ответов по api от телеграмма.
 * Класс был создан с целью убрать дубли когда на случай неожиданных ответов и получения желаемого тиа ответа
 * @param <T> тип ответа который мы хотим получить, например TdApi.User
 */

public class TelegramResultObjectHandler<T extends TdApi.Object> implements TelegramResultHandler {

    @Getter
    private T responseTdapiObject;
    private final int constructor;
    private final Condition condition;
    private final Lock lock;

    public TelegramResultObjectHandler(int constructor, Lock lock, Condition condition) {
        this.constructor = constructor;
        this.condition = condition;
        this.lock = lock;
    }

    @Override
    public void onResult(TdApi.Object object) {
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                if (object.getConstructor() == constructor) {
                    responseTdapiObject = (T) object;
                } else {
                    System.err.println("Receive wrong response from TDLib: " + object);
                }
                condition.signalAll();
            } else {
                System.out.println("!!!!!!!!!!!!!ВНИМАНИЕ!!!!!!!");
                System.out.println("Возникло исключение в методе onResult");
                throw new RuntimeException("Не получилось синхронизировать поток в TelegramResultObjectHandler.onResult");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
