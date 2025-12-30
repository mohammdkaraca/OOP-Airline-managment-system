package service_management;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import flightManagment.Flight;
import reservation_ticketing.Passenger;

public class RandomSeatTest {
	
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
	
	public static void multiThredTest(boolean syncType, Flight flight) throws InterruptedException, FileNotFoundException {
		// Use the provided syncType (true => synchronized, false => unsynchronized)
		List<String> passengerLines = new ArrayList<>();
		
		// Changed to relative path for portability
		try (Scanner sc = new Scanner(new File("src/passengers.csv"))) {
			while (sc.hasNextLine()) {
				String line = sc.nextLine().trim();
				if (line.isEmpty()) continue;
				passengerLines.add(line);
			}
		}

		// Cap the threads at 91 or the number of passengers found
		int toCreate = Math.min(91, passengerLines.size());
		List<Thread> threads = new ArrayList<>();

		// Create passenger threads
		for (int i = 0; i < toCreate; i++) {
			String line = passengerLines.get(i);
			String[] d = line.split(",");
			
			if (d.length < 4 || !isInteger(d[0])) {
				// System.out.println("Skipping invalid/heading passenger row: " + line);
				continue;
			}
			try {
				Passenger p = new Passenger(syncType, Integer.parseInt(d[0]), d[1], d[2], Long.parseLong(d[3]), flight);
				Thread t = new Thread(p);
				threads.add(t);
				t.start();
			} catch (Exception e) {
				System.out.println("Error parsing passenger row: " + line + " -> " + e.getMessage());
			}
		}

		// Wait for all threads to finish
		for (Thread t : threads) {
			t.join();
		}

		System.out.println("Simulation Complete.");
		System.out.println("Reserved seats: " + flight.getPlane().getFulledSeatsCount());
		System.out.println("Empty seats: " + flight.getPlane().getEmptySeatsCount());
	}
}