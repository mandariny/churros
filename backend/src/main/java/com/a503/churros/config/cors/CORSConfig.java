package com.a503.churros.config.cors;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class CORSConfig implements WebMvcConfigurer {

    private final long MAX_AGE_SECS = 3600;

    @Value("${app.cors.allowedOrigins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/*")
                .allowedOrigins("http://localhost:3000", "https://www.churros.site")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("")
                .allowCredentials(true)
                .maxAge(MAX_AGE_SECS);

//        registry.addMapping("/**")
//        .allowedOriginPatterns("*")
//                // .allowedOrigins("http://localhost:3000")
//                // .allowedOrigins("http://localhost:9999")
//                // .allowedOrigins("https://www.churros.site")
//                // .allowedOrigins("https://churros.site")
//                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .allowCredentials(true)
//                .maxAge(MAX_AGE_SECS);
    }
}
