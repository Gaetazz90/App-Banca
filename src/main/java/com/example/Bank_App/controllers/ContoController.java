package com.example.Bank_App.controllers;

import com.example.Bank_App.domain.dto.requests.CreateContoRequest;
import com.example.Bank_App.domain.dto.responses.ContoResponse;
import com.example.Bank_App.domain.dto.responses.EntityIdResponse;
import com.example.Bank_App.domain.dto.responses.GenericResponse;
import com.example.Bank_App.domain.exceptions.MyEntityNotFoundException;
import com.example.Bank_App.services.ContoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/v1/conto")
public class ContoController {

    @Autowired
    private ContoService contoService;

    @GetMapping("/get/{id}")
    public ResponseEntity<ContoResponse> getContoById(@PathVariable Long id) throws MyEntityNotFoundException {
        return new ResponseEntity<>(contoService.getByIdWithResponse(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ContoResponse>> getAll() {
        return new ResponseEntity<>(contoService.getAll(), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<EntityIdResponse> createConto(@RequestBody @Valid CreateContoRequest request) {
        return new ResponseEntity<>(contoService.create(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{idConto}/utente/{idUtente}")
    public ResponseEntity<GenericResponse> deleteContoById(@PathVariable Long idUtente, @PathVariable Long idConto){
        contoService.deleteById(idUtente,idConto);
        return new ResponseEntity<>(new GenericResponse("Conto con id: " + idConto + " cancellato con successo"), HttpStatus.OK);
    }

}
