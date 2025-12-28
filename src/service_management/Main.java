package service_management;

import java.io.FileNotFoundException;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class Main {

    public static void main(String[] args) {
        // Ensure GUI runs on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Initialize Manager
                //FlightManager manager = new FlightManager();
                System.out.println("Initializing Flight Manager...");

                // 2. Load Data (Make sure csv files exist in project root)
                // Note: The createFlight method in your code acts as the data loader
                Database db = FlightManager.extractFileData(); 

                if(db.flights.isEmpty() && db.planes.isEmpty()) {
                    System.out.println("Warning: No data loaded. Check CSV file paths.");
                } else {
                    System.out.println("Data loaded successfully.");
                    System.out.println("Flights loaded: " + db.flights.size());
                    System.out.println("Reservations loaded: " + db.reservations.size());
                }

                // 3. Launch GUI 
                AirlineGUI gui = new AirlineGUI(db);
                gui.setVisible(true);

            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, 
                    "CSV Files not found! \nPlease ensure planes.csv, flights.csv, etc. are in the project folder.", 
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