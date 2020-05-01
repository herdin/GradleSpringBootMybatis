package com.harm.app.mapper;

import com.harm.app.dto.model.TestModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TestMapper {
    //쿼리파일을 나누기 싫으면 Annotation 으로도 가능하다
//    @Select("select * from test")
    List<TestModel> getAllTest();
    TestModel getTest(Integer testId);
//    @Insert("insert into test values(#{testId}, #{testData})")
    int addTest(TestModel testModel);
}
