package com.example.Bank_App.domain.dto.responses;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TransazioneResponse(
        Long id,
        Long id_conto_mittente,
        Long id_conto_destinatario,
        Long id_utente,
        Double amount,
        String tipoOperazione,
        LocalDateTime timestamp
) {
}
