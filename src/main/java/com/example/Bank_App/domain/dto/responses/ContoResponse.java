package com.example.Bank_App.domain.dto.responses;

import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record ContoResponse(
        Long id,
        Double saldo,
        Double costo,
        LocalDate dataSottoscrizione,
        List<Long> idIntestatari
) {
}
