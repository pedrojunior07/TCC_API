package com.vaticano.paroquia.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private String message;

    private Object data;

    public MessageResponse(String message) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }

    public MessageResponse(String message, Object data) {
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.data = data;
    }
}
