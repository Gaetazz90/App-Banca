package com.example.Bank_App.repositories;


import com.example.Bank_App.domain.entities.Conto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContoRepository extends JpaRepository<Conto, Long> {
}
