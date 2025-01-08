package folk.tradingbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
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
        if (seconds > 60)
            throw new RuntimeException("Медот Sleep будет ждать больше минуты. Не очень хорошо");
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String findGroupFromRegax(String text, String regax, int groupNum) {
        Pattern pattern = Pattern.compile(regax, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(text);
        boolean matches = matcher.matches();
        return matcher.group(groupNum);
    }
}
