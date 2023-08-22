package top.imlee.gptblogger.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import top.imlee.gptblogger.dao.TitleDao;
import top.imlee.gptblogger.keystrategy.RoundKeyStrategy;
import top.imlee.gptblogger.pojo.ApiKey;
import top.imlee.gptblogger.pojo.Title;
import top.imlee.gptblogger.service.IApiKeyService;
import top.imlee.gptblogger.service.ITitleService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
@Configuration
public class TitleServiceImpl extends ServiceImpl<TitleDao, Title> implements ITitleService {
    @Autowired
    private OpenAiClient openAiClient;

    @Autowired
    private IApiKeyService apiKeyService;

    @Autowired
    private LinkedList<String> valueList;

    @Value("${gptblogger.prompt}")
    private String prompt;

    @Value("${gptblogger.url}")
    private String url;

    @Value("${gptblogger.api_key}")
    private String apiKey;

    public String uploadArticle(JSONObject jsonObject) {
        return HttpRequest.post(url).body(jsonObject.toString()).execute().body();
    }


    @Override
    public void generateArticle() {
        List<Title> list = this.list();
        for (Title title : list) {
            String value = title.getValue();
            log.info("当前标题：{}", value);
            try {
                Message message = Message.builder().role(Message.Role.USER).content(prompt + value).build();
                ChatCompletion chatCompletion = ChatCompletion.builder().messages(List.of(message)).model(ChatCompletion.Model.GPT_3_5_TURBO.getName()).build();
                ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion);
                chatCompletionResponse.getChoices().forEach(e -> {
                    String content = e.getMessage().getContent();
//                    log.info("生成的内容：{}", content);

                    JSONObject jsonObject = new JSONObject();
                    try {
                        String head = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
                        content = content.substring(content.indexOf(head) + head.length());
                        Document document = DocumentHelper.parseText(content);
                        Element rootElement = document.getRootElement();
                        Integer categoryId = Integer.parseInt(rootElement.element("categoryId").getText());
                        String title1 = rootElement.element("title").getText();
                        String tag = rootElement.element("tag").getText();
                        String summary = rootElement.element("summary").getText();
                        String content1 = rootElement.element("content").getText();
                        String seoKeywords = rootElement.element("seoKeywords").getText();
                        String seoDescription = rootElement.element("seoDescription").getText();
                        jsonObject.put("categoryId", categoryId);
                        jsonObject.put("title", title1);
                        jsonObject.put("tag", tag);
                        jsonObject.put("summary", summary);
                        jsonObject.put("content", content1);
                        jsonObject.put("seoKeywords", seoKeywords);
                        jsonObject.put("seoDescription", seoDescription);
                    } catch (DocumentException ex) {
                        log.error("生成的内容不是合法的 xml 格式：{}", ex.getMessage());
                        return;
                    }
                    jsonObject.put("ADMIN_API_KEY", apiKey);
                    jsonObject.put("isPublished", true);

                    LocalDateTime now = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDateTime = now.format(formatter);

                    jsonObject.put("postTime", formattedDateTime);
                    Random random = new Random();
                    jsonObject.put("clickCount", random.nextInt(2000));
                    jsonObject.put("isTop", false);

                    String result = uploadArticle(jsonObject);
                    log.info("上传结果：{}", result);
                    if (JSON.parseObject(result).getInteger("code") == 0) {
                        this.removeById(title.getId());
                    }
                });
            } catch (BaseException e) {
                log.error("生成内容失败：{}", e.getMessage());
                if (e.getMessage().startsWith("Incorrect API key provided") || e.getMessage().startsWith("You exceeded your current quota")) {
                    log.error("API key 无效，正在删除...");
                    LambdaQueryWrapper<ApiKey> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(ApiKey::getValue, RoundKeyStrategy.key);
                    apiKeyService.remove(queryWrapper);
                    valueList.remove(RoundKeyStrategy.key);
                    RoundKeyStrategy.currentIndex--;
                }
            } catch (Exception e) {
                log.error("未知异常：{}", e.getMessage());
            }

        }

    }
}
