package com.harm.app.dto.request;

import javax.validation.constraints.NotBlank;

public class UserReqeust {
    @NotBlank
    private String userId;
    @NotBlank
    private String userPassword;

    public UserReqeust() {}
    public UserReqeust(String userId, String userPassword) {
        this.userId = userId;
        this.userPassword = userPassword;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public String toString() {
        return "UserReqeust{" +
                "userId='" + userId + '\'' +
                ", userPassword='" + userPassword + '\'' +
                '}';
    }
}
