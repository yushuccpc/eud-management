package cn.iocoder.yudao.module.edu.framework.web.config;

import cn.iocoder.yudao.framework.swagger.config.YudaoSwaggerAutoConfiguration;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * edu 模块的 web 组件的 Configuration
 *
 * @author edu-manage
 */
@Configuration(proxyBeanMethods = false)
public class EduWebConfiguration {

    /**
     * edu 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi eduGroupedOpenApi() {
        return YudaoSwaggerAutoConfiguration.buildGroupedOpenApi("edu");
    }

}