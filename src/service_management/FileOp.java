package service_management;

import java.util.*;
import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import flightManagment.Seat;
import flightManagment.Plane;
import flightManagment.Flight;
import reservation_ticketing.Passenger;
import reservation_ticketing.Reservation;
import reservation_ticketing.Ticket;

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

    public static Map<Integer, Flight> getFlightData(String fileName, Map<Integer, Plane> planes) throws FileNotFoundException {
        Map<Integer, Flight> flights = new HashMap<>();
        try (Scanner sc = new Scanner(new File(fileName))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] d = line.split(",");
                
                if (d.length < 7 || !isInteger(d[0]) || !isInteger(d[6])) {
                    // System.out.println("Skipping invalid/heading flight row: " + line);
                    continue;
                }
                try {
                    Plane originalPlane = planes.get(Integer.parseInt(d[6]));
                	Plane flightSpecificPlane = (originalPlane != null) ? originalPlane.getCopy() : null;
                    Flight f = new Flight(
                            Integer.parseInt(d[0]),
                            d[1],
                            d[2],
                            LocalDate.parse(d[3]),
                            LocalTime.parse(d[4]),
                            Duration.parse(d[5]),
                            flightSpecificPlane);
                    flights.put(f.getFlightNum(), f);
                } catch (Exception e) {
                    System.out.println("Error parsing flight row: " + line + " -> " + e.getMessage());
                }
            }
        }
        return flights;
    }

    public static Map<Long, Passenger> getPassengerData(String fileName) throws FileNotFoundException {
        Map<Long, Passenger> passengers = new HashMap<>();
        try (Scanner sc = new Scanner(new File(fileName))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] d = line.split(",");
                
                if (d.length < 4 || !isInteger(d[0])) {
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
                    System.out.println("Error parsing passenger row: " + line + " -> " + e.getMessage());
                }
            }
        }
        return passengers;
    }

    public static Map<Integer, Plane> getPlaneData(String fileName) throws FileNotFoundException {
        Map<Integer, Plane> planes = new HashMap<>();
        try (Scanner sc = new Scanner(new File(fileName))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] d = line.split(",");
                
                if (d.length < 4 || !isInteger(d[0])) {
                    continue;
                }
                try {
                    Plane plane = new Plane(
                            Integer.parseInt(d[0]),
                            d[1],
                            Integer.parseInt(d[2]),
                            Integer.parseInt(d[3])
                    );
                    planes.put(plane.getPlaneID(), plane);
                } catch (Exception e) {
                    System.out.println("Error parsing plane row: " + line + " -> " + e.getMessage());
                }
            }
        }
        return planes;
    }

    public static Map<String, Reservation> getReservationData(String fileName, Map<Integer, Flight> flights, Map<Long, Passenger> passengers) throws FileNotFoundException {
        Map<String, Reservation> reservations = new HashMap<>();
        try (Scanner sc = new Scanner(new File(fileName))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] d = line.split(",");
                
                if (d.length < 5 || !isInteger(d[1]) || !isInteger(d[2])) {
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
                        System.out.println("Invalid linking in reservation: " + Arrays.toString(d));
                        continue;
                    }
                    Seat seat = flight.getPlane().getSeatByNumber(seatNum);
                    if (seat == null) {
                        System.out.println("Seat not found: " + seatNum);
                        continue;
                    }
                    if (seat.isReservedStatus()) {
                        // Already reserved, skip or log
                        continue;
                    }
                    seat.setReservedStatus(true, flight.getPlane());
                    Reservation tmp = new Reservation(d[0], flight, passenger, seat, LocalDate.parse(d[4]));
                    reservations.put(reservationId, tmp);
                } catch (Exception e) {
                    System.out.println("Error parsing reservation row: " + line + " -> " + e.getMessage());
                }
            }
        }
        return reservations;
    }

    public static Map<Integer, Ticket> getTicketData(String fileName, Map<String, Reservation> reservations, Map<Integer, Flight> flights) throws FileNotFoundException {
        Map<Integer, Ticket> tickets = new HashMap<>();
        try (Scanner sc = new Scanner(new File(fileName))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] d = line.split(",");
                
                if (d.length < 4 || !isInteger(d[0])) {
                    continue;
                }
                try {
                    Reservation rs = reservations.get(d[1]);
                    if (rs == null) continue; 
                    
                    Ticket t = new Ticket(
                            Integer.parseInt(d[0]),
                            rs,
                            Double.parseDouble(d[2]),
                            Integer.parseInt(d[3])
                    );
                    tickets.put(t.getTicketId(), t);
                } catch (Exception e) {
                    System.out.println("Error parsing ticket row: " + line + " -> " + e.getMessage());
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
            if (writeHeader && (!fileExists || !append)) {
                writer.write(header + System.lineSeparator());
            }
            // 2. Write Data
            for (T item : data) {
                writer.write(item.toString() + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + fileName);
            e.printStackTrace();
        }
    }
}