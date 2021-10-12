# Search Flights

Integrates routes and scheudles APIs and merges them into a flight table from an airpot to another airport between departure and arrival date.

## TODO
- [x] validate required fields are not missing
- [x] validate required date are not in the past
- [x] validate departure is always earlier than arrival
- [x] adds new custom exceptions for 3rd-party clients
- [x] adds global exception handlers with REST friendly format
- [x] uses DTOs & POJOs for JSON marshaling/unmarshalling
- [x] filters irrelevant results early in the flow to gain speedup
- [x] uses modern java syntax wherever applicable
- [x] uses dependency injection and other SOLID principles
- [x] finds both direct and one-stop interconnected/transit flights
- [x] uses custom constraints and validations in Springboot

## Example query
```curl
curl --location --request GET 'http://localhost:8080/api/v1/interconnections?departure=DUB&arrival=FAO&departureDateTime=2022-03-01T07:00&arrivalDateTime=2022-03-03T21:00'
```
```json
[
    {
        "stops": 0,
        "legs": [
            {
                "departureAirport": "DUB",
                "arrivalAirport": "FAO",
                "departureDateTime": "2022-03-01T07:10",
                "arrivalDateTime": "2022-03-01T10:10"
            }
        ]
    },
    {
        "stops": 0,
        "legs": [
            {
                "departureAirport": "DUB",
                "arrivalAirport": "FAO",
                "departureDateTime": "2022-03-02T17:15",
                "arrivalDateTime": "2022-03-02T20:15"
            }
        ]
    },
    {
        "stops": 0,
        "legs": [
            {
                "departureAirport": "DUB",
                "arrivalAirport": "FAO",
                "departureDateTime": "2022-03-03T17:15",
                "arrivalDateTime": "2022-03-03T20:15"
            }
        ]
    }
]
```
## Directory structure

<pre>
│   TaskApplication.java
│
├───configs
│       Constants.java
│       Dependencies.java
│
├───controllers
│       FlightFinderController.java
│
├───exceptions
│   │   BadRequestErrorException.java
│   │   EntityNotFoundException.java
│   │   RestExceptionHandler.java
│   │   ThirdPartyErrorException.java
│   │
│   └───errors
│           ApiError.java
│           ApiSubError.java
│           ApiValidationError.java
│
├───models
│   ├───dtos
│   │       Flight.java
│   │       MonthPayload.java
│   │       Route.java
│   │       Schedule.java
│   │
│   └───pojos
│           Interconnection.java
│           Leg.java
│
├───services
│       AirlineClient.java
│       FlightFinderService.java
│
└───utils
│   CustomDate.java
│   SearchFormUtils.java
│   TimeZoneUtils.java
│
├───constraints
│       DateTimeFormatConstraint.java
│       DateTimeFutureConstraint.java
│
└───validators
        DateTimeFormatValidator.java
        DateTimeFutureValidator.java
</pre>


