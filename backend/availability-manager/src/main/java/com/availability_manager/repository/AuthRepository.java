package com.availability_manager.repository;

import com.availability_manager.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AuthRepository extends JpaRepository<Login, String> {

    Optional<Login> findByEmployee_Anumber(String anumber);

    @Query("SELECT l FROM Login l")
    List<Login> getAllLogins();
}
