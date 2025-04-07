package com.availability_manager.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Table(name = "TeamsWorks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TeamWork {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "Name", nullable = false, length = 50)
    private String name;

    @Column(name = "Description", nullable = false)
    private String description;

    @ManyToMany
    @JsonIgnore
    @JoinTable(name = "TeamsWorks_Employees", joinColumns = @JoinColumn(name = "TeamWork_Id"), inverseJoinColumns = @JoinColumn(name = "Employee_Anumber"))
    private Set<Employee> employees = new HashSet<>();
}
