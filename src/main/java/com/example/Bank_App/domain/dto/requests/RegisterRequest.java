package com.example.Bank_App.domain.dto.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import java.time.LocalDate;

@Builder
public record RegisterRequest(
        @NotBlank(message = "Il nome non può essere blank o null")
        String nome,
        @NotBlank(message = "Il cognome non può essere blank o null")
        String cognome,
        @Email(message = "Email non valida")
        String email,
        @NotBlank(message = "inserisci una passsword")
        String password,
        @Pattern(
                regexp = "^\\+?[0-9]+$",
                message = "Telefono non valido")
        String telefono,
        @Pattern(
                regexp = "^[A-Z]{6}[0-9]{2}[A-EHLMPR-T][0-9]{2}[A-Z][0-9]{3}[A-Z]$",
                message = "Codice fiscale non valido")
        String codiceFiscale,
        @NotBlank(message = "L'indirizzo non può essere null o blank")
        String indirizzo,
        @Past(message = "La data di nascita deve essere nel passato")
        LocalDate dataNascita,
        @NotNull(message = "il comune deve essere presente")
        EntityIdRequest comune_id
) {
}
