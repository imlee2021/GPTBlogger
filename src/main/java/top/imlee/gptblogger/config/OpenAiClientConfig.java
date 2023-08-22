package top.imlee.gptblogger.config;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import com.unfbx.chatgpt.interceptor.OpenAiResponseInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.imlee.gptblogger.keystrategy.RoundKeyStrategy;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

@Configuration
public class OpenAiClientConfig {
    @Value("${gptblogger.connect_timeout}")
    private int connectTimeout;
    @Value("${gptblogger.write_timeout}")
    private int writeTimeout;
    @Value("${gptblogger.read_timeout}")
    private int readTimeout;
    @Value("${gptblogger.api_host}")
    private String apiHost;

    @Value("${gptblogger.http.logging.level}")
    private String httpLoggingLevel;

    @Autowired
    private LinkedList valueList;


    @Bean
    public OpenAiClient openAiClient() {
//        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("127.0.0.1", 10808));
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        // ！！！！千万别再生产或者测试环境打开BODY级别日志！！！！
        // ！！！生产或者测试环境建议设置为这三种级别：NONE,BASIC,HEADERS,！！！
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.valueOf(httpLoggingLevel));
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .proxy(proxy)// 自定义代理
                .addInterceptor(httpLoggingInterceptor)// 自定义日志输出
                .addInterceptor(new OpenAiResponseInterceptor())// 自定义返回值拦截
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)// 自定义超时时间
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)// 自定义超时时间
                .readTimeout(readTimeout, TimeUnit.SECONDS)// 自定义超时时间
                .build();


        // 构建客户端
        return OpenAiClient.builder().apiKey(valueList)
                // 自定义key的获取策略：默认KeyRandomStrategy
                .keyStrategy(new RoundKeyStrategy()).okHttpClient(okHttpClient)
                // 自己做了代理就传代理地址，没有可不不传
                .apiHost(apiHost).build();
    }
}
