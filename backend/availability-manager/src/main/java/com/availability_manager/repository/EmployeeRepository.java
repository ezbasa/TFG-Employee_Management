package com.availability_manager.repository;

import com.availability_manager.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    @Query("SELECT e FROM Employee e " +
            "ORDER BY e.name")
    List<Employee> getAllEmployees();

    @Query("SELECT e FROM Employee e " +
            "WHERE e.location = ?1")
    List<Employee> getEmployeeByLocation(String location);
}