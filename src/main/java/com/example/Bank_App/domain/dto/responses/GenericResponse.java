package com.example.Bank_App.domain.dto.responses;

import lombok.Builder;

@Builder
public record GenericResponse(
        String message
) {
}
