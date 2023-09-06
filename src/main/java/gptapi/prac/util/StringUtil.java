package gptapi.prac.util;

public class StringUtil {

    public static String slice(String input, int startIndex) {
        int length = input.length();
        if (startIndex < 0) {
            startIndex = length + startIndex;
        }

        if (startIndex < 0) {
            startIndex = 0;
        }

        return input.substring(startIndex);
    }

}
