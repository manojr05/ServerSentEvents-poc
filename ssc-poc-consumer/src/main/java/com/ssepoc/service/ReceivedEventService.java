package com.ssepoc.service;

import com.ssepoc.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReceivedEventService {


    public void logReceivedMessage(Employee data) {
        log.info("Received event: {}", data);
    }
}
