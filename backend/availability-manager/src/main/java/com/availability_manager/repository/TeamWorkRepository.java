package com.availability_manager.repository;

import com.availability_manager.model.TeamWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamWorkRepository extends JpaRepository<TeamWork, Long> {
    @Query("SELECT t FROM TeamWork t JOIN t.employees m WHERE m.anumber = ?1")
    List<TeamWork> membersInTeamWork(String anumber);

}
