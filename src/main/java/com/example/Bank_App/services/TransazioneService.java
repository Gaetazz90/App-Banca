package com.example.Bank_App.services;


import com.example.Bank_App.domain.dto.mappers.TransazioneMapper;
import com.example.Bank_App.domain.dto.requests.TransazioneRequest;
import com.example.Bank_App.domain.dto.responses.EntityIdResponse;
import com.example.Bank_App.domain.dto.responses.TransazioneResponse;
import com.example.Bank_App.domain.entities.Conto;
import com.example.Bank_App.domain.entities.Transazione;
import com.example.Bank_App.domain.entities.Utente;
import com.example.Bank_App.domain.enums.TipoOperazione;
import com.example.Bank_App.domain.exceptions.IllegalTransactionException;
import com.example.Bank_App.domain.exceptions.MyEntityNotFoundException;
import com.example.Bank_App.repositories.TransazioneRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransazioneService {

    @Autowired
    private UtenteService utenteService;
    @Autowired
    private ContoService contoService;
    @Autowired
    private TransazioneRepository transazioneRepository;
    @Autowired
    private TransazioneMapper transazioneMapper;

    public List<TransazioneResponse> getAll() {
        return transazioneRepository
                .findAll()
                .stream()
                .map(transazioneMapper::fromTransazioneToResponse)
                .toList();
    }

    @Transactional
    public EntityIdResponse createTransazione(TransazioneRequest request) throws MyEntityNotFoundException, IllegalTransactionException {
        // Verifico che l'utente esista e lo prendo
        Utente utente = utenteService.getById(request.id_utente());
        // Verifico che i due conti esistano e li prendo
        Conto contoMittente = contoService.getById(request.id_mittente());
        Conto contoDestinatario = contoService.getById(request.id_destinatario());
        // verifico che i conti siano distinti
        if (contoMittente.equals(contoDestinatario)) {
            throw new IllegalTransactionException("Conto mittente e destinatario coincidono!");
        }
        // Verifico che il conto mittente appartenga all'utente
        if (!contoMittente.getIntestatari().contains(utente)) {
            throw new IllegalTransactionException("Il conto " + contoMittente.getId() + " non appartiene all'utente " +
                    utente.getId());
        }
        // Verifico che il conto mittente abbia abbastanza cash
        if (contoMittente.getSaldo() < request.amount()) {
            throw new IllegalTransactionException("non hai abbastanza soldi sul conto!");
        }
        // Modifico il saldo nei conti
        contoService.updateSaldo(contoMittente.getId(), contoMittente.getSaldo() - request.amount());
        contoService.updateSaldo(contoDestinatario.getId(), contoDestinatario.getSaldo() + request.amount());
        Transazione transazione = Transazione
                .builder()
                .amount(request.amount())
                .contoMittente(contoMittente)
                .contoDestinatario(contoDestinatario)
                .timestamp(LocalDateTime.now())
                .utente(utente)
                .tipoOperazione(TipoOperazione.TRANSAZIONE)
                .build();
        transazioneRepository.save(transazione);
        return EntityIdResponse.builder().id(transazione.getId()).build();
    }

    public EntityIdResponse createVersamento(TransazioneRequest request) throws MyEntityNotFoundException {
        // Verifico che l'utente esista e lo prendo
        Utente utente = utenteService.getById(request.id_utente());
        // Verifico che il conto dove effettuare il versamento esista
        Conto contoDestinatario = contoService.getById(request.id_destinatario());
        contoService.updateSaldo(contoDestinatario.getId(), contoDestinatario.getSaldo() + request.amount());
        Transazione transazione = transazioneRepository.save(
                Transazione.builder()
                        .amount(request.amount())
                        .contoDestinatario(contoDestinatario)
                        .timestamp(LocalDateTime.now())
                        .utente(utente)
                        .tipoOperazione(TipoOperazione.VERSAMENTO)
                        .build()
        );
        return EntityIdResponse.builder().id(transazione.getId()).build();
    }

    public EntityIdResponse createPrelievo(TransazioneRequest request) throws MyEntityNotFoundException, IllegalTransactionException {
        // Verifico che l'utente esista e lo prendo
        Utente utente = utenteService.getById(request.id_utente());
        // Verifico che il conto dove effettuare il versamento esista
        Conto contoDestinatario = contoService.getById(request.id_destinatario());
        // controllo che il conto appartenga all'utente per poter prelevare
        if (!contoDestinatario.getIntestatari().contains(utente)) {
            throw new IllegalTransactionException("Il conto " + contoDestinatario.getId() + " non appartiene all'utente " +
                    utente.getId());
        }
        // Verifico che il conto abbia abbastanza cash
        if (contoDestinatario.getSaldo() < request.amount()) {
            throw new IllegalTransactionException("non hai abbastanza soldi sul conto!");
        }
        contoService.updateSaldo(contoDestinatario.getId(), contoDestinatario.getSaldo() - request.amount());
        Transazione transazione = transazioneRepository.save(
                Transazione.builder()
                        .amount(request.amount())
                        .contoDestinatario(contoDestinatario)
                        .timestamp(LocalDateTime.now())
                        .utente(utente)
                        .tipoOperazione(TipoOperazione.PRELIEVO)
                        .build()
        );
        return EntityIdResponse.builder().id(transazione.getId()).build();
    }
}
