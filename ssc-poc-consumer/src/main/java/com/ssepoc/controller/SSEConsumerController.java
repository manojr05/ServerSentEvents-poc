package com.ssepoc.controller;

import com.ssepoc.model.Employee;
import com.ssepoc.service.ReceivedEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class SSEConsumerController {

    private final ReceivedEventService receivedEventService;

    @GetMapping("/consume")
    public Flux<ServerSentEvent<Employee>> subscribeToSSE() {
        WebClient client = WebClient.create("http://localhost:8080");

        return client.get()
                .uri("/events")
                .retrieve()
                .bodyToFlux(Employee.class)
                .map(data -> {
                    receivedEventService.logReceivedMessage(data);
                    return ServerSentEvent.<Employee>builder().data(data).build();
                });
    }
}

