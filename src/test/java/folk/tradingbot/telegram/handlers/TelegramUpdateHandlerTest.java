package folk.tradingbot.telegram.handlers;

import folk.tradingbot.telegram.TelegramChatListenerService;
import folk.tradingbot.telegram.models.TelegramUpdateMessage;
import org.drinkless.tdlib.TdApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TelegramUpdateHandlerTest {

    @InjectMocks
    TelegramUpdateHandler handler = new TelegramUpdateHandler();
    @Mock
    TelegramChatListenerService service = Mockito.mock(TelegramChatListenerService.class);

    @Test
    void onResult_onNewMessage_shouldRunMessageListener() {
        Mockito.doNothing().when(service).processMessage(Mockito.any(TelegramUpdateMessage.class));
        handler.setChatListenerService(service);

        handler.onResult(new TdApi.UpdateNewMessage());

        Mockito.verify(service, Mockito.times(1))
                .processMessage(Mockito.any(TelegramUpdateMessage.class));
    }

}