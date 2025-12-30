package flightManagment;

public class Seat {
    
    public enum SeatClass {
        ECONOMY(0),
        BUSINESS(1);

        private final int index;

        SeatClass(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }

    private String seatNum;
    private double price;
    private boolean reservedStatus;
    private SeatClass level;

    public Seat(String seatNum) {
        this.seatNum = seatNum;
    }

    public Seat(String seatNum, double price, boolean reservedStatus, SeatClass level) {
        this.seatNum = seatNum;
        this.price = price;
        this.reservedStatus = reservedStatus;
        this.level = level;
    }

    public String getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(String seatNum) {
        this.seatNum = seatNum;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public boolean isReservedStatus() {
        return reservedStatus;
    }

    /**
     * Updates the reservation status and adjusts the plane's capacity counters
     * only if the status actually changes.
     */
    public void setReservedStatus(boolean reservedStatus, Plane plane) {
        boolean previous = this.reservedStatus;
        
        // Prevent double counting if status isn't changing
        if (previous == reservedStatus) {
            return;
        }

        if (reservedStatus) {
            // Marking seat as reserved
            plane.setFulledSeatsCount(plane.getFulledSeatsCount() + 1);
            if (plane.getEmptySeatsCount() > 0) {
                plane.setEmptySeatsCount(plane.getEmptySeatsCount() - 1);
            }
        } else {
            // Marking seat as empty (canceling reservation)
            if (plane.getFulledSeatsCount() > 0) {
                plane.setFulledSeatsCount(plane.getFulledSeatsCount() - 1);
            }
            plane.setEmptySeatsCount(plane.getEmptySeatsCount() + 1);
        }
        
        // IMPORTANT: Actually update the local variable
        this.reservedStatus = reservedStatus;
    }

    public SeatClass getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = (level == 0) ? SeatClass.ECONOMY : SeatClass.BUSINESS;
    }

    @Override
    public String toString() {
        return "Seat [seatNum=" + seatNum + ", price=" + price + ", reservedStatus=" + reservedStatus + ", level=" + level + "]";
    }
}