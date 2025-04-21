package com.example.Bank_App.domain.dto.responses;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UtenteProfiloResponse (
        Long id,
        String nome,
        String cognome,
        LocalDate dataNascita,
        String email,
        String telefono,
        String codiceFiscale,
        String indirizzo,
        String comune
){
}
