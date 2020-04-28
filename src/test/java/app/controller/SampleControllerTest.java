package app.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SampleControllerTest {
    Logger logger = LoggerFactory.getLogger(SampleControllerTest.class);

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getUser() throws Exception {
        int userId = 1;
        mockMvc.perform(MockMvcRequestBuilders.get("/user/" + userId))
                .andDo(print())
                .andExpect(status().isOk())
                ;
    }

    @Test
    public void hello() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/hello")
        )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @Test
    public void retryHello() throws Exception {
        int userId = 1;
        mockMvc.perform(MockMvcRequestBuilders.get("/remoteHello")
                .param("requestUrl", "https://www.naver.never.com")
                .param("port", "443")
        )
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }
}
