package com.harm.app.dto.request;

import java.time.LocalDateTime;

public class EventRequest {
    private String url;
    private Integer id;
    private String name;
    private LocalDateTime regTime;

    public EventRequest() {}
    public EventRequest(String url, Integer id, String name, LocalDateTime regTime) {
        this.url = url;
        this.id = id;
        this.name = name;
        this.regTime = regTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getRegTime() {
        return regTime;
    }

    public void setRegTime(LocalDateTime regTime) {
        this.regTime = regTime;
    }

    @Override
    public String toString() {
        return "Event{" +
                "url='" + url + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", regTime=" + regTime +
                '}';
    }
}
