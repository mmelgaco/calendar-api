package com.tamanna.calendar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AvailabilityDTO {

    private LocalDate day;

    private List<String> slots;
}
