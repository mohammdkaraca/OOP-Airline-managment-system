package service_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import flightManagment.Plane;

public class CalculatePriceTest {

    @Test
    void testBusinessClassPricing() {
        // Create a dummy plane for the method parameters
        Plane dummyPlane = new Plane(1234, "Boeing 737", 180, 30); 
        
        // Input values
        double weight = 15.0; // No extra weight fee
        double duration = 1.0; // No extra duration fee
        String seat = "12B"; // Middle seat (no window fee)
        
        // Scenario: Economy (0) vs Business (1)
        double economyPrice = CalculatePrice.calculateSeatPrice(weight, 0, duration, dummyPlane, seat);
        double businessPrice = CalculatePrice.calculateSeatPrice(weight, 1, duration, dummyPlane, seat);
        
        // Verify that Business is exactly 1.5x Economy
        assertEquals(economyPrice * 1.5, businessPrice, 0.01, 
            "Business class should be 50% more expensive than Economy"); 
    }

    @Test
    void testInvalidSeatClassException() {
        Plane dummyPlane = new Plane(5678, "Boeing 737", 180, 30);
        
        // This test verifies that the system throws an exception for an invalid class (e.g., 99)
        assertThrows(IllegalArgumentException.class, () -> {
            CalculatePrice.calculateSeatPrice(10.0, 99, 1.0, dummyPlane, "10A");
        }, "Should throw IllegalArgumentException for invalid seat class"); 
    }
}