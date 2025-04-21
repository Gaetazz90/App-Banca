package com.example.Bank_App.domain.dto.mappers;


import com.example.Bank_App.domain.dto.requests.CreateUtenteRequest;
import com.example.Bank_App.domain.dto.responses.UtenteProfiloResponse;
import com.example.Bank_App.domain.entities.Utente;
import com.example.Bank_App.domain.exceptions.MyEntityNotFoundException;
import com.example.Bank_App.services.ComuneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UtenteMapper {

    @Autowired
    private ComuneService comuneService;

    public Utente fromCreateUtenteRequest(CreateUtenteRequest request) throws MyEntityNotFoundException {
        return Utente
                .builder()
                .nome(request.nome())
                .cognome(request.cognome())
                .email(request.email())
                .indirizzo(request.indirizzo())
                .codiceFiscale(request.codiceFiscale())
                .comune(comuneService.getById(request.comune_id().id()))
                .dataNascita(request.dataNascita())
                .telefono(request.telefono())
                .build();
    }

    public UtenteProfiloResponse fromUtenteToProfilo(Utente utente) {
        return UtenteProfiloResponse.builder()
                .id(utente.getId())
                .nome(utente.getNome())
                .cognome(utente.getCognome())
                .dataNascita(utente.getDataNascita())
                .indirizzo(utente.getIndirizzo())
                .codiceFiscale(utente.getCodiceFiscale())
                .email(utente.getEmail())
                .telefono(utente.getTelefono())
                .comune(utente.getComune().getNome())
                .build();
    }
}
