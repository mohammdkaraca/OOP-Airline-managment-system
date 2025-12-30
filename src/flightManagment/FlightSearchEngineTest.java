package flightManagment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FlightSearchEngineTest {

    private Map<Integer, Flight> mockDatabase;
    private Plane dummyPlane;

    @BeforeEach
    void setUp() {
        mockDatabase = new HashMap<>();
        dummyPlane = new Plane(1, "Boeing 737", 100, 10);

        // Flight 1: Valid Future Flight (NY -> London)
        Flight f1 = new Flight(101, "New York", "London", LocalDate.now().plusDays(5), LocalTime.of(14, 0), Duration.ofHours(7), dummyPlane);
        
        // Flight 2: Valid Future Flight (Paris -> Berlin)
        Flight f2 = new Flight(102, "Paris", "Berlin", LocalDate.now().plusDays(2), LocalTime.of(10, 0), Duration.ofHours(2), dummyPlane);

        // Flight 3: EXPIRED Flight (NY -> London) - Date is yesterday
        Flight f3 = new Flight(103, "New York", "London", LocalDate.now().minusDays(1), LocalTime.of(14, 0), Duration.ofHours(7), dummyPlane);

        mockDatabase.put(f1.getFlightNum(), f1);
        mockDatabase.put(f2.getFlightNum(), f2);
        mockDatabase.put(f3.getFlightNum(), f3);
    }

    @Test
    void testRetrieveCorrectCities() {
        System.out.println("--- Test: Retrieve Correct Cities (NY -> London) ---");
        
        // 1. Run Logic
        List<Flight> results = FlightSearchEngine.searchFlights(mockDatabase, "New York", "London");

        // 2. Print Output
        System.out.println("Expected Count: 1");
        System.out.println("Actual Count:   " + results.size());
        
        System.out.println("Expected Flight ID: 101");
        if (!results.isEmpty()) {
            System.out.println("Actual Flight ID:   " + results.get(0).getFlightNum());
        } else {
            System.out.println("Actual Flight ID:   None");
        }

        // 3. Assertions (The Test)
        assertEquals(1, results.size(), "Should return exactly 1 valid flight");
        assertEquals(101, results.get(0).getFlightNum());
        
        System.out.println("Result: PASSED\n");
    }

    @Test
    void testEliminatePastFlights() {
        System.out.println("--- Test: Eliminate Past Flights ---");
        
        // 1. Run Logic
        List<Flight> results = FlightSearchEngine.searchFlights(mockDatabase, "New York", "London");

        // 2. Print Output
        System.out.println("Flights Found: " + results.size());
        System.out.println("Checking that Flight 103 (Expired) is NOT present...");
        
        boolean foundExpired = false;
        for (Flight f : results) {
            System.out.print("Checking Flight ID " + f.getFlightNum() + "... ");
            if (f.getFlightNum() == 103) {
                foundExpired = true;
                System.out.println("FOUND (Error!)");
            } else {
                System.out.println("OK");
            }
        }

        // 3. Assertions
        for (Flight f : results) {
            assertNotEquals(103, f.getFlightNum(), "Expired flight 103 should not be in results");
        }
        
        System.out.println("Result: PASSED\n");
    }
}