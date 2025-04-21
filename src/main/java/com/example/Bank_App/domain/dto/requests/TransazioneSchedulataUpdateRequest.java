package com.example.Bank_App.domain.dto.requests;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TransazioneSchedulataUpdateRequest(
        @Positive
        Double amount,
        @Future
        LocalDateTime publishTime
) {
}