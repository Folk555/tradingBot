package folk.tradingbot.telegram.handlers;

import org.drinkless.tdlib.TdApi;


public class TelegramAuthorizationHandler implements TelegramResultHandler {

    @Override
    public void onResult(TdApi.Object object) {
        switch (object.getConstructor()) {
            case TdApi.Error.CONSTRUCTOR:
                System.err.println("Receive an error:" + object);
                break;
            case TdApi.Ok.CONSTRUCTOR:
                break;
            default:
                System.out.println("!!!!!!!!!!!!!ВНИМАНИЕ!!!!!!!");
                System.out.println("Возникло исключение в методе onResult");
                throw new RuntimeException("Receive wrong response from TDLib: " + object);
        }
    }
}
