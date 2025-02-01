package com.example.Bank_App.repositories;

import com.example.Bank_App.domain.entities.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenBlackListRepository  extends JpaRepository<TokenBlackList,Long> {

    Optional<TokenBlackList> getByToken(String token);

}
