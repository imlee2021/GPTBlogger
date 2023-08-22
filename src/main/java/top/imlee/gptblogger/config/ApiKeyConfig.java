package top.imlee.gptblogger.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.imlee.gptblogger.pojo.ApiKey;
import top.imlee.gptblogger.service.IApiKeyService;

import java.util.LinkedList;
import java.util.List;

@Configuration
public class ApiKeyConfig {
    @Autowired
    private IApiKeyService apiKeyService;

    @Bean
    public LinkedList<String> valueList() {
        List<ApiKey> apiKeyList = apiKeyService.list();
        LinkedList<String> valueList = new LinkedList<>();
        for (ApiKey apiKey : apiKeyList) {
            valueList.add(apiKey.getValue());
        }
        return valueList;
    }
}
