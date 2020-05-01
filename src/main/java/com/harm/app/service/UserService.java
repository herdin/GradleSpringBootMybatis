package com.harm.app.service;

import com.harm.app.dto.request.UserReqeust;
import com.harm.app.mapper.TestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    TestMapper testMapper;

    public boolean addUser(UserReqeust userReqeust) {
        //
//        testMapper.addTest()
        return true;
    }
}
