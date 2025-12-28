package service_management;
import java.util.*;
import flightManagment.Flight;
import flightManagment.Plane;
import reservation_ticketing.Reservation;
import reservation_ticketing.Ticket;
import reservation_ticketing.Passenger;
import java.util.HashMap;


	public class Database {
	    // Key: String (Unique ID), Value: Object
	    Map<Long, Passenger> passengers;
	    Map<Integer, Flight> flights;
	    Map<Integer, Ticket> tickets;
	    Map<String, Reservation> reservations;
	    Map<Integer, Plane> planes;

	    public Database() {
	        passengers = new HashMap<>();
	        flights = new HashMap<>();
	        tickets = new HashMap<>();
	        reservations = new HashMap<>();
	        planes = new HashMap<>();
	    }
	}
	

