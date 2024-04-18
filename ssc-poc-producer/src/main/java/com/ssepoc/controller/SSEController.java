package com.ssepoc.controller;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@RestController
public class SSEController {

    private final DirectProcessor<String> eventProcessor = DirectProcessor.create();
    private OffsetDateTime timestamp = OffsetDateTime.now();
    private final Flux<ServerSentEvent<String>> eventFlux = eventProcessor.map(data ->
            ServerSentEvent.<String>builder()
                    .event("ALARM_STATUS")
                    .id("9dab5c75-05fe-4a8d-8e2b-4d4a359a9402")
                    .data(data + "\n:" + timestamp)
                    .build());

    @PostMapping("/send-event")
    public String sendEvent(@RequestBody String payload) {
        eventProcessor.onNext(payload);
        return "Event sent successfully!";
    }

    @GetMapping("/events")
    public Flux<ServerSentEvent<String>> getEvents() {
        return eventFlux;
    }
}

