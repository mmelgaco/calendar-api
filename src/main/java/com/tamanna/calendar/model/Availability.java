package com.tamanna.calendar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private LocalDate day;

    @Column(nullable = false)
    private int startingHour;

    @Column(nullable = false)
    private int endingHour;

    @JsonIgnore
    public int[] getSlot(){
        return new int[]{ this.getStartingHour(), this.getEndingHour() };
    }
}
