package GUI;

import java.io.FileNotFoundException;
import javax.swing.SwingUtilities;

import service_management.*;

import javax.swing.JOptionPane;

public class Main {

    public static void main(String[] args) {
        // Ensure GUI runs on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Initialize Manager
                System.out.println("Initializing Flight Manager...");

                // 2. Load Data (Make sure csv files exist in project root/src)
                Database db = FlightManager.extractFileData(); 
                
                if(db.getFlights().isEmpty() && db.getPlanes().isEmpty()) {
                    System.out.println("Warning: No data loaded. Check CSV file paths.");
                } else {
                    System.out.println("Data loaded successfully.");
                    System.out.println("Flights loaded: " + db.getFlights().size());
                    System.out.println("Reservations loaded: " + db.getReservations().size());
                }

                // 3. Launch GUI 
                AirlineGUI gui = new AirlineGUI(db);
                gui.setVisible(true);

            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, 
                    "CSV Files not found! \nPlease ensure planes.csv, flights.csv, etc. are in the src folder.", 
                    "Initialization Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                    "An unexpected error occurred: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}