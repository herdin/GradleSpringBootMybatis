package com.harm.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harm.app.dto.request.EventRequest;
import com.harm.app.dto.request.TransactionRequest;
import com.harm.app.dto.request.UserReqeust;
import com.harm.app.service.EventService;
import com.harm.app.service.RemoteAPIService;
import com.harm.app.service.UserService;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/*
TODO
    - RestTemplate 을 사용한 서비스 연동
    - 더 나은 RestTemplate 사용
        - https://taetaetae.github.io/2020/03/22/better-rest-template-1-retryable/
        - https://taetaetae.github.io/2020/03/29/better-rest-template-2-netflix-hystrix/
*/
@RestController
public class SampleController {
    private Logger logger = LoggerFactory.getLogger(SampleController.class);
    private ObjectMapper objectMapper;
    private UserService userService;
    private RemoteAPIService remoteAPIService;
    private RestHighLevelClient restHighLevelClient;
    private EventService eventService;
    public SampleController(
            ObjectMapper objectMapper
            , UserService userService
            , RemoteAPIService remoteAPIService
            , RestHighLevelClient restHighLevelClient
            , EventService eventService
    ) {
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.remoteAPIService = remoteAPIService;
        this.restHighLevelClient = restHighLevelClient;
        this.eventService = eventService;
    }

//    @ExceptionHandler(UnknownHostException.class)
//    public ResponseEntity<String> exceptionHandler(UnknownHostException e) {
//        return ResponseEntity.badRequest().body(e.getMessage());
//    }

    @CrossOrigin(origins = "*")
    @GetMapping("/hello")
    public String hello() throws UnknownHostException {
        return "hello, v3.0 msa-example, from " + InetAddress.getLocalHost().toString();
    }

