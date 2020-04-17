package app.service;

import app.dto.request.UserReqeust;
import app.mapper.TestMapper;
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
