package com.demo.product.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

import static java.util.Map.*;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> stringStringMap = new java.util.HashMap<>();
        stringStringMap.put("cloud_name", cloudName);
        stringStringMap.put("api_key", apiKey);
        stringStringMap.put("api_secret", apiSecret);
        return new Cloudinary(stringStringMap);
    }
}