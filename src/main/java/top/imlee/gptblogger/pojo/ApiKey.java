package top.imlee.gptblogger.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("apikey")
public class ApiKey {
    private Integer id;
    private String value;
}
