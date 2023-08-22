package top.imlee.gptblogger.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.imlee.gptblogger.pojo.ApiKey;

@Mapper
public interface ApiKeyDao extends BaseMapper<ApiKey> {
}
