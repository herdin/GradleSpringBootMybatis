package com.harm.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harm.app.dto.request.CardRequest;
import com.harm.app.dto.request.EventRequest;
import com.harm.app.dto.request.TransactionRequest;
import com.harm.app.dto.request.UserReqeust;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

@Service
public class RemoteAPIService {
    private Logger logger = LoggerFactory.getLogger(RemoteAPIService.class);
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    private enum SERVICE_ENDPOINT {
        LOGIN(HttpMethod.POST, "http://msa.member.anmani.link:8090/member/login/"),
        GET_USER_CARD(HttpMethod.GET, "http://msa.card.anmani.link:8090/card/user/"),
        PUT_USER_CARD(HttpMethod.PUT, "http://msa.card.anmani.link:8090/card")

        ;
        HttpMethod httpMethod;
        String url;
        SERVICE_ENDPOINT(HttpMethod httpMethod, String url) {
            this.httpMethod = httpMethod;
            this.url = url;
        }
        public HttpMethod httpMethod() {
            return httpMethod;
        }
        public String url() {
            return url;
        }
    }

    public RemoteAPIService(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    @HystrixCommand(
            fallbackMethod = "fallbackMethod",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000"),
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

    public EventRequest getEvent(String url, Integer eventId) {
        logger.debug("call url {} with event id {}", url, eventId);
        String recvEventString = restTemplate.getForObject(url + "/" + eventId, String.class);
        logger.debug("recv event -> {}", recvEventString);
        EventRequest recvEventRequest = null;
        try {
            recvEventRequest = objectMapper.readValue(recvEventString, EventRequest.class);
        } catch (JsonProcessingException e) {
            logger.error("json parse error -> {}", e);
        }
        return recvEventRequest;
    }

    @HystrixCommand(
            fallbackMethod = "loginFallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "2000"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "30"),
            })
    public boolean login(UserReqeust userReqeust) {
        logger.debug("remote api call for login to [{}] user [{}]", SERVICE_ENDPOINT.LOGIN.url(), userReqeust.getUserId());
        boolean isLogined = false;
        try {
            ResponseEntity responseEntity = restTemplate.exchange(SERVICE_ENDPOINT.LOGIN.url() + userReqeust.getUserId() +"/" + bytesToHex(sha256(userReqeust.getUserPassword())), SERVICE_ENDPOINT.LOGIN.httpMethod(), null, String.class);
            logger.debug("remote api call for login status {} result {}", responseEntity.getStatusCode(), responseEntity.getBody());
            if(HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                isLogined = true;
            }
        } catch (RestClientException e) {
            logger.error(e.getMessage());
        }
        return isLogined;
    }

    public boolean loginFallback(UserReqeust userReqeust) {
        logger.debug("fallback called.");
        return false;
    }

    private byte[] sha256(String msg) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(msg.getBytes());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b: bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    @HystrixCommand(
            fallbackMethod = "getUserCardFallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "2000"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "30"),
            })
    public List<CardRequest> getUserCard(String userId) {
        logger.debug("remote api call for get user card to [{}] user [{}]", SERVICE_ENDPOINT.GET_USER_CARD.url(), userId);
        List<CardRequest> userCardList = null;
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(SERVICE_ENDPOINT.GET_USER_CARD.url() + userId, SERVICE_ENDPOINT.GET_USER_CARD.httpMethod(), null, String.class);

            logger.debug("remote api call for get user card status {} result {}", responseEntity.getStatusCode(), responseEntity.getBody());

            if(HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                CardRequest[] userCardArr = objectMapper.readValue(responseEntity.getBody(), CardRequest[].class);
                userCardList = Arrays.asList(userCardArr);
            }
        } catch(RestClientException re) {
            logger.error(re.getMessage());
        } catch (JsonProcessingException je) {
            logger.error(je.getMessage());
        }
        return userCardList;
    }
    public List<CardRequest> getUserCardFallback(String userId) {
        logger.debug("fallback called.");
        return Arrays.asList(new CardRequest[]{});
    }

    @HystrixCommand(
            fallbackMethod = "putUserCardFallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "2000"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "30"),
            })
    public boolean putUserCard(CardRequest cardRequest) {
        logger.debug("remote api call for put user card to [{}] user [{}]", SERVICE_ENDPOINT.GET_USER_CARD.url(), cardRequest.getUserId());
        boolean isPutOk = false;
        try {
            RequestEntity<CardRequest> requestEntity = RequestEntity
                    .put(new URI(SERVICE_ENDPOINT.PUT_USER_CARD.url()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(cardRequest)
                    ;
            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
            logger.debug("remote api call for put user card status {} result {}", responseEntity.getStatusCode(), responseEntity.getBody());

            if(HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                isPutOk = true;
            }
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
        }
        return isPutOk;
    }

    public boolean putUserCardFallback(CardRequest cardRequest) {
        logger.debug("fallback called.");
        return false;
    }

    @HystrixCommand(
            fallbackMethod = "getCardTransactionFallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "2000"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "30"),
            })
    public List<TransactionRequest> getCardTransaction(String cardNo) {
        return null;
    }
    public List<TransactionRequest> getCardTransactionFallback(String cardNo) {
        logger.debug("fallback called.");
        return Arrays.asList(new TransactionRequest[]{});
    }
}
