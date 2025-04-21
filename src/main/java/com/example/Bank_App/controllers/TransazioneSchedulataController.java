package com.example.Bank_App.controllers;


import com.example.Bank_App.domain.dto.requests.TransazioneSchedulataRequest;
import com.example.Bank_App.domain.dto.requests.TransazioneSchedulataUpdateRequest;
import com.example.Bank_App.domain.dto.responses.EntityIdResponse;
import com.example.Bank_App.domain.dto.responses.GenericResponse;
import com.example.Bank_App.domain.exceptions.IllegalTransactionException;
import com.example.Bank_App.domain.exceptions.MyEntityNotFoundException;
import com.example.Bank_App.services.TransazioneSchedulataService;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/v1/scheduled_transaction")
public class TransazioneSchedulataController {

    @Autowired
    private TransazioneSchedulataService transazioneScheduledService;

    @PostMapping("/create")
    public ResponseEntity<EntityIdResponse> createTransazioneScheduled(@RequestBody TransazioneSchedulataRequest request) throws IllegalTransactionException, MyEntityNotFoundException, SchedulerException {
        return new ResponseEntity<>(transazioneScheduledService.createTransazioneSchedulata(request), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<EntityIdResponse> updateTransazioneScheduled(@PathVariable Long id,
                                                                       @RequestBody TransazioneSchedulataUpdateRequest request) throws SchedulerException {
        return new ResponseEntity<>(transazioneScheduledService.updateTransazioneSchedulata(id, request), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}/utente/{utenteId}")
    public ResponseEntity<GenericResponse> deleteById(@PathVariable Long id, @PathVariable Long utenteId) throws SchedulerException {
        transazioneScheduledService.deleteTransazioneSchedulataById(id, utenteId);
        return new ResponseEntity<>(new GenericResponse
                ("Transazione schedulata con id " + id + " eliminata con successo"), HttpStatus.OK);
    }

}
