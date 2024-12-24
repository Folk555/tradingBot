package folk.tradingbot.telegram.handlers;

import org.drinkless.tdlib.TdApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TelegramResultObjectHandlerTest {

    private int constructor = TdApi.User.CONSTRUCTOR;
    @Mock
    private Lock lock = Mockito.mock(Lock.class);
    @Mock
    private Condition condition = Mockito.mock(Condition.class);

    @Test
    void onResult_paramEqualsConstructor_success() throws InterruptedException {
        TelegramResultObjectHandler<TdApi.User> telegramResultObjectHandler =
                new TelegramResultObjectHandler<>(constructor, lock, condition);
        Mockito.when(lock.tryLock(10, TimeUnit.SECONDS)).thenReturn(true);

        telegramResultObjectHandler.onResult(new TdApi.User());

        Mockito.verify(lock, Mockito.times(1)).tryLock(10, TimeUnit.SECONDS);
        Mockito.verify(condition, Mockito.times(1)).signalAll();
        Mockito.verify(lock, Mockito.times(1)).unlock();
    }

    @Test
    void onResult_paramNotEqualsConstructor_printError() throws InterruptedException {
        TelegramResultObjectHandler<TdApi.User> telegramResultObjectHandler =
                new TelegramResultObjectHandler<>(constructor, lock, condition);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setErr(new PrintStream(baos));
        Mockito.doReturn(true).when(lock).tryLock(10, TimeUnit.SECONDS);

        telegramResultObjectHandler.onResult(new TdApi.Chat());

        Mockito.verify(lock, Mockito.times(1)).tryLock(10, TimeUnit.SECONDS);
        Assertions.assertTrue(baos.toString().contains("Receive wrong response from TDLib:"));
        Mockito.verify(condition, Mockito.times(1)).signalAll();
        Mockito.verify(lock, Mockito.times(1)).unlock();
    }

    @Test
    void getResponseTdapiObject() throws InterruptedException {
        TelegramResultObjectHandler<TdApi.User> telegramResultObjectHandler =
                new TelegramResultObjectHandler<>(constructor, lock, condition);
        Mockito.doReturn(true).when(lock).tryLock(10, TimeUnit.SECONDS);
        TdApi.User user = new TdApi.User();
        user.usernames = new TdApi.Usernames(new String[]{"testName"}, null, null);
        telegramResultObjectHandler.onResult(user);

        TdApi.Object responseTdapiObject = telegramResultObjectHandler.getResponseTdapiObject();

        Assertions.assertNotNull(responseTdapiObject);
        assertInstanceOf(TdApi.User.class, responseTdapiObject);
        TdApi.User user1 = (TdApi.User) responseTdapiObject;
        Assertions.assertEquals(user1.usernames, user.usernames);

    }
}