package com.harm.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harm.app.dto.request.CardRequest;
import com.harm.app.dto.request.EventRequest;
import com.harm.app.dto.request.TransactionRequest;
import com.harm.app.dto.request.UserReqeust;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class RemoteAPIService {
    private Logger logger = LoggerFactory.getLogger(RemoteAPIService.class);
    private RestTemplate restTemplate;
    private RestHighLevelClient restHighLevelClient;
    private ObjectMapper objectMapper;

    private enum SERVICE_ENDPOINT {
        //GET     http://msa.card.anmani.link:8090/card/member/{memberId}
        //PUT     http://msa.card.anmani.link:8090/card/{cardNo}/member/{memberId}
        //DELETE  http://msa.card.anmani.link:8090/card/{cardNo}/member/{memberId}

    }

    public RemoteAPIService(RestHighLevelClient restHighLevelClient, ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.restHighLevelClient = restHighLevelClient;
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
        final String memberIdSlot = "#memberId#";
        final String memberPasswordSlot = "#memberPassword#";
        String requestUrlStr = "http://msa.member.anmani.link:8090/member/login/" + memberIdSlot + "/" + memberPasswordSlot;

        logger.debug("remote api call for login to [{}] user [{}]", requestUrlStr, userReqeust.getUserId());
        boolean isLogined = false;
        try {

            requestUrlStr = requestUrlStr.replaceAll(memberIdSlot, userReqeust.getUserId());
            requestUrlStr = requestUrlStr.replaceAll(memberPasswordSlot, bytesToHex(sha256(userReqeust.getUserPassword())));

            ResponseEntity responseEntity = restTemplate.exchange(requestUrlStr, HttpMethod.POST, null, String.class);
            logger.debug("remote api call for login status {} result {}", responseEntity.getStatusCode(), responseEntity.getBody());
            if(HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                isLogined = true;
            }
        } catch (RestClientException e) {
            logger.error(e.getMessage());
            throw new RemoteAccessException(e.getMessage());
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
    public List<CardRequest> getUserCard(String memberId) {
        final String memberIdSlot = "#memberId#";
        String requestUrlStr = "http://msa.card.anmani.link:8090/card/member/" + memberId;

        logger.debug("remote api call for get user card to [{}] user [{}]", requestUrlStr, memberId);
        List<CardRequest> userCardList = null;
        try {

            requestUrlStr = requestUrlStr.replaceAll(memberIdSlot, memberId);

            ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrlStr, HttpMethod.GET, null, String.class);
            logger.debug("remote api call for get user card status {} result {}", responseEntity.getStatusCode(), responseEntity.getBody());

            if(HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                CardRequest[] userCardArr = objectMapper.readValue(responseEntity.getBody(), CardRequest[].class);
                userCardList = Arrays.asList(userCardArr);
            }
        } catch(RestClientException re) {
            logger.error(re.getMessage());
            throw new RemoteAccessException(re.getMessage());
        } catch (JsonProcessingException je) {
            logger.error(je.getMessage());
            throw new RemoteAccessException(je.getMessage());
        }
        return userCardList;
    }
    public List<CardRequest> getUserCardFallback(String userId) {
        logger.debug("fallback called.");
        return Arrays.asList(new CardRequest[]{});
    }

    @HystrixCommand(
            fallbackMethod = "modUserCardFallback",
            commandProperties = {
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "1000"),
                    @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
                    @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "2000"),
                    @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "30"),
            })
    public boolean modUserCard(CardRequest cardRequest, boolean isPut) {
        final String cardNoSlot = "#cardNo#";
        final String memberIdSlot = "#memberId#";
        String requestUrlStr = "http://msa.card.anmani.link:8090/card/" + cardNoSlot + "/member/" + memberIdSlot;

        logger.debug("remote api call for {} user card to [{}] user [{}]", (isPut)? "put":"delete", requestUrlStr, cardRequest.getUserId());

        boolean isModOk = false;
        try {
//            RequestEntity<CardRequest> requestEntity = RequestEntity
//                    .put(new URI(url))
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .body(cardRequest)
//                    ;
//            ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

            requestUrlStr = requestUrlStr.replaceAll(cardNoSlot, cardRequest.getCardNo());
            requestUrlStr = requestUrlStr.replaceAll(memberIdSlot, cardRequest.getUserId());

            ResponseEntity<String> responseEntity = restTemplate.exchange(requestUrlStr, (isPut)? HttpMethod.PUT:HttpMethod.DELETE,null, String.class);
            logger.debug("remote api call for {} user card status {} result {}", (isPut)? "put":"delete", responseEntity.getStatusCode(), responseEntity.getBody());

            if(HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                isModOk = true;
            }
        } catch (RestClientException e) {
            logger.error(e.getMessage());
            throw new RemoteAccessException(e.getMessage());
        }
        return isModOk;
    }

    public boolean modUserCardFallback(CardRequest cardRequest, boolean isPut) {
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
        ArrayList<TransactionRequest> transModels = new ArrayList<>();

        SearchRequest searchRequest = new SearchRequest("sjb");
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("card_no", cardNo  );
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RemoteAccessException(e.getMessage());
//            return transModels;
        }

        logger.debug("status {}, took {} terminatedEarly {}, timeout {}", searchResponse.status(), searchResponse.getTook(), searchResponse.isTerminatedEarly(), searchResponse.isTimedOut());

        SearchHits hits = searchResponse.getHits();
        for(SearchHit hit : hits) {
            String hitSource = hit.getSourceAsString();
            TransactionRequest transModel = null;
            try {
                transModel = objectMapper.readValue(hitSource, TransactionRequest.class);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage());
                throw new RemoteAccessException(e.getMessage());
//                return transModels;
            }
            transModels.add(transModel);
            logger.debug("hit source -> {}", hitSource);
            logger.debug("hit -> {}", transModel);
        }

        return transModels;
    }
    public List<TransactionRequest> getCardTransactionFallback(String cardNo) {
        logger.debug("fallback called.");
        List<TransactionRequest> duumyList = IntStream.range(0, 20).mapToObj(i -> new TransactionRequest(
                "dummy-card-no-" + i,
                "dummy-tr-dtime-" + i,
                "dummy-vehc-id-" + i,
                "dummy-route-id-" + i,
                "dummy-tr-amt-" + i,
                "dummy-tr-type-" + i
                )).collect(Collectors.toList());
        return duumyList;
    }
}
