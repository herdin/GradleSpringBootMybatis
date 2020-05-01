package com.harm.app.controller;

import com.harm.app.dto.request.UserReqeust;
import com.harm.app.service.RemoteAPIService;
import com.harm.app.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

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
    public SampleController(ObjectMapper objectMapper, UserService userService, RemoteAPIService remoteAPIService) {
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.remoteAPIService = remoteAPIService;
    }

//    @ExceptionHandler(UnknownHostException.class)
//    public ResponseEntity<String> exceptionHandler(UnknownHostException e) {
//        return ResponseEntity.badRequest().body(e.getMessage());
//    }

    @GetMapping("/hello")
    public String hello() throws UnknownHostException {
        return "hello, " + InetAddress.getLocalHost().toString();
    }

    @GetMapping("/remoteHello")
    public String remoteHello(String url) throws URISyntaxException {
        logger.debug("remote hello -> {}", url);
        return remoteAPIService.getStringResult(url);
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



    //회원가입
    @PostMapping("/user/{id}/{name}")
    public ResponseEntity<UserReqeust> addUser(@PathVariable  int id, @PathVariable  String name) {
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
    public ResponseEntity<UserReqeust> getUser(@PathVariable int id) {
        //서비스 에서 디비조회했다 치고
        UserReqeust userReqeust = new UserReqeust(id, "get user 입니다.");
        return ResponseEntity.ok(userReqeust);
    }

}
