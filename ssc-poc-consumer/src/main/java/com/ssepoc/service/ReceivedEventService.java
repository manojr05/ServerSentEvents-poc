package com.ssepoc.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReceivedEventService {


    public void logReceivedMessage(String data) {
        log.info("Received event: {}", data);
    }
}
