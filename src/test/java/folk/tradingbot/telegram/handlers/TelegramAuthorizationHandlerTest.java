package folk.tradingbot.telegram.handlers;

import org.drinkless.tdlib.TdApi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TelegramAuthorizationHandlerTest {

    TelegramAuthorizationHandler telegramAuthorizationHandler = new TelegramAuthorizationHandler();

    @Test
    void onResult_receiveOk_nothing() {
        TdApi.Ok ok = new TdApi.Ok();
        Assertions.assertDoesNotThrow(() -> telegramAuthorizationHandler.onResult(ok));
    }

    @Test
    void onResult_receiveError_printError() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setErr(new PrintStream(baos));
        TdApi.Error error = new TdApi.Error();

       telegramAuthorizationHandler.onResult(error);
       Assertions.assertTrue(baos.toString().contains("Receive an error:"));
    }

    @Test
    void onResult_receiveNotErrorNotOk_throwException() {
        TdApi.User user = new TdApi.User();

        RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> telegramAuthorizationHandler.onResult(new TdApi.User()));
        Assertions.assertTrue(runtimeException.getMessage().contains("Receive wrong response from TDLib:"));
    }
}