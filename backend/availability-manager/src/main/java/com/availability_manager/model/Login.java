package com.availability_manager.model;

import com.availability_manager.model.enumerate.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Logins")
@Entity
public class Login {

    @Id
    @Column(name = "employee_anumber", nullable = false)
    private String employeeAnumber; //Al usar MapsId en la relacion, este campo se auto rellena

    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "firstAccess")
    private boolean firstAccess;

    @OneToOne
    @MapsId // Indica que Login usará la misma clave primaria que Employee
    @JsonIgnore
    @JoinColumn(name = "employee_anumber", referencedColumnName = "anumber")
    private Employee employee; // Relación con Employee, usa el campo anumber como clave foránea
}