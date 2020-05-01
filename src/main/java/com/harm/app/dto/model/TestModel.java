package com.harm.app.dto.model;

import org.apache.ibatis.type.Alias;

@Alias("testModel") //xml mapper 에서 resultType 으로 사용된다.
public class TestModel {
    Integer testId;
    String testData;

    public TestModel(Integer testId, String testData) {
        this.testId = testId;
        this.testData = testData;
    }

    public Integer getTestId() {
        return testId;
    }

    public void setTestId(Integer testId) {
        this.testId = testId;
    }

    public String getTestData() {
        return testData;
    }

    public void setTestData(String testData) {
        this.testData = testData;
    }

    @Override
    public String toString() {
        return "TestModel{" +
                "testId=" + testId +
                ", testData='" + testData + '\'' +
                '}';
    }

}
