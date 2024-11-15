package com.dekra.availability_manager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "employees")
@Entity
public class Employee {

    @Id
    @Column(name = "anumber")
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
}
