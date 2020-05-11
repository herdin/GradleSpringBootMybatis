package com.harm.app.controller;

import com.harm.app.dto.model.TransModel;
import com.harm.app.dto.request.CardRequest;
import com.harm.app.dto.request.UserReqeust;
import com.harm.app.service.ElasticSearchService;
import com.harm.app.service.RemoteAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class MainController {
    private Logger logger = LoggerFactory.getLogger(MainController.class);
    private RemoteAPIService remoteAPIService;
    private ElasticSearchService elasticSearchService;
    public MainController(
            RemoteAPIService remoteAPIService
            , ElasticSearchService elasticSearchService) {
        this.remoteAPIService = remoteAPIService;
        this.elasticSearchService = elasticSearchService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody UserReqeust userReqeust, HttpSession httpSession) {
        logger.debug("user {} attempt to login", userReqeust.getUserId());
        ResponseEntity responseEntity = ResponseEntity.badRequest().body(null);
        if(remoteAPIService.login(userReqeust)) {
            logger.debug("user {} login ok", userReqeust.getUserId());
            httpSession.setAttribute("userId", userReqeust.getUserId());
            responseEntity = ResponseEntity.ok().body(null);
        }
        return responseEntity;
    }

    @GetMapping("/user/card")
    public ResponseEntity<List<CardRequest>> getUserCard(@SessionAttribute String userId) {
        logger.debug("user {} attempt to get user card", userId);
        List<CardRequest> userCardList = remoteAPIService.getUserCard(userId);
        logger.debug("get user card -> {}", userCardList);
        return ResponseEntity.ok().body(userCardList);
    }

    @PutMapping("/user/card")
    public ResponseEntity putUserCard(@SessionAttribute String userId, @RequestBody CardRequest cardRequest) {
        logger.debug("user {} attempt to put user card {}", userId, cardRequest);
        cardRequest.setUserId(userId);
        if(remoteAPIService.putUserCard(cardRequest)) {
            return ResponseEntity.ok().body(null);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/user/card/{cardNo}/trans")
    public ResponseEntity<List<TransModel>> getTransactionListByUserCardNo(@PathVariable String cardNo) {
        return null;
    }

}
