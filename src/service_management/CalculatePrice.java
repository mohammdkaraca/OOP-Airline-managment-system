package service_management;

import java.util.Random;
import flightManagment.Plane;

public class CalculatePrice {
	
	private static final double basePrice = 30.0;
	
	public static double calculateSeatPrice(double baggageWeight, int seatClass, double duration, Plane plane, String seatNum) {
	    
	    Random r = new Random(12345); // Fixed seed for consistent results
	    double finalPrice = r.nextDouble((100 - basePrice) + 1) + basePrice;
	    
	    if(duration > 2.0) {
	    	finalPrice += (duration - 2.0) * 20.0; // Add $20 for each hour beyond 2 hours
	    }
	    if (baggageWeight > 20.0) {
	        double extraWeight = baggageWeight - 20.0;
	        finalPrice += extraWeight * 3.0; // Add $3 for each kg over 20kg
	    }
	    
	    // Simple check for window seat based on column layout
	    int seatNumber = Character.getNumericValue(seatNum.charAt(1));
	    if(seatNumber == plane.getColAmount() || seatNumber == 1) {
	    	finalPrice += 15.0; // Add $15 for window seats
	    }
        
	    switch (seatClass) {
	        case 0:
	            // Economy: No change
	            break;
	        case 1:
	            finalPrice = finalPrice * 1.5; // Business class is 50% more expensive
	            break;
	        default:
	            throw new IllegalArgumentException("Invalid seat class: " + seatClass);
	    }

	    return finalPrice;
	}
}