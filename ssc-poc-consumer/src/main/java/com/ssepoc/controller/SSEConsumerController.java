package com.ssepoc.controller;

import com.ssepoc.service.ReceivedEventService;
import com.ssepoc.util.ExtractObjects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SSEConsumerController {

    private final ReceivedEventService receivedEventService;
    private final OkHttpClient client = new OkHttpClient()
            .newBuilder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .writeTimeout(0, TimeUnit.MILLISECONDS)
            .connectTimeout(0, TimeUnit.MILLISECONDS)
            .build(); // Create OkHttpClient instance

    @GetMapping("/consume")
    public Flux<ServerSentEvent<String>> subscribeToSSE() {
        Request request = new Request.Builder()
                .url("http://localhost:8080/events")
                .build();

        return Flux.create(sink -> {
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    sink.error(e); // Emit error to the Flux sink
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        IOException exception = new IOException("Unexpected code " + response);
                        sink.error(exception); // Emit error to the Flux sink
                        return;
                    }

                    try (ResponseBody responseBody = response.body()) {
                        if (responseBody == null) {
                            IOException exception = new IOException("Response body is null");
                            sink.error(exception); // Emit error to the Flux sink
                            return;
                        }

                        AtomicReference<StringBuilder> stringBuilder = new AtomicReference<>(new StringBuilder());

                        try (BufferedSource bufferedSource = responseBody.source()) {
                            while (!bufferedSource.exhausted()) {
                                String line = bufferedSource.readUtf8Line();
                                if (line != null && !line.isEmpty()) {
                                    stringBuilder.get().append(line).append("\n");
                                } else {
                                    // Parse the received data into separate fields
                                    String[] parts = stringBuilder.toString().split("\n");
                                    String data = parts[2].substring(5);
                                    String id = parts[0].substring(3);
                                    String event = parts[1].substring(6);
                                    String timeStamp = parts[3].substring(6);
                                    if (data != null) {
                                        receivedEventService.logReceivedMessage(data);
                                        ExtractObjects.extractObjects(data);
                                        sink.next(ServerSentEvent.builder(data + ":" + timeStamp).id(id).event(event).build());
                                    }
                                    stringBuilder.set(new StringBuilder());
                                }
                            }
                        } catch (IOException e) {
                            sink.error(e);
                        }
                        sink.complete();
                    } catch (Exception e) {
                        sink.error(e);
                    }
                }
            });
        });
    }
}
