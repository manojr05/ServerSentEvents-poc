package com.ssepoc.controller;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
import java.time.LocalTime;

@RestController
public class SSEController {

    private final DirectProcessor<String> eventProcessor = DirectProcessor.create();
    private final Flux<ServerSentEvent<String>> eventFlux = eventProcessor.map(data ->
            ServerSentEvent.<String>builder()
                    .event("message")
                    .data(data)
                    .build());

    @PostMapping("/send-event")
    public String sendEvent(@RequestBody String payload) {
        String eventData = "Payload received: " + payload + " @ " + LocalTime.now();
        eventProcessor.onNext(eventData);
        return "Event sent successfully!";
    }

    @GetMapping("/events")
    public Flux<ServerSentEvent<String>> getEvents() {
        return eventFlux;
    }
}

