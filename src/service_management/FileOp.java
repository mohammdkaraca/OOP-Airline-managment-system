package service_management;
import java.util.*;
import flightManagment.Seat;
import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import flightManagment.Plane;
import flightManagment.Flight;
import reservation_ticketing.Passenger;
import reservation_ticketing.Reservation;
import reservation_ticketing.Ticket;
@SuppressWarnings("unused")

public class FileOp {
    // Helper to detect integers
    private static boolean isInteger(String s) {
        if (s == null) return false;
        s = s.trim();
        if (s.isEmpty()) return false;
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static Map<Integer, Flight> getFlightData(String fileName,Map<Integer, Plane> planes)
            throws FileNotFoundException {

        Map<Integer, Flight> flights = new HashMap<>();
        try (Scanner sc = new Scanner(new File(fileName))) {

            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] d = line.split(",");
                // Expect first column to be numeric flight number; skip headers/invalid rows
                if (d.length < 7 || !isInteger(d[0]) || !isInteger(d[6])) {
                    System.out.println("Skipping invalid/heading flight row: " + line);
                    continue;
                }
                try {
                    Flight f = new Flight(
                            Integer.parseInt(d[0]),
                            d[1],
                            d[2],
                            LocalDate.parse(d[3]),
                            LocalTime.parse(d[4]),
                            Duration.parse(d[5]),planes.get(Integer.parseInt(d[6])));

                    flights.put(f.getFlightNum(), f);
                } catch (Exception e) {
                    System.out.println("Error parsing flight row, skipping: " + line + " -> " + e.getMessage());
                }
            }
        }
        return flights;
    }

    public static Map<Long, Passenger> getPassengerData(String fileName)
            throws FileNotFoundException {

        Map<Long, Passenger> passengers = new HashMap<>();
        try (Scanner sc = new Scanner(new File(fileName))) {

            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] d = line.split(",");
                // Expect first column passenger id numeric
                if (d.length < 4 || !isInteger(d[0])) {
                    System.out.println("Skipping invalid/heading passenger row: " + line);
                    continue;
                }

                try {
                    Passenger p = new Passenger(
                            Integer.parseInt(d[0]),
                            d[1],
                            d[2],
                            Long.parseLong(d[3])
                    );

                    passengers.put(p.getPassengerId(), p);
                } catch (Exception e) {
                    System.out.println("Error parsing passenger row, skipping: " + line + " -> " + e.getMessage());
                }
            }
        }
        return passengers;
    }

    public static Map<Integer, Plane> getPlaneData(String fileName)
            throws FileNotFoundException {

        Map<Integer, Plane> planes = new HashMap<>();

        try (Scanner sc = new Scanner(new File(fileName))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] d = line.split(",");
                // Expect first column plane id numeric
                if (d.length < 4 || !isInteger(d[0])) {
                    System.out.println("Skipping invalid/heading plane row: " + line);
                    continue;
                }

                try {
                    Plane plane = new Plane(
                            Integer.parseInt(d[0]), // planeID
                            d[1],                    // model
                            Integer.parseInt(d[2]), // capacity
                            Integer.parseInt(d[3])  // row count
                    );

                    planes.put(plane.getPlaneID(), plane);
                } catch (Exception e) {
                    System.out.println("Error parsing plane row, skipping: " + line + " -> " + e.getMessage());
                }
            }
        }
        return planes;
    }

    public static Map<String, Reservation> getReservationData(
            String fileName,
            Map<Integer, Flight> flights,
            Map<Long, Passenger> passengers)
            throws FileNotFoundException {

        Map<String, Reservation> reservations = new HashMap<>();

        try (Scanner sc = new Scanner(new File(fileName))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] d = line.split(",");
                // Expect at least 5 columns and flight/passenger ids to be numeric
                if (d.length < 5 || !isInteger(d[1]) || !isInteger(d[2])) {
                    System.out.println("Skipping invalid/heading reservation row: " + line);
                    continue;
                }

                try {
                    String reservationId = d[0];
                    int flightNum = Integer.parseInt(d[1]);
                    long passengerId = Long.parseLong(d[2]);
                    Flight flight = flights.get(flightNum);
                    String seatNum = d[3];
                    Passenger passenger = passengers.get(passengerId);

                    if (flight == null || passenger == null || flight.getPlane() == null) {
                    	System.out.println(flight +"     "+passenger +"       "+flight.getPlane());
                        System.out.println("Invalid reservation row: " + Arrays.toString(d));
                        continue;
                    }
                    Seat seat = flight.getPlane().getSeatByNumber(seatNum);
                    if (seat == null) {
                        System.out.println("Seat not found: " + seatNum);
                        continue;
                    }
                    if (seat.isReservedStatus()) {
                        System.out.println("Seat already reserved: " + seatNum);
                        continue;
                    }
                    seat.setReservedStatus(true);
                    Reservation tmp = new Reservation(d[0],flight,passenger,seat,LocalDate.parse(d[4]));
                    reservations.put(reservationId, tmp);
                } catch (Exception e) {
                    System.out.println("Error parsing reservation row, skipping: " + line + " -> " + e.getMessage());
                }
            }
        }
        return reservations;
    }

    public static Map<Integer, Ticket> getTicketData(String fileName,Map<String,Reservation> reservations,Map<Integer,Flight> flights)
            throws FileNotFoundException {

        Map<Integer, Ticket> tickets = new HashMap<>();
        try (Scanner sc = new Scanner(new File(fileName))) {

            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] d = line.split(",");
                // Expect ticket id numeric and at least 4 columns
                if (d.length < 4 || !isInteger(d[0])) {
                    System.out.println("Skipping invalid/heading ticket row: " + line);
                    continue;
                }
                try {
                    Reservation rs = reservations.get(d[1]);
                    Ticket t = new Ticket(
                            Integer.parseInt(d[0]),
                            rs,
                            Double.parseDouble(d[2]),
                            Integer.parseInt(d[3])
                    );

                    tickets.put(t.getTicketId(), t);
                } catch (Exception e) {
                    System.out.println("Error parsing ticket row, skipping: " + line + " -> " + e.getMessage());
                }
            }
        }
        return tickets;
    }

    public static <T> void saveFile(String fileName, Collection<T> data, boolean append, boolean writeHeader, String header) {
        File file = new File(fileName);
        boolean fileExists = file.exists() && file.length() > 0;

        try (FileWriter writer = new FileWriter(fileName, append)) {
            
            // 1. Write Header
            // We write the header if we are creating a new file OR if we are overwriting the old one.
            if (writeHeader && (!fileExists || !append)) {
                writer.write(header + System.lineSeparator());
            }

            // 2. Write Data
            for (T item : data) {
                // This relies on the class (Plane/Flight) having a proper toString() method
                writer.write(item.toString() + System.lineSeparator());
            }

        } catch (IOException e) {
            System.out.println("Error writing to file: " + fileName);
            e.printStackTrace();
        }
    }


}