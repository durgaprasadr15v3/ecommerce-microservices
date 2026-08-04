package com.demo.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplete {

    @Bean
    public RestTemplate restTempleteMethod()
    {
       // RestTemplete restTemplate = new RestTemplate();
        return new RestTemplate();
    }

}
