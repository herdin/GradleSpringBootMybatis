package app.controller;

import app.dto.request.UserReqeust;
import app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SampleController {
    Logger logger = LoggerFactory.getLogger(SampleController.class);

    @Autowired
    UserService userService;

    @RequestMapping("/hello")
    public String hello() {
        return "hello!";
    }

    //회원가입
    @GetMapping("/user/{id}/{name}")
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


}
