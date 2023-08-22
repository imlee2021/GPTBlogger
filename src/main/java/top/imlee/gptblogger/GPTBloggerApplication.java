package top.imlee.gptblogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.imlee.gptblogger.service.ITitleService;

@SpringBootApplication
public class GPTBloggerApplication implements ApplicationRunner {
    @Autowired
    private ITitleService titleService;

    public static void main(String[] args) {
        SpringApplication.run(GPTBloggerApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        titleService.generateArticle();
    }
}
