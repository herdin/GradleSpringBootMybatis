package com.harm.app.service;

import com.harm.app.dto.request.EventRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class EventService {
    private Logger logger = LoggerFactory.getLogger(EventService.class);

    private Map<Integer, EventRequest> eventRepository = new HashMap<>();

    @PostConstruct
    public void init() {
        logger.debug("post construct");
        for(int i=0; i<100; i++) {
            EventRequest eventRequest = new EventRequest("dummy-url", i, "event-" + i, LocalDateTime.now());
            eventRepository.put(eventRequest.getId(), eventRequest);
        }
    }

    public EventRequest getEventById(Integer eventId) {
        return eventRepository.get(eventId);
    }

}
