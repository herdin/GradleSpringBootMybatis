package com.harm.app.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RemoteAPIService {
    private Logger logger = LoggerFactory.getLogger(RemoteAPIService.class);
    private RestTemplate restTemplate;
    public RemoteAPIService() {
        this.restTemplate = new RestTemplate();
    }
    @HystrixCommand(
            fallbackMethod = "fallbackMethod",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "500"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "2000"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "30"),
            })
    public String getStringResult(String url) {
        String result = restTemplate.getForObject(url, String.class);
        logger.debug("result -> {}", result);
        return result;
    }
    public String fallbackMethod(String url) {
        logger.debug("fallbackMethod, url : {}", url);
        return "defaultResult";
    }
}
