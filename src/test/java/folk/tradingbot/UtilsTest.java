package folk.tradingbot;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;
import java.time.Instant;

class UtilsTest {

    @Test
    void printPromptAndGetAns_shouldPrintPrompt() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        ByteArrayInputStream bais = new ByteArrayInputStream("Hi Man".getBytes());
        System.setIn(bais);

        String ans = Utils.printPromptAndGetAns("Hello World");

        Assertions.assertEquals("Hello World", baos.toString());
        Assertions.assertEquals("Hi Man", ans);
    }

    @Test
    void sleep_less60seconds_success() {
        Instant start = Instant.now();
        Utils.sleep(2);
        Instant finish = Instant.now();
        long time = Duration.between(start, finish).toSeconds();

        Assertions.assertEquals(2, time);
    }

    @Test
    void sleep_grater60seconds_success() {
        RuntimeException runtimeException =
                Assertions.assertThrows(RuntimeException.class, () -> Utils.sleep(61));

        Assertions.assertTrue(runtimeException.getMessage()
                .contains("Медот Sleep будет ждать больше минуты. Не очень хорошо"));
    }
}