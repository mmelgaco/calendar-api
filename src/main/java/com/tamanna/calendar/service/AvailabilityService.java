package com.tamanna.calendar.service;

import com.tamanna.calendar.dto.AvailabilityDTO;
import com.tamanna.calendar.model.Availability;
import com.tamanna.calendar.model.Role;
import com.tamanna.calendar.repository.AvailabilityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {

    private final Logger logger = LoggerFactory.getLogger(AvailabilityService.class);
    
    @Autowired
    private AvailabilityRepository availabilityRepository;

    /**
     * saves a list of availability for candidates and interviewers
     * 
     * @param availabilityList the list of availability data, containing day, name, role and slots
     *
     * @return the saved list of availability
     */
    public List<Availability> saveAll(List<Availability> availabilityList) {
        return availabilityRepository.saveAll(availabilityList);
    }

    /**
     * Find the availability by matching the slots from candidate and interviewers
     *
     * @param candidate the candidate to match the slots
     * @param interviewers the interviewers to match the slots
     *
     * @return a final list of AvailabilityDTO with the days and slots in 1-hour
     */
    public List<AvailabilityDTO> findByCandidateAndInterviewers(String candidate, List<String> interviewers) {

        List<Availability> candidateList = availabilityRepository.findByNameAndRole(candidate, Role.CANDIDATE);

        return candidateList.stream().map(availability -> {
            logger.debug("slot cadidate: " + availability.getName() + " start " + availability.getStartingHour() + " ends: " + availability.getEndingHour());

            List<int[]> matchingSlots = findMatchingInterviewersSlots(interviewers, availability);

            // all interviewers should match the candidate slot
            if (matchingSlots.size() >= interviewers.size()) {
                return matchInterviewersSlotsAndReturn(availability, matchingSlots);
            }

            return null;

        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private AvailabilityDTO matchInterviewersSlotsAndReturn(Availability availability, List<int[]> matchingSlots) {
        int start = matchingSlots.get(0)[0];
        int end = matchingSlots.get(0)[1];
        for(int i = 1; i< matchingSlots.size(); i++) {
            start = Math.max(start, matchingSlots.get(i)[0]);
            end = Math.min(end, matchingSlots.get(i)[1]);
        }

        List<String> slots = new ArrayList<>();
        for(int i=start;i<end;i++) {
            slots.add(String.format("%d to %d", start, end));
        }

        return new AvailabilityDTO(availability.getDay(), slots);
    }

    private List<int[]> findMatchingInterviewersSlots(List<String> interviewers, Availability candidateAvailability) {
        int[] candidateSlot = candidateAvailability.getSlot();

        List<Availability> interviewersList = interviewers.stream().flatMap(interviewer ->
                availabilityRepository.findByNameAndDay(interviewer, candidateAvailability.getDay()).stream()
        ).collect(Collectors.toList());

        return interviewersList.stream().map(interviewAvailability -> {
            int[] interviewerSlot = interviewAvailability.getSlot();

            if (interviewerSlot[0] > candidateSlot[1] || candidateSlot[0] > interviewerSlot[1]) {
                logger.debug(String.format("Day %tF NOT MATCHING for interview %s ", candidateAvailability.getDay(), interviewAvailability.getName()));
            } else {
                logger.debug(String.format("Day %tF MATCH! for interview %s ", candidateAvailability.getDay(), interviewAvailability.getName()));
                int start = Math.max(candidateSlot[0], interviewerSlot[0]);
                int end = Math.min(candidateSlot[1], interviewerSlot[1]);
                if (start < end) {
                    return new int[] {
                            start,
                            end,
                    };
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}
