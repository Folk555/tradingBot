package folk.tradingbot;

import folk.tradingbot.telegram.TelegramClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionHandler {
    @Autowired
    TelegramClient telegramClient;

    private static Logger LOGGER = LogManager.getLogger(ExceptionHandler.class);

    @AfterThrowing(pointcut = "execution(* folk.tradingbot.*.*(..))", throwing = "ex")
    public void handleException(Exception ex) {
        String message = "Произошло серьезное исключение\n" + ex;
        LOGGER.error(message);
        telegramClient.sendMessageToMainChat(message);
    }
}
