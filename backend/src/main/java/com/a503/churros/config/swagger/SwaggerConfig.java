package com.a503.churros.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    // Swagger - API 기본 설정
    private String API_VERSION = "1.0";
    private String API_TITLE = "Churros Project";
    private String API_DESCRIPTION = "A503 API";;


    public Docket getDocket(String groupName, boolean defaultResponseMessage,
                            // Predicate<String> predicate,
                            String basePackage) {

        return new Docket(DocumentationType.OAS_30).groupName(groupName)
                .useDefaultResponseMessages(defaultResponseMessage) // Swagger 에서 제공해주는 기본 응답 코드 표시 여부
                .apiInfo(apiInfo()) // apiInfo정보
                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.a503.churros.controller." + basePackage))
                .paths(PathSelectors.any()) // 아무 경로나 가능
                .build();
    }

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder().title(API_TITLE).description(API_DESCRIPTION).version(API_VERSION).build();
    }

    private ApiKey apiKey() {
        return new ApiKey("Authorization", "Authorization", "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(Arrays.asList(defaultAuth()))
                .build();
    }
    private SecurityReference defaultAuth() {
        return new SecurityReference("apiKey", new AuthorizationScope[0]);
    }
    // DEVELOPTING
    @Bean
    public Docket scarp() {
        return getDocket("SCARP", true, "scrap");
    }
    @Bean
    public Docket news() {
        return getDocket("NEWS", true, "news");
    }

    @Bean
    public Docket auth() {
        return getDocket("AUTH", true, "auth");
    }

    @Bean
    public Docket test() {
        return getDocket("TEST", true, "test");
    }

}