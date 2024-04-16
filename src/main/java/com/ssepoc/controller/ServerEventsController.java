package com.ssepoc.controller;

import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@RestController
public class ServerEventsController {

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> getEvents(){
        Stream<Integer> stringStream = Stream.of(11,12,13,14,15,16,17,18,19,20);

        AtomicInteger counter = new AtomicInteger(1);
        return Flux.fromStream(stringStream)
                .map(integer -> ServerSentEvent.<String> builder()
                        .id(String.valueOf(counter.getAndIncrement()))
                        .data(String.valueOf(integer))
                        .event("sampleEvent")
                        .build())
                .delayElements(Duration.ofMillis(1000));
    }

}