    @GetMapping("/remoteHello")
    public String remoteHello(String url) throws URISyntaxException {
        logger.debug("remote hello -> {}", url);
        return remoteAPIService.getStringResult(url);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<EventRequest> event(@PathVariable Integer eventId) {
        logger.debug("event id -> {}", eventId);
        Optional<EventRequest> eventRequest = Optional.ofNullable(eventService.getEventById(eventId));
        logger.debug("event repository get event by event id {} is {}", eventId, eventRequest.isPresent()? eventRequest:"null");
        if(eventRequest.isPresent()) {
            return ResponseEntity.ok().body(eventRequest.get());
        } else {
            return ResponseEntity.badRequest().body(new EventRequest());
        }
    }

    @GetMapping("/remoteEvent")
    public ResponseEntity<EventRequest> remoteEvent(@RequestParam String url, @RequestParam Integer eventId) {
        logger.debug("remote event url -> {}", eventId);
        Optional<EventRequest> recvEventRequest = Optional.ofNullable(remoteAPIService.getEvent(url, eventId));
        logger.debug("remote event repository get event by event id {} is {}", eventId, recvEventRequest.isPresent()? recvEventRequest.get():"null");
        if(recvEventRequest.isPresent()) {
            return ResponseEntity.ok().body(recvEventRequest.get());
        } else {
            return ResponseEntity.badRequest().body(new EventRequest());
        }
    }

    @PutMapping("/session")
    public ResponseEntity<Integer> putSession(@Validated @RequestBody UserReqeust userReqeust, BindingResult bindingResult, HttpSession httpSession) {
        logger.debug("recv user -> {}", userReqeust);
        if(bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(-1);
        } else {
            httpSession.setAttribute("userId", userReqeust.getUserId());
            httpSession.setAttribute("userPassword", userReqeust.getUserPassword());
            return ResponseEntity.ok().body(0);
        }
    }

    @GetMapping("/session")
    public ResponseEntity<UserReqeust> getSession(@SessionAttribute(required = false) String userId, @SessionAttribute(required = false) String userPassword) {
        logger.debug("from session, user id {}, user password {}", userId, userPassword);
        return ResponseEntity.ok().body(new UserReqeust(userId, userPassword));
    }

    @GetMapping("/estest")
    public String estest() throws IOException {
//        estest_04();
        estest_03();
        estest_02();
        return "estest";
    }

    public RestHighLevelClient createConnection() {
//        return new RestHighLevelClient(
//                RestClient.builder(
//                        new HttpHost("ec2-52-78-204-9.ap-northeast-2.compute.amazonaws.com",9200,"http")
//                )
//        );
        return restHighLevelClient;
    }

    //회원가입
    @PostMapping("/user/{id}/{name}")
    public ResponseEntity<UserReqeust> addUser(@PathVariable  String id, @PathVariable  String name) {
        logger.debug("this is recv -> {}, {}", id, name);
        //회원 insert
        ///
        boolean isOk = userService.addUser(new UserReqeust());
        if(isOk) {
            UserReqeust userReqeust = new UserReqeust(id+1, name);
            return ResponseEntity.ok(userReqeust);
//            return ResponseEntity.status(200).body()
        } else {
            return ResponseEntity.badRequest().body(new UserReqeust());
        }
    }

    @GetMapping("/user/detail")
    public String addUser2(@ModelAttribute UserReqeust userReqeust) {
        logger.debug("this is recv -> {}", userReqeust);
        return "hello, " + userReqeust.toString();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserReqeust> getUser(@PathVariable String id) {
        //서비스 에서 디비조회했다 치고
        UserReqeust userReqeust = new UserReqeust(id, "get user 입니다.");
        return ResponseEntity.ok(userReqeust);
    }


    /**
     * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/5.6/java-rest-high-search.html
     */
    private void estest_04() throws IOException {
        logger.debug("estest_04 init");
        SearchRequest searchRequest = new SearchRequest("sjb");
        /**
         *      * @deprecated Types are in the process of being removed. Instead of using a type, prefer to
         *      * filter on a field on the document.
         */
//        searchRequest.types("trans");

        QueryBuilder queryBuilder = QueryBuilders.matchQuery("card_no", "1010010030654487")
                .lenient(false)
                .fuzziness(Fuzziness.ZERO) //연관도라고한다..
                .prefixLength(3)
                .maxExpansions(10);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        sourceBuilder.query(QueryBuilders.termQuery("card_no", "1010010030654487"));
        sourceBuilder.query(queryBuilder);
        sourceBuilder.from(0);
        sourceBuilder.size(5);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = createConnection().search(searchRequest, RequestOptions.DEFAULT);

        logger.debug("status {}, took {} terminatedEarly {}, timeout {}", searchResponse.status(), searchResponse.getTook(), searchResponse.isTerminatedEarly(), searchResponse.isTimedOut());

        SearchHits hits = searchResponse.getHits();
        ArrayList<TransactionRequest> transModels = new ArrayList<>();
        for(SearchHit hit : hits) {
            String hitSource = hit.getSourceAsString();
            TransactionRequest transModel = objectMapper.readValue(hitSource, TransactionRequest.class);
            transModels.add(transModel);
            logger.debug("hit source -> {}", hitSource);
            logger.debug("hit -> {}", transModel);
        }

        logger.debug("estest_04 -> {}", searchResponse);
    }
    /**
     * https://coding-start.tistory.com/172
     */
    private void estest_03() throws IOException {
        logger.debug("estest_03 init");
        SearchRequest searchRequest = new SearchRequest("sjb");
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("card_no", "1010010131588568");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(queryBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = createConnection().search(searchRequest, RequestOptions.DEFAULT);

        logger.debug("status {}, took {} terminatedEarly {}, timeout {}", searchResponse.status(), searchResponse.getTook(), searchResponse.isTerminatedEarly(), searchResponse.isTimedOut());

        SearchHits hits = searchResponse.getHits();
        ArrayList<TransactionRequest> transModels = new ArrayList<>();
        for(SearchHit hit : hits) {
            String hitSource = hit.getSourceAsString();
            TransactionRequest transModel = objectMapper.readValue(hitSource, TransactionRequest.class);
            transModels.add(transModel);
            logger.debug("hit source -> {}", hitSource);
            logger.debug("hit -> {}", transModel);
        }

        logger.debug("estest-03 -> {}", searchResponse);
    }
    private void estest_02() throws IOException {
        logger.debug("estest_02 init");
        String aliasName = "sjb";
        String typeName = "trans";
        String fieldName = "card_no";

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery(fieldName, "1010010030654487"));
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(5);
//        searchSourceBuilder.sort(new FieldSortBuilder(fieldName).order(SortOrder.DESC));

        SearchRequest request = new SearchRequest(aliasName);
        request.types(typeName);
        request.source(searchSourceBuilder);


        SearchResponse response = null;
        SearchHits searchHits = null;
//        List<Answer> resultMap = new ArrayList<>();

        try(RestHighLevelClient restHighLevelClient = createConnection()) {

            response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            searchHits = response.getHits();
            for( SearchHit hit : searchHits) {
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//                Answer a = new Answer();
//                a.setQuestion(sourceAsMap.get("question")+"");
//                a.setAnswer(sourceAsMap.get("answer")+"");
//                resultMap.add(a);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("estest-02 -> {}", response);
    }

    /**
     * https://nevercaution.github.io/elasticsearch-rest-client/
     */
    private void estest_01() throws IOException {
        RestHighLevelClient client = null;
        CreateIndexRequest request = new CreateIndexRequest("sjb");
//        request.settings(seriesSettings(), XContentType.JSON);
//        request.mapping("type_name", seriesIndex(), XContentType.JSON);
        client.indices().create(request, RequestOptions.DEFAULT);
    }

//    @GetMapping("/event")
//    public Event event() throws UnknownHostException {
//        Event newEvent = new Event("", 0, InetAddress.getLocalHost().toString(), LocalDateTime.now());
//        return newEvent;
//    }
//
//    @GetMapping("/remoteEvent")
//    public Event remoteEvent(@RequestBody Event event) throws URISyntaxException {
//        logger.debug("remote event -> {}", event);
//        RequestEntity<Event>.
//        ResponseEntity<Event> responseEntity = restTemplate.exchange(new URI(event.getUrl()), HttpMethod.GET, null, Event.class);
//        return responseEntity.getBody();
//    }
}
