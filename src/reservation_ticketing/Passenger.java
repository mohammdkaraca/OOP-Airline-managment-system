package reservation_ticketing;

public class Passenger {
	private long passengerId;
	private String name;
	private String surname;
	private long contactNum;
	
	public Passenger(long passengerId,String name,String surname,long contactNum) {
		this.passengerId = passengerId;
		this.name = name;
		this.surname = surname;
		this.contactNum = contactNum;
	}

	public long getPassengerId() {
		return passengerId;
	}

	public void setPassengerId(int passengerId) {
		 this.passengerId = passengerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public long getContactNum() {
		return contactNum;
	}

	public void setContactNum(long contactNum) {
		this.contactNum = contactNum;
	}
	
	public String toString() {
	    return getPassengerId() + "," +
	           getName() + "," +
	           getSurname() + "," +
	           getContactNum();
	}

	
}
