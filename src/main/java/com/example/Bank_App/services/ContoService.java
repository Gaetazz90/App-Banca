package com.example.Bank_App.services;


import com.example.Bank_App.domain.dto.mappers.ContoMapper;
import com.example.Bank_App.domain.dto.requests.CreateContoRequest;
import com.example.Bank_App.domain.dto.responses.ContoResponse;
import com.example.Bank_App.domain.dto.responses.EntityIdResponse;
import com.example.Bank_App.domain.entities.Conto;
import com.example.Bank_App.domain.entities.Utente;
import com.example.Bank_App.domain.exceptions.MyEntityNotFoundException;
import com.example.Bank_App.domain.exceptions.WrongUtenteContoException;
import com.example.Bank_App.repositories.ContoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContoService {

    @Autowired
    private ContoRepository contoRepository;
    @Autowired
    private ContoMapper contoMapper;

    public Conto getById(Long id) throws MyEntityNotFoundException {
        return contoRepository.findById(id).orElseThrow(() -> new MyEntityNotFoundException("il conto con id " + id + " non esiste!"));
    }

    public ContoResponse getByIdWithResponse(Long id) throws MyEntityNotFoundException {
        return contoMapper.toContoResponse(contoRepository
                .findById(id)
                .orElseThrow(() -> new MyEntityNotFoundException("il conto con id " + id + " non esiste!")));
    }

    public List<ContoResponse> getAll() {
        return contoRepository.findAll()
                .stream()
                .map(contoMapper::toContoResponse)
                .toList();
    }

    public EntityIdResponse create(CreateContoRequest request) {
        Conto conto = contoMapper.fromContoRequest(request);
        contoRepository.save(conto);
        return new EntityIdResponse(conto.getId());
    }

    public ContoResponse updateSaldo(Long id, Double newSaldo) throws MyEntityNotFoundException {
        Conto conto = getById(id);
        conto.setSaldo(newSaldo);
        return contoMapper.toContoResponse(contoRepository.save(conto));
    }

    public void deleteById(Long idUtente, Long idConto) {
        Conto conto = getById(idConto);
        List<Long> listaIdIntestatari = conto.getIntestatari().stream().map(Utente::getId).toList();
        if(!listaIdIntestatari.contains(idUtente)){
            throw new WrongUtenteContoException("Il conto da chiudere con id: " +idConto + " non appartiene all'utente: " + idUtente);
        }
        contoRepository.deleteById(idConto);
    }
}
