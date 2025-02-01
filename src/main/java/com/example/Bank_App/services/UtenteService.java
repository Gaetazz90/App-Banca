package com.example.Bank_App.services;


import com.example.Bank_App.domain.dto.mappers.UtenteMapper;
import com.example.Bank_App.domain.dto.requests.CreateUtenteRequest;
import com.example.Bank_App.domain.dto.requests.UpdateUtenteRequest;
import com.example.Bank_App.domain.dto.responses.EntityIdResponse;
import com.example.Bank_App.domain.dto.responses.UtenteProfiloResponse;
import com.example.Bank_App.domain.entities.Utente;
import com.example.Bank_App.domain.exceptions.MyEntityNotFoundException;
import com.example.Bank_App.repositories.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;
    @Autowired
    private UtenteMapper utenteMapper;
    @Autowired
    private ComuneService comuneService;

    public Utente getById(Long id) throws MyEntityNotFoundException {
        return utenteRepository
                .findById(id)
                .orElseThrow(() -> new MyEntityNotFoundException("utente con id " + id + " non trovato"));
    }

    public List<Utente> getAll() {
        return utenteRepository.findAll();
    }

    public List<UtenteProfiloResponse> getAllProfili() {
        List<Utente> listaUtenti =  utenteRepository.findAll();
         return listaUtenti.stream().map(utente -> utenteMapper.fromUtenteToProfilo(utente)).toList();
    }

    public UtenteProfiloResponse getProfilo(Long id) throws MyEntityNotFoundException {
        Utente utente = getById(id);
        return utenteMapper.fromUtenteToProfilo(utente);
    }

    public Utente getByEmail(String email) throws MyEntityNotFoundException {
        return utenteRepository
                .findByEmail(email)
                .orElseThrow(() -> new MyEntityNotFoundException("utente con email " + email + " non trovato"));
    }

    public EntityIdResponse createUtente(CreateUtenteRequest request) throws MyEntityNotFoundException {
        Utente utenteSaved = utenteRepository.save(utenteMapper.fromCreateUtenteRequest(request));
        return new EntityIdResponse(utenteSaved.getId());
    }

    public void insertUtente(Utente utente) {
        utenteRepository.save(utente);
    }

    public EntityIdResponse updateUtente(Long id, UpdateUtenteRequest request) throws MyEntityNotFoundException {
        Utente myUtente = getById(id);
        if (request.nome() != null) myUtente.setNome(request.nome());
        if (request.cognome()!= null) myUtente.setCognome(request.cognome());
        if (request.indirizzo() != null) myUtente.setIndirizzo(request.indirizzo());
        if (request.dataNascita() != null) myUtente.setDataNascita(request.dataNascita());
        if (request.comune_id() != null) {
            myUtente.setComune(comuneService.getById(request.comune_id().id()));
        }
        return new EntityIdResponse(utenteRepository.save(myUtente).getId());
    }

    public void deleteById(Long id) {
        Utente myUtente = getById(id);
        utenteRepository.deleteById(id);
    }

}
