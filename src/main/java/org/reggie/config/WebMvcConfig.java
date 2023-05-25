package org.reggie.config;

import lombok.extern.slf4j.Slf4j;
import org.reggie.common.JacksonObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Configuration
@Slf4j
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 静态资源映射方法，将静态页面能够通过springboot框架在浏览器展示
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始静态资源映射：");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 扩展MVC框架的消息转换器，此时主要作用是 将Long类型的Employee.id转为String以免丢失精度
     * @param converters 转换器集合，自定义出来的converter要追加其中
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器@WebMvcConfig");
        // new一个自定义消息转换器，将返回的对象R转为json并响应给页面（本来默认就有消息转换器，但是这里需要“将Long类型的Employee.id转为String以免丢失精度”
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 设置自定义消息转换器，底层使用Jackson将Java转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        // 将自定义消息转换器对象追加到MVC框架的 转换器 容器，放在最前面（索引为0）
        converters.add(0, messageConverter);
    }
}
