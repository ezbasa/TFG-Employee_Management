package com.availability_manager.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees")
@Entity
public class Employee {

    @Id
    @Column(name = "anumber", unique = true, nullable = false)
    private String anumber;

    @Column(name = "name")
    private String name;

    @Column(name = "team")
    private String team;

    @Column(name = "location")
    private String location;

    @Column(name = "holiday")
    private int holiday;

    @OneToMany(mappedBy = "employee")
    @ToString.Exclude
    private List<CalendarItem> calendarItems;

    @OneToOne(mappedBy = "employee")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Login login;
}
