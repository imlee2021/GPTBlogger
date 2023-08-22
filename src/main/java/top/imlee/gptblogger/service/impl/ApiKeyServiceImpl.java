package top.imlee.gptblogger.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.imlee.gptblogger.dao.ApiKeyDao;
import top.imlee.gptblogger.pojo.ApiKey;
import top.imlee.gptblogger.service.IApiKeyService;

@Service
public class ApiKeyServiceImpl extends ServiceImpl<ApiKeyDao, ApiKey> implements IApiKeyService {
}
