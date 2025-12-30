package flightManagment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FlightSearchEngine {

    /**
     * Filters flights based on departure/arrival cities and ensures the flight hasn't departed yet.
     */
    public static List<Flight> searchFlights(Map<Integer, Flight> allFlights, String from, String to) {
        if (allFlights == null) return new ArrayList<>();

        return allFlights.values().stream()
            // 1. Filter by City
            .filter(f -> f.getDeparturePlace().equalsIgnoreCase(from) && 
                         f.getArrivalPlace().equalsIgnoreCase(to))
            // 2. Filter out flights that have already passed (Date + Time < Now)
            .filter(f -> {
                LocalDateTime flightDateTime = LocalDateTime.of(f.getDate(), f.getHour());
                return flightDateTime.isAfter(LocalDateTime.now());
            })
            .collect(Collectors.toList());
    }
}