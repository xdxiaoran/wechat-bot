package com.harry.wechat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by Harry on 2017/3/8.
 * With Intelli IDEA
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Value("${swagger.name}")
    private String title;
    @Value("${swagger.desc}")
    private String description;
    @Value("${swagger.version}")
    private String version;
    @Value("${swagger.termsOfServiceUrl}")
    private String termsOfServiceUrl;
    @Value("${swagger.contact.name}")
    private String contactName;
    @Value("${swagger.contact.url}")
    private String contactUrl;
    @Value("${swagger.contact.email}")
    private String contactEmal;
    @Value("${swagger.basePackage}")
    private String basePackage;

    @Bean
    public Docket swaggerPlugin() {
        ApiInfo apiInfo = new ApiInfo(title, description, version, null, new Contact(contactName,contactUrl,contactEmal), null, null);
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo).useDefaultResponseMessages(false);
    }
}
