package reservation_ticketing;
import flightManagment.Flight;
import flightManagment.Seat;
import java.time.*;
public class Reservation {
	private String reservationCode;
	private Flight flight;
	private Passenger passenger;
	private Seat seat;
	private LocalDate dateOfReservation;
	//added for now might change later
	private int flightNum;
	private int passengerId;
	private String seatNum;
	
		public Reservation(String reservationCode,Flight flight,Passenger passenger,Seat seat,LocalDate dateOfReservation) {
			this.reservationCode = reservationCode;
			this.flight = flight;
			this.passenger = passenger;
			this.seat = seat;
			this.dateOfReservation = dateOfReservation;
		}
		
		public Reservation(Flight flight,Passenger passenger,Seat seat,LocalDate dateOfReservation) {
			this.reservationCode = "R"+(int)(Math.random() * 100000); // Generate a random reservation code
			this.flight = flight;
			this.passenger = passenger;
			this.seat = seat;
			this.dateOfReservation = dateOfReservation;
			
		}

		public String getReservationCode() {
			return reservationCode;
		}

		public void setReservationCode(String reservationCode) {
			this.reservationCode = reservationCode;
		}

		public Flight getFlight() {
			return flight;
		}

		public void setFlight(Flight flight) {
			this.flight = flight;
		}

		public Passenger getPassenger() {
			return passenger;
		}

		public void setPassenger(Passenger passenger) {
			this.passenger = passenger;
		}

		public Seat getSeat() {
			return seat;
		}

		public void setSeat(Seat seat) {
			this.seat = seat;
		}

		public LocalDate getDateOfReservation() {
			return dateOfReservation;
		}

		public void setDateOfReservation(LocalDate dateOfReservation) {
			this.dateOfReservation = dateOfReservation;
		}
		
		public String toString() {
		    return getReservationCode() + "," +
		           getFlight().getFlightNum() + "," +
		           getPassenger().getPassengerId() + "," +
		           getSeat().getSeatNum() + "," +
		           getDateOfReservation();
		}

	
}
