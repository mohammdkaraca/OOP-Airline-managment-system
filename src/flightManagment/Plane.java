package flightManagment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import service_management.SeatManager;
//fix the issue of seats needed to be sorted or not
//as well as fixing the issue of having them filled in or not
//if not filled in we need to check according to reservedStatus
public class Plane {
	private int planeID;
	private String planeModel;
	private int capacity;
	private Seat seatM[][];
	private int colAmount;
	private Map<String,Seat> seatMap = new HashMap<>();
	
		public Plane(int planeId,String planeModel,int capacity,int seatAmount) {
			this.planeID = planeId;
			this.planeModel = planeModel;
			this.capacity = capacity;
			int col = capacity/seatAmount;
			this.seatM = new Seat[seatAmount][col];			
			
			seatMap = SeatManager.initializeSeats(seatAmount,col,seatM,seatMap);
			this.colAmount= seatAmount;
			
		}
		


		    public Seat getSeatByNumber(String seatNum) {
		        return seatMap.get(seatNum);
		    }
		

		public int getPlaneID() {
			return planeID;
		}

		public void setPlaneID(int planeID) {
			this.planeID = planeID;
		}

		public String getPlaneModel() {
			return planeModel;
		}

		public void setPlaneModel(String planeModel) {
			this.planeModel = planeModel;
		}

		public int getCapacity() {
			return capacity;
		}

		public void setCapacity(int capacity) {
			this.capacity = capacity;
		}

		public Seat[][] getSeatM() {
			return seatM;
		}

		public void setSeatM(Seat[][] seatM) {
			this.seatM = seatM;
		}
		public int getColAmount() {
			return colAmount;
		}

		
		public String toString() {
		    return getPlaneID() + "," +
		           getPlaneModel() + "," +
		           getCapacity() + "," +
		           this.colAmount;
		}

		
}
