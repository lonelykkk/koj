package com.xiao.xoj.config;


import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;



/**
 * @author 肖恩
 * @create 2023/3/21 14:59
 * @description: swagger-ui
 */
@Configuration
@EnableSwagger2
//@EnableSwagger2WebMvc
public class SwaggerConfig {

    // back http://localhost:9001/swagger-ui.html
    // judge http://localhost:9003/swagger-ui.html
//    http://127.0.0.1:9001/doc.html
    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi")
                .apiInfo(webApiInfo())
                .select()
                .paths(Predicates.not(PathSelectors.regex("/admin/.*")))
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build();
    }

    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                .title("xoj毕业设计API文档")
                .description("本文档描述了xoj毕业设计接口定义")
                .version("1.0")
                .contact(new Contact("xiao en", "http://xiaoen.com",
                        "3219405547@qq.com"))
                .build();
    }

}
