package reservation_ticketing;

public class Ticket {
	private int ticketId;
	private Reservation reservation;
	private double price;
	private int baggageAllowance;
	private String reservationCode;

	public Ticket(int ticketId, Reservation reservation, double price, int baggageAllowance) {
		this.ticketId = ticketId;
		this.reservation = reservation;
		this.price = price;
		this.baggageAllowance = baggageAllowance;
	}

	public Ticket(Reservation reservation, double price, int baggageAllowance) {
		this.ticketId = (int)(Math.random() * 100000); // Generate a random ticket ID
		this.reservation = reservation;
		this.price = price;
		this.baggageAllowance = baggageAllowance;
	}

	public String getReservationCode() {
		return reservationCode;
	}

	public void setReservationCode(String reservationCode) {
		this.reservationCode = reservationCode;
	}

	public int getTicketId() {
		return ticketId;
	}

	public void setTicketId(int ticketId) {
		this.ticketId = ticketId;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getBaggageAllowance() {
		return baggageAllowance;
	}

	public void setBaggageAllowance(int baggageAllowance) {
		this.baggageAllowance = baggageAllowance;
	}
	
	@Override
	public String toString() {
	    // Uses the reservation object to get the code, which is safer than the local field
	    return getTicketId() + "," +
	           (getReservation() != null ? getReservation().getReservationCode() : "N/A") + "," +
	           getPrice() + "," +
	           getBaggageAllowance();
	}
}