package service_management;
import flightManagment.*;
import java.util.*;
import reservation_ticketing.*;
import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public class FlightManager {
	
	//first functions create, update and delete flights
	
	public static void createFlight(int flightNum,String departurePlace,String arrivalPlace,LocalDate date,LocalTime hour,Duration duration) {
			//extract data from gui and create flites make sure to save the file before exiting program
		//system needs to set a plane for the user not the user choose 
			
		/*
		 Plane plane = planeService.assignPlane(route, date, expectedPassengers);
		
		Flight flight = new Flight(
		    flightNum,
		    from,
		    to,
		    date,
		    time,
		    duration,
		    plane
		);
 
	  
	 
		 public Plane assignPlane(String route, int expectedPassengers) {
		        for (Plane p : planes.values()) {
		            if (p.getCapacity() >= expectedPassengers && p.isAvailable()) {
		                return p;
		            }
		        }
		*/
		
		
		
	}
	public static Database extractFileData() throws FileNotFoundException {
		//call the file io class functions and create the flights with the input from the csv files
		//since the FileOp class is a helper class not a object type class its functions should be static as well(it has an empty constructor)
		Database data = new Database();
		
		data.planes = FileOp.getPlaneData("/Users/mo/Desktop/AirlineManagment/src/planes.csv");
		data.flights = FileOp.getFlightData("/Users/mo/Desktop/AirlineManagment/src/flights.csv",data.planes);
		data.passengers = FileOp.getPassengerData("/Users/mo/Desktop/AirlineManagment/src/passengers.csv");
		data.reservations = FileOp.getReservationData("/Users/mo/Desktop/AirlineManagment/src/reservations.csv",data.flights,data.passengers);
		data.tickets = FileOp.getTicketData("/Users/mo/Desktop/AirlineManagment/src/tickets.csv",data.reservations,data.flights);
		return data;
		
	}
	public static Flight createFlight(int flightNum,String departurePlace,String arrivalPlace,LocalDate date,LocalTime hour,Duration duration,Plane plane,Database db) {
		Flight flight = new Flight(flightNum,departurePlace,arrivalPlace,date,hour,duration,plane);
		db.flights.put(flightNum, flight);
		FileOp.saveFile("/Users/mo/Desktop/AirlineManagment/src/flights.csv", db.flights.values(),false,true,
				        "flightNum,departure,arrival,date,time,duration,planeId");
		return flight;
	}
	public static Plane createPlane(int planeId,String model,int capacity,int rows,Database db) {
		Plane plane = new Plane(planeId,model,capacity,rows);
		db.planes.put(planeId, plane);
		FileOp.saveFile("/Users/mo/Desktop/AirlineManagment/src/planes.csv", db.planes.values(),false,true,
				        "planeId,model,capacity,rows");
		return plane;
	}
	public static Flight updateFlight(Database db,Flight flight) {
		if(db.flights.containsKey(flight.getFlightNum())) {
			db.flights.put(flight.getFlightNum(), flight);
			FileOp.saveFile("/Users/mo/Desktop/AirlineManagment/src/flights.csv", db.flights.values(),false,true,
					        "flightNum,departure,arrival,date,time,duration,planeId");
		}
		return flight;
		
	}
	public static boolean deleteFlight(Database db,int flightNum) {
		//delete flight from db and update the csv file
		//return true if deleted false if not found
		if(!db.flights.containsKey(flightNum)) {
			return false;
		}
		db.flights.remove(flightNum);
		FileOp.saveFile("/Users/mo/Desktop/AirlineManagment/src/flights.csv", db.flights.values(),false,true,
				        "flightNum,departure,arrival,date,time,duration,planeId");
		return true;
	}
	 

}