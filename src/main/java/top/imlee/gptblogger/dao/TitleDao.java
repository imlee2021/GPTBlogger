package top.imlee.gptblogger.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.imlee.gptblogger.pojo.Title;

@Mapper
public interface TitleDao extends BaseMapper<Title> {
}
