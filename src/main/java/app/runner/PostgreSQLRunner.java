package app.runner;

import app.mapper.TestMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class PostgreSQLRunner implements ApplicationRunner {
    private Logger logger = LoggerFactory.getLogger(PostgreSQLRunner.class);
    @Autowired
    private TestMapper testMapper;

    @Autowired
    Environment environment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        logger.debug("hello! {}", this.getClass().getSimpleName());
        logger.debug("username in property -> {}", environment.getProperty("spring.datasource.username"));

//        logger.debug("add test -> {}", testMapper.addTest(new TestModel(10, "herdin")));
        logger.debug("get test -> {}", testMapper.getAllTest());
//        testMapper.addTest(new TestModel(3, "hello"));
    }
}
