package service_management;

import flightManagment.*;
import java.util.*;
import reservation_ticketing.*;
import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class FlightManager {
	
	// Placeholder for extraction logic if needed separately
	public static void createFlight(int flightNum, String departurePlace, String arrivalPlace, LocalDate date, LocalTime hour, Duration duration) {
		// Logic handled in overloaded method below
	}

	public static Database extractFileData() throws FileNotFoundException {
		Database data = new Database();
		
		// Using relative paths ensures portability
		data.setPlanes(FileOp.getPlaneData("src/planes.csv"));
		data.setFlights(FileOp.getFlightData("src/flights.csv", data.getPlanes()));
		data.setPassengers(FileOp.getPassengerData("src/passengers.csv"));
		data.setReservations(FileOp.getReservationData("src/reservations.csv", data.getFlights(), data.getPassengers()));
		data.tickets = FileOp.getTicketData("src/tickets.csv", data.getReservations(), data.getFlights());
		return data;
	}

	public static Flight createFlight(int flightNum, String departurePlace, String arrivalPlace, LocalDate date, LocalTime hour, Duration duration, Plane plane, Database db) {
		Flight flight = new Flight(flightNum, departurePlace, arrivalPlace, date, hour, duration, plane);
		db.getFlights().put(flightNum, flight);
		FileOp.saveFile("src/flights.csv", db.getFlights().values(), false, true,
				        "flightNum,departure,arrival,date,time,duration,planeId");
		return flight;
	}

	public static Plane createPlane(int planeId, String model, int capacity, int rows, Database db) {
		Plane plane = new Plane(planeId, model, capacity, rows);
		db.getPlanes().put(planeId, plane);
		FileOp.saveFile("src/planes.csv", db.getPlanes().values(), false, true,
				        "planeId,model,capacity,rows");
		return plane;
	}

	public static Flight updateFlight(Database db, Flight flight) {
		if (db.getFlights().containsKey(flight.getFlightNum())) {
			db.getFlights().put(flight.getFlightNum(), flight);
			FileOp.saveFile("src/flights.csv", db.getFlights().values(), false, true,
					        "flightNum,departure,arrival,date,time,duration,planeId");
		}
		return flight;
	}

	public static boolean deleteFlight(Database db, int flightNum) {
		if (!db.getFlights().containsKey(flightNum)) {
			return false;
		}
		db.getFlights().remove(flightNum);
		FileOp.saveFile("src/flights.csv", db.getFlights().values(), false, true,
				        "flightNum,departure,arrival,date,time,duration,planeId");
		return true;
	}
}