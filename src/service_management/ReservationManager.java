package service_management;
import java.time.LocalDate;
import flightManagment.Flight;
import reservation_ticketing.Passenger;
import reservation_ticketing.Reservation;
import flightManagment.Seat;
import reservation_ticketing.Ticket;
public class ReservationManager {
	
	public static boolean canelReservation(String reservationId,Database database) {
		String key = String.valueOf(reservationId);
		if(database.reservations.containsKey(key)) {
			database.reservations.remove(key);
			// persist change to file
			FileOp.saveFile("/Users/mo/Desktop/AirlineManagment/src/reservations.csv", database.reservations.values(), false, true,
						"reservationCode,flightNum,passengerId,seatNum,dateOfReservation");
			return true;
		}
		return false;
		
	}
	public static Reservation createReservation(Flight flight,Passenger passenger,Seat seat,LocalDate date,int seatLvl,Database database) {
		Reservation reservation = new Reservation(flight,passenger,seat,date);
		seat.setLevel(seatLvl);
		database.reservations.put(String.valueOf(reservation.getReservationCode()), reservation);
		FileOp.saveFile("/Users/mo/Desktop/AirlineManagment/src/reservations.csv", database.reservations.values(),false,true,
						"reservationCode,flightNum,passengerId,seatNum,dateOfReservation");
		return reservation;
	}
	public static Ticket issueTicket(Reservation reservation,double price,int baggaeWeight,Database database) {
		Ticket ticket = new Ticket(reservation,price,baggaeWeight);
		database.tickets.put(ticket.getTicketId(), ticket);
		reservation.getFlight().addTicket(ticket);
		FileOp.saveFile("/Users/mo/Desktop/AirlineManagment/src/tickets.csv", database.tickets.values(),false,true,
						"ticketNum,reservationCode,price,baggaeWeight");
		return ticket;
	}
}