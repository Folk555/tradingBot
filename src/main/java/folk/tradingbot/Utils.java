package folk.tradingbot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    private static Logger LOGGER = LogManager.getLogger(Utils.class);
    public static String printPromptAndGetAns(String prompt) {
        System.out.print(prompt);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String str = "";
        try {
            str = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void sleep(int seconds) {
        if (seconds > 60) {
            LOGGER.error("Возникло исключение в методе sleep? А именно " +
                    "медот Sleep будет ждать больше минуты. Не очень хорошо");
            throw new RuntimeException("Медот Sleep будет ждать больше минуты. Не очень хорошо");
        }
        try {
            LOGGER.trace("Спим {} секунд", seconds);
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String findGroupFromRegax(String text, String regax, int groupNum) {
        try {
            Pattern pattern = Pattern.compile(regax, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(text);
            if (!matcher.find()) {
                throw new IllegalArgumentException("Паттерн '" + regax + "' не был найден в тексте");
            }
            return matcher.group(groupNum);
        } catch (Exception e) {
            LOGGER.error("Ошибка в  findGroupFromRegax. Text: '{}', Regex: '{}', Group: {}",
                    text, regax, groupNum, e);
            throw e;
        }
    }
}
