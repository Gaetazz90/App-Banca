package com.example.Bank_App.services;

import com.example.Bank_App.domain.entities.Comune;
import com.example.Bank_App.domain.exceptions.MyEntityNotFoundException;
import com.example.Bank_App.repositories.ComuneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComuneService {

    @Autowired
    private ComuneRepository comuneRepository;

    public Comune getById(Long id) throws MyEntityNotFoundException {
        return comuneRepository.findById(id)
                .orElseThrow(() -> new MyEntityNotFoundException("Comune non trovato"));
    }

}
