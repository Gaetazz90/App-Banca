package com.example.Bank_App.services;

import com.example.Bank_App.domain.dto.requests.TransazioneRequest;
import com.example.Bank_App.domain.dto.requests.TransazioneSchedulataRequest;
import com.example.Bank_App.domain.dto.requests.TransazioneSchedulataUpdateRequest;
import com.example.Bank_App.domain.dto.responses.EntityIdResponse;
import com.example.Bank_App.domain.entities.Conto;
import com.example.Bank_App.domain.entities.TransazioneSchedulata;
import com.example.Bank_App.domain.entities.Utente;
import com.example.Bank_App.domain.exceptions.IllegalTransactionException;
import com.example.Bank_App.domain.exceptions.MyEntityNotFoundException;
import com.example.Bank_App.repositories.TransazioneSchedulataRepository;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Date;

@Service
public class TransazioneSchedulataService implements Job {

    @Autowired
    private TransazioneSchedulataRepository transazioneSchedulataRepository;
    @Autowired
    private UtenteService utenteService;
    @Autowired
    private ContoService contoService;
    @Autowired
    private TransazioneService transazioneService;
    @Autowired
    private Scheduler scheduler;

    public EntityIdResponse createTransazioneSchedulata(TransazioneSchedulataRequest request)
            throws MyEntityNotFoundException, IllegalTransactionException, SchedulerException {
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
        TransazioneSchedulata transazioneSchedulata = TransazioneSchedulata
                .builder()
                .amount(request.amount())
                .publishTime(request.publishTime())
                .utente(utente)
                .contoMittente(contoMittente)
                .contoDestinatario(contoDestinatario)
                .build();

        transazioneSchedulataRepository.save(transazioneSchedulata);

        TransazioneRequest transazioneRequest = TransazioneRequest
                .builder()
                .amount(request.amount())
                .id_utente(utente.getId())
                .id_mittente(contoMittente.getId())
                .id_destinatario(contoDestinatario.getId())
                .tipoOperazione("transazione")
                .build();
        // crea il job per lo schedule della transazione
        JobDetail jobDetail = buildJobDetail(transazioneSchedulata, transazioneRequest);
        Trigger trigger = buildJobTrigger(jobDetail, Date.from(transazioneSchedulata.getPublishTime().atZone(ZoneId.systemDefault()).toInstant()));
        scheduler.scheduleJob(jobDetail, trigger);
        return EntityIdResponse.builder().id(transazioneSchedulata.getId()).build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, Date publishTime) {

        return TriggerBuilder
                .newTrigger()
                .forJob(jobDetail)
                .startAt(publishTime)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .build();

    }

    private JobDetail buildJobDetail(TransazioneSchedulata transazioneSchedulata,
                                     TransazioneRequest transazioneRequest) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("entityData", transazioneRequest); // ---> l'entità che passerò all'execute
        jobDataMap.put("id", transazioneSchedulata.getId()); // ---> l'id del job
        return JobBuilder
                .newJob(TransazioneSchedulataService.class)
                .withIdentity(String.valueOf(transazioneSchedulata.getId()), "transazioni")
                .storeDurably()
                .setJobData(jobDataMap)
                .build();
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        TransazioneRequest request = (TransazioneRequest) jobDataMap.get("entityData");
        Long id_scheduled = jobDataMap.getLongValue("id");
        try {
            transazioneService.createTransazione(request);
        } catch (MyEntityNotFoundException | IllegalTransactionException e) {
            throw new RuntimeException(e);
        }
        transazioneSchedulataRepository.deleteById(id_scheduled);
    }

    public EntityIdResponse updateTransazioneSchedulata(Long id, TransazioneSchedulataUpdateRequest request) throws SchedulerException {
        TransazioneSchedulata transazioneSchedulata = transazioneSchedulataRepository
                .findById(id)
                .orElseThrow(() -> new MyEntityNotFoundException("La transazione schedulata con " + id + " non è presente"));
        JobKey jobKey = new JobKey(String.valueOf(transazioneSchedulata.getId()), "transazioni");
        scheduler.deleteJob(jobKey);
        TransazioneSchedulataRequest transazioneScheduledRequest = TransazioneSchedulataRequest
                .builder()
                .amount(request.amount() == null ? transazioneSchedulata.getAmount() : request.amount())
                .publishTime(request.publishTime() == null ? transazioneSchedulata.getPublishTime() : request.publishTime())
                .id_utente(transazioneSchedulata.getUtente().getId())
                .id_mittente(transazioneSchedulata.getContoMittente().getId())
                .id_destinatario(transazioneSchedulata.getContoDestinatario().getId())
                .build();
        transazioneSchedulataRepository.deleteById(id);
        return createTransazioneSchedulata(transazioneScheduledRequest);
    }

    public void deleteTransazioneSchedulataById(Long id, Long utenteId) throws SchedulerException {
        TransazioneSchedulata transazioneSchedulata = transazioneSchedulataRepository
                .findById(id)
                .orElseThrow(() -> new MyEntityNotFoundException("La transazione schedulata con " + id + " non è presente"));
        if(!transazioneSchedulata.getUtente().getId().equals(utenteId)){
            throw new IllegalTransactionException("ERRORE: La transazione con id: " + id + " non corrisponde all'utente: " + utenteId);
        }
        JobKey jobKey = new JobKey(String.valueOf(transazioneSchedulata.getId()), "transazioni");
        scheduler.deleteJob(jobKey);
        transazioneSchedulataRepository.deleteById(id);
    }
}
