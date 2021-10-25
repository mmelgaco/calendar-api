
# Interview calendar-API

This is a repository for a calendar interview REST API implemented with Spring Boot.
It implements an endpoint to add Candidates or Interviewers with its slots. 
It also implements an endpoint to check the availability of a Candidate with one or more interviewers.

**Notes:**

We decided to do a very simple version ( MVP ) with just a model (Availability) to represent both the candidates/interviewers and their availability.
When going live we should implement a Person Model (and it's endpoints ) with the two roles and an Availability Model.
Also integration tests would be great.

# Stack:

- Java 8
- Spring Boot
- JPA
- H2 Database
- JUnit
- Gradle

# Prerequisites

- Linux/Mac
- Java (min 8)
- Gradle
- Docker(optional)

## Dependencies

### Local setup:

- Clone project
- Run with gradle
    - ./gradlew bootRun
- Run the tests:
    - ./gradlew test

### Docker run:

- Install Docker
- Open console in the project folder
- to build the image, run: docker build -t calendar-api .
- to run it: docker run calendar-api
- Send the request to localhost:8080

### Endpoints:

### Add one or more Candidates, Interviewers and their availability:

```
/availability [POST]

Content-Type: application/json

[
    {
       "name":"Carl","role":"CANDIDATE","day":"2021-10-25","startingHour":9,"endingHour":10
    },
    {
       "name":"Ines","role":"INTERVIEWER","day":"2021-10-25","startingHour":9,"endingHour":16
    },
    {
       "name":"Ingrid","role":"INTERVIEWER","day":"2021-10-25","startingHour":9,"endingHour":16
    }
]

```

In this example, Carl (CANDIDATE) is available at day 25-10 from 9 to 10am, and Ines (INTERVIEWER) is available at the same day from 9am to 4pm

Then the server will respond with 201: Created and return the full list saved.

### Check the availability for Candidates and Interviewers and return the matched slots

```
/availability [GET]

Content-Type: application/json
Query params: 
 candidate=Carl
 interviewers=Ines,Ingrid
 
[
    {
        "day": "2021-10-25",
        "slots": [
            "9 to 10"
        ]
    }
]
```

In this example we fetch all the slots that match the Candidate Carl and both interviewers Ines and Ingrid.

