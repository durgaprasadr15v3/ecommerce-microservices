package com.demo.order.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import com.demo.order.config.*;

@Service
public class RestTempleteCall {

    String url= "http://localhost:8082/api/products/welcome";

    //@Autowired
    private RestTemplate restTemplate;
    public RestTempleteCall(RestTemplate restTemplate)
    {
        this.restTemplate = restTemplate;
    }



    public String restTemplateCall(){
        String result = restTemplate.getForObject(url, String.class);
        return result;

    }

}
