package com.example.interview_ai.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")

                // ✅ Allowed Frontend URLs
                .allowedOrigins(
                        "http://localhost:3000",
                        "https://mock-interview-x-frontend-s1pi.vercel.app"
                )

                // ✅ Methods allowed
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                // ✅ Headers allowed
                .allowedHeaders("*")

                // ✅ Important for cookies / auth
                .allowCredentials(true);
    }
}