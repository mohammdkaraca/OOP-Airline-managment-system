package service_management;
import java.util.*;
import flightManagment.Flight;
import flightManagment.Plane;
import reservation_ticketing.Reservation;
import reservation_ticketing.Ticket;
import reservation_ticketing.Passenger;
import java.util.HashMap;



	public class Database implements Runnable {
	    // Key: String (Unique ID), Value: Object
	    private Map<Long, Passenger> passengers;
	    private Map<Integer, Flight> flights;
	    Map<Integer, Ticket> tickets;
	    private Map<String, Reservation> reservations;
	    private Map<Integer, Plane> planes;
	    private double totalOccupancy;

	    public Database() {
	        setPassengers(new HashMap<>());
	        setFlights(new HashMap<>());
	        tickets = new HashMap<>();
	        setReservations(new HashMap<>());
	        setPlanes(new HashMap<>());
	    }

	public Map<Integer, Flight> getFlights() {
		return flights;
	}

	public void setFlights(Map<Integer, Flight> flights) {
		this.flights = flights;
	}

	public Map<Long, Passenger> getPassengers() {
		return passengers;
	}

	public void setPassengers(Map<Long, Passenger> passengers) {
		this.passengers = passengers;
	}

	public Map<String, Reservation> getReservations() {
		return reservations;
	}

	public void setReservations(Map<String, Reservation> reservations) {
		this.reservations = reservations;
	}

	public Map<Integer, Plane> getPlanes() {
		return planes;
	}

	public void setPlanes(Map<Integer, Plane> planes) {
		this.planes = planes;
	}

	public double getTotalOccupancy() {
		return this.totalOccupancy;
	}

	@Override
	public void run() {
		// Compute average occupancy per flight. Use ticket-based occupancy when tickets exist; otherwise fall back to plane.fulledSeatsCount
		if (this.flights == null || this.flights.isEmpty()) {
			this.totalOccupancy = 0.0;
			return;
		}
		double total = 0.0;
		int n = 0;
		for (Flight f : this.flights.values()) {
			int capacity = 0;
			double occPercent = 0.0;
			if (f.getPlane() != null) capacity = f.getPlane().getCapacity();
			// prefer tickets count if available
			int ticketsCount = (f.getTicketList() != null) ? f.getTicketList().size() : 0;
			int fulled = (f.getPlane() != null) ? f.getPlane().getFulledSeatsCount() : 0;
			if (capacity > 0) {
				if (ticketsCount > 0) {
					occPercent = ((double) ticketsCount / capacity) * 100.0;
				} else {
					// fallback to plane counters
					occPercent = ((double) fulled / capacity) * 100.0;
				}
			} else {
				occPercent = 0.0;
			}
			total += occPercent;
			n++;
		}
		this.totalOccupancy = (n > 0) ? (total / n) : 0.0;
		// keep flights reference as before
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

    public Map<Integer, Ticket> getTickets() {
		return tickets;
	}
	}