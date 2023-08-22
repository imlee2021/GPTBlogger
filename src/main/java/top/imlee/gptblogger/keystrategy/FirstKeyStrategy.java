package top.imlee.gptblogger.keystrategy;

import com.unfbx.chatgpt.function.KeyStrategyFunction;

import java.util.List;

public class FirstKeyStrategy implements KeyStrategyFunction<List<String>, String> {

    @Override
    public String apply(List<String> apiKeys) {
        return apiKeys.get(0);
    }
}