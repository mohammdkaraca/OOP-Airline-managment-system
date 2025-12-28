package service_management;

import java.util.Map;

import flightManagment.Seat;

public class SeatManager {
	
	
	public static Map<String,Seat> initializeSeats(int rows, int cols,Seat seatM[][],java.util.Map<String,Seat> seatMap) {
	    for (int i = 0; i < rows; i++) {
	        char rowLetter = (char) ('A' + i);
	        for (int j = 0; j < cols; j++) {
	            Seat s = new Seat(rowLetter + String.valueOf(j + 1));
	            //System.out.println(rowLetter + String.valueOf(j + 1));
	            seatM[i][j] = s;
	            seatMap.put(s.getSeatNum(), s);
	        }
	    }
		return seatMap;
	}
	
	public static int calculateAvailableSeats(Map<String,Seat> seatMap) {
	    int availableSeats = 0;
	    for (Seat seat : seatMap.values()) {
	        if (!seat.isReservedStatus()) {
	            availableSeats++;
	        }
	    }
	    return availableSeats;
	}
}
