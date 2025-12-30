package service_management;

import java.time.LocalDate;
import flightManagment.Flight;
import flightManagment.Plane;
import flightManagment.Seat;
import reservation_ticketing.Passenger;
import reservation_ticketing.Reservation;
import reservation_ticketing.Ticket;

public class ReservationManager {
	
	public static boolean canelReservation(String reservationId, Database database) {
        String key = String.valueOf(reservationId);
        
        // Check if reservation exists
        if (database.getReservations().containsKey(key)) {
            
            // 1. Update Plane Capacity (Free up the seat)
            Reservation res = database.getReservations().get(key);
            if (res.getFlight() != null && res.getFlight().getPlane() != null) {
                Plane plane = res.getFlight().getPlane();
                // Logic: Increasing empty seats. 
                // Note: ideally the Seat object's reservedStatus should also be set to false here,
                // but your Seat.java handles counters inside setReservedStatus too. 
                // To be safe and consistent with your Seat logic:
                if (res.getSeat() != null) {
                    res.getSeat().setReservedStatus(false, plane); 
                }
            }

            // 2. Remove the Reservation from Map
            database.getReservations().remove(key);
            FileOp.saveFile("src/reservations.csv", database.getReservations().values(), false, true,
                            "reservationCode,flightNum,passengerId,seatNum,dateOfReservation");

            // 3. Find and Remove the Linked Ticket (Cascading Delete)
            Integer ticketIdToRemove = null;
            if (database.tickets != null) {
                for (Ticket t : database.tickets.values()) {
                    if (t.getReservation() != null && t.getReservation().getReservationCode().equals(key)) {
                        ticketIdToRemove = t.getTicketId();
                        break; 
                    }
                }
            }

            // 4. Save updated Tickets file
            if (ticketIdToRemove != null) {
                database.tickets.remove(ticketIdToRemove);
                FileOp.saveFile("src/tickets.csv", database.tickets.values(), false, true,
                                "ticketNum,reservationCode,price,baggaeWeight");
            }

            return true;
        }
        return false;
	}

	public static Reservation createReservation(Flight flight, Passenger passenger, Seat seat, LocalDate date, int seatLvl, Database database) {
		Reservation reservation = new Reservation(flight, passenger, seat, date);
		seat.setLevel(seatLvl);
		database.getReservations().put(String.valueOf(reservation.getReservationCode()), reservation);
		
		FileOp.saveFile("src/reservations.csv", database.getReservations().values(), false, true,
						"reservationCode,flightNum,passengerId,seatNum,dateOfReservation");
		return reservation;
	}

	public static Ticket issueTicket(Reservation reservation, double price, int baggaeWeight, Database database) {
		Ticket ticket = new Ticket(reservation, price, baggaeWeight);
		database.tickets.put(ticket.getTicketId(), ticket);
		reservation.getFlight().addTicket(ticket);
		
		FileOp.saveFile("src/tickets.csv", database.tickets.values(), false, true,
						"ticketNum,reservationCode,price,baggaeWeight");
		return ticket;
	}
}