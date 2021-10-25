package com.tamanna.calendar.controller;

import com.tamanna.calendar.dto.AvailabilityDTO;
import com.tamanna.calendar.model.Availability;
import com.tamanna.calendar.service.AvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/availability")
public class AvailabilityApiRestController {

    @Autowired
    private AvailabilityService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<Availability> create(@RequestBody List<Availability> availabilityList) {
        return service.saveAll(availabilityList);
    }

    @GetMapping
    public List<AvailabilityDTO> find(@RequestParam(required = true) String candidate,
                                      @RequestParam(required = true) List<String> interviewers) {
        return service.findByCandidateAndInterviewers(candidate, interviewers);
    }
}
