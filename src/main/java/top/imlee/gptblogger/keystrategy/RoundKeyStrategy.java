package top.imlee.gptblogger.keystrategy;

import com.unfbx.chatgpt.function.KeyStrategyFunction;

import java.util.List;

public class RoundKeyStrategy implements KeyStrategyFunction<List<String>, String> {
    public static int currentIndex = 0;
    public static String key = null;

    @Override
    public String apply(List<String> apiKeys) {
        key = apiKeys.get(currentIndex);
        currentIndex = (currentIndex + 1) % apiKeys.size();
        return key;
    }
}
