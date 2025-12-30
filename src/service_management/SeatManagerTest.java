package service_management;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import flightManagment.Plane;
import flightManagment.Seat;

class SeatManagerTest {

    private Plane plane;

    @BeforeEach
    void setUp() {
        // Create a plane with 10 seats (Capacity 10, 2 rows of 5)
        plane = new Plane(999, "TestJet", 10, 2);
    }

    @Test
    void testEmptySeatsCountDecreases() {
        int initialEmpty = plane.getEmptySeatsCount(); // Should be 10
        int initialFull = plane.getFulledSeatsCount(); // Should be 0

        // Reserve a seat (e.g., "A1")
        Seat seat = plane.getSeatByNumber("A1");
        assertNotNull(seat, "Seat A1 should exist");

        // Use the method from Seat.java
        seat.setReservedStatus(true, plane);

        // Check counters
        assertEquals(initialEmpty - 1, plane.getEmptySeatsCount(), "Empty seats count should decrease by 1");
        assertEquals(initialFull + 1, plane.getFulledSeatsCount(), "Full seats count should increase by 1");
    }

    @Test
    void testExceptionOnNonExistentSeat() {
        // We expect an exception when asking for a seat that doesn't exist (e.g., "Z99")
        // The lambda now just calls the method directly.
        assertThrows(IllegalArgumentException.class, () -> {
            plane.getSeatByNumber("Z99");
        }, "Should throw IllegalArgumentException for non-existent seat");
    }
}