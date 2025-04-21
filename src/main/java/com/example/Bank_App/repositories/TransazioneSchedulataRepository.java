package com.example.Bank_App.repositories;


import com.example.Bank_App.domain.entities.TransazioneSchedulata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransazioneSchedulataRepository extends JpaRepository<TransazioneSchedulata, Long> {
}
