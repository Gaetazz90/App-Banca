package com.example.Bank_App.domain.dto.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record TransazioneRequest(
        @Positive
        Double amount,
        Long id_mittente,
        @NotNull
        Long id_destinatario,
        @NotNull
        Long id_utente,
        @NotNull
        String tipoOperazione
) {
}