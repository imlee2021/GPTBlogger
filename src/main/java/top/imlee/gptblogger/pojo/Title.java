package top.imlee.gptblogger.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("title")
public class Title {
    private Integer id;
    private String value;
}

