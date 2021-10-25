package com.tamanna.calendar.repository;

import com.tamanna.calendar.model.Availability;
import com.tamanna.calendar.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Integer> {

    List<Availability> findByNameAndRole(String name, Role role);

    List<Availability> findByNameAndDay(String name, LocalDate day);

}
