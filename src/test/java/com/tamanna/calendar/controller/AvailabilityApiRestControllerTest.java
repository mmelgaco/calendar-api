package com.tamanna.calendar.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tamanna.calendar.dto.AvailabilityDTO;
import com.tamanna.calendar.model.Availability;
import com.tamanna.calendar.model.Role;
import com.tamanna.calendar.repository.AvailabilityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AvailabilityApiRestControllerTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    AvailabilityRepository availabilityRepository;
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public void setup() {
        availabilityRepository.deleteAll();
        om.findAndRegisterModules();

    }

    @Test
    public void should_create_availability() throws Exception {
        List<Availability> expectedRecord = getTestData().get("carl");
        List<Availability> actualRecord = om.readValue(mockMvc.perform(post("/availability")
                .contentType("application/json")
                .content(om.writeValueAsString(getTestData().get("carl"))))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), new TypeReference<List<Availability>>() {});

        Assertions.assertEquals(expectedRecord.size(), actualRecord.size());
    }

    @Test
    public void when_no_parameters_should_return_a_bad_request() throws Exception {
        mockMvc.perform(get("/availability"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void when_both_interviewers_has_availability_return_intersecting_slots() throws Exception {
        Map<String, List<Availability>> data = getTestData();

        for (Map.Entry<String, List<Availability>> kv : data.entrySet()) {
            om.readValue(mockMvc.perform(post("/availability")
                    .contentType("application/json")
                    .content(om.writeValueAsString(kv.getValue())))
                    .andDo(print())
                    .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString(), new TypeReference<List<Availability>>() {});
        }

        String names = "Ingrid,Ines";

        List<AvailabilityDTO> list = om.readValue(mockMvc.perform(get("/availability?candidate=Carl&interviewers=" + names))
                .andDo(print())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString(), new TypeReference<List<AvailabilityDTO>>() {}
        );

        Assertions.assertEquals(list.size(), 2);
        AvailabilityDTO first = list.get(0);
        Assertions.assertEquals(first.getDay(), LocalDate.of(2021, 10, 26));
        Assertions.assertEquals(first.getSlots().size(), 1);
        Assertions.assertEquals(first.getSlots().get(0), "9 to 10");
        AvailabilityDTO second = list.get(1);
        Assertions.assertEquals(second.getDay(), LocalDate.of(2021, 10, 28));
        Assertions.assertEquals(second.getSlots().size(), 1);
        Assertions.assertEquals(second.getSlots().get(0), "9 to 10");
    }

    private Map<String, List<Availability>> getTestData() throws ParseException {
        Map<String, List<Availability>> data = new LinkedHashMap<>();

        // Candidate Carl
        LocalDate monday = LocalDate.of(2021, 10, 25);
        LocalDate tuesday = LocalDate.of(2021, 10, 26);
        LocalDate wednesday = LocalDate.of(2021, 10, 27);
        LocalDate thursday = LocalDate.of(2021, 10, 28);
        LocalDate friday = LocalDate.of(2021, 10, 29);

        List<Availability> carl = Arrays.asList(
            new Availability(1, "Carl", Role.CANDIDATE, monday, 9, 10),
            new Availability(2, "Carl", Role.CANDIDATE, tuesday, 9, 10),
            new Availability(3, "Carl", Role.CANDIDATE, wednesday, 10, 12),
            new Availability(4, "Carl", Role.CANDIDATE, thursday, 8, 10),
            new Availability(5, "Carl", Role.CANDIDATE, friday, 9, 10)
        );

        data.put("carl", carl);

        // Interview Ines
        List<Availability> ines = Arrays.asList(
                new Availability(6, "Ines", Role.INTERVIEWER, monday, 9, 16),
                new Availability(7, "Ines", Role.INTERVIEWER, tuesday, 9, 16),
                new Availability(8, "Ines", Role.INTERVIEWER, wednesday, 10, 16),
                new Availability(9, "Ines", Role.INTERVIEWER, thursday, 9, 16),
                new Availability(10, "Ines", Role.INTERVIEWER, friday, 9, 16)
        );
        data.put("ines", ines);

        // Interview Ingrid
        List<Availability> ingrid = Arrays.asList(
                new Availability(11, "Ingrid", Role.INTERVIEWER, monday, 12, 18),
                new Availability(12, "Ingrid", Role.INTERVIEWER, tuesday, 9, 12),
                new Availability(13, "Ingrid", Role.INTERVIEWER, wednesday, 12, 18),
                new Availability(14, "Ingrid", Role.INTERVIEWER, thursday, 8, 12)
        );
        data.put("ingrid", ingrid);

        return data;
    }
}
