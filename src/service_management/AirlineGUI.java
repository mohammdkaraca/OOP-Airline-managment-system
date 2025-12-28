package service_management;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import flightManagment.Flight;
import reservation_ticketing.Passenger;
import reservation_ticketing.Reservation;
import java.util.Map;
import flightManagment.Plane;
import flightManagment.Seat;
import java.time.*;

public class AirlineGUI extends JFrame {

    private Database database;
    private DefaultTableModel flightsTableModel;
    private JTable flightsTable;
    private DefaultTableModel reservationsTableModel; // track for refresh
    private JTable reservationsTable;

    public AirlineGUI(Database database) {
        this.database = database;
        setTitle("Airline Reservation & Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // 1. Flight Management Tab
        JPanel flightPanel = createFlightPanel();
        tabbedPane.addTab("Flight Search & View", flightPanel);

        // 2. Reservation Management Tab
        JPanel reservationPanel = createReservationPanel();
        tabbedPane.addTab("Reservations", reservationPanel);

        // 3. Admin / Simulation Tab (For Multithreading tasks)
        JPanel adminPanel = createAdminPanel();
        tabbedPane.addTab("Admin & Simulation", adminPanel);

        add(tabbedPane);
    }

    private void refreshFlightsTable() {
        if (flightsTableModel == null) return;
        flightsTableModel.setRowCount(0);
        if (database.flights != null) {
            for (Flight f : database.flights.values()) {
                Object[] row = {
                    f.getFlightNum(),
                    f.getDeparturePlace(),
                    f.getArrivalPlace(),
                    f.getDate(),
                    f.getHour(),
                    f.getDuration(),
                    (f.getPlane() != null) ? f.getPlane().getPlaneModel() : "N/A"
                };
                flightsTableModel.addRow(row);
            }
        }
    }

    private JPanel createFlightPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.add(new JLabel("Current Flights in Database:"));
        panel.add(header, BorderLayout.NORTH);

        // Table Setup
        String[] columnNames = {"Flight Num", "From", "To", "Date", "Time", "Duration", "Plane Model"};
        flightsTableModel = new DefaultTableModel(columnNames, 0);

        // Populate Table from HashMap
        refreshFlightsTable();

        flightsTable = new JTable(flightsTableModel);
        panel.add(new JScrollPane(flightsTable), BorderLayout.CENTER);

        // Bottom Controls (Placeholder for Search)
        JPanel bottom = new JPanel();
        JButton refreshBtn = new JButton("Refresh List");
        JButton bookBtn = new JButton("Book Selected Flight");
        bottom.add(refreshBtn);
        bottom.add(bookBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        // Refresh button action: repopulate table
        refreshBtn.addActionListener(e -> refreshFlightsTable());

        // Book button action: implement booking flow
        bookBtn.addActionListener(e -> {
            int sel = flightsTable.getSelectedRow();
            if (sel == -1) {
                JOptionPane.showMessageDialog(this, "Please select a flight to book.", "No Flight Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Object flightObj = flightsTableModel.getValueAt(sel, 0);
            int flightNum;
            try {
                flightNum = Integer.parseInt(flightObj.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Selected flight number is invalid.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Flight selectedFlight = database.flights.get(flightNum);
            if (selectedFlight == null) {
                JOptionPane.showMessageDialog(this, "Flight data not found in database.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 1) Ask for passenger info using a custom panel
            JTextField tfId = new JTextField();
            JTextField tfName = new JTextField();
            JTextField tfSurname = new JTextField();
            JTextField tfBaggage = new JTextField();
            JTextField tfContact = new JTextField();
            String[] classes = {"Economy", "Business"};
            JComboBox<String> cbClass = new JComboBox<>(classes);

            JPanel inputPanel = new JPanel(new GridLayout(0, 2));
            inputPanel.add(new JLabel("ID (numeric):")); inputPanel.add(tfId);
            inputPanel.add(new JLabel("Name:")); inputPanel.add(tfName);
            inputPanel.add(new JLabel("Surname:")); inputPanel.add(tfSurname);
            inputPanel.add(new JLabel("Baggage weight (kg):")); inputPanel.add(tfBaggage);
            inputPanel.add(new JLabel("Contact number:")); inputPanel.add(tfContact);
            inputPanel.add(new JLabel("Class:")); inputPanel.add(cbClass);

            int res = JOptionPane.showConfirmDialog(this, inputPanel, "Passenger Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res != JOptionPane.OK_OPTION) return; // cancelled

            // Validate inputs
            long passengerId;
            double baggageWeight;
            String name = tfName.getText().trim();
            String surname = tfSurname.getText().trim();
            String contact = tfContact.getText().trim();
            int seatClassIndex = cbClass.getSelectedIndex() == 1 ? 1 : 0;

            try {
                passengerId = Long.parseLong(tfId.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID. Must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                baggageWeight = Double.parseDouble(tfBaggage.getText().trim());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid baggage weight. Must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2) Seat selection dialog
            Plane plane = selectedFlight.getPlane();
            if (plane == null) {
                JOptionPane.showMessageDialog(this, "Selected flight has no assigned plane.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedSeat = showSeatSelectionDialog(plane);
            if (selectedSeat == null) {
                // user cancelled seat selection
                return;
            }

            // 3) Calculate price
            double durationHours = 0.0;
            try {
                Duration dur = selectedFlight.getDuration();
                if (dur != null) {
                    durationHours = dur.toMinutes() / 60.0;
                }
            } catch (Exception ex) {
                // fallback
                durationHours = 0.0;
            }

            double finalPrice = CalculatePrice.calculateSeatPrice(baggageWeight, seatClassIndex, durationHours, plane, selectedSeat);

            // 4) Show price and ask for confirmation
            int conf = JOptionPane.showConfirmDialog(this, String.format("Final price for seat %s: $%.2f\nDo you confirm the reservation?", selectedSeat, finalPrice), "Confirm Reservation", JOptionPane.YES_NO_OPTION);
            if (conf != JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Reservation cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Passenger passenger = new Passenger(passengerId, name, surname, Long.parseLong(contact));
            database.passengers.put(passengerId, passenger); 
            FileOp.saveFile("/Users/mo/Desktop/AirlineManagment/src/passengers.csv", database.passengers.values(), false, true,
							 "passengerId,name,surname,contactNumber");
            Reservation r = ReservationManager.createReservation(database.flights.get(flightNum), passenger, plane.getSeatByNumber(selectedSeat), 
            		                             LocalDate.now(), seatClassIndex, database);
            ReservationManager.issueTicket(r, finalPrice, (int)baggageWeight, database);

            // 5) Print all the data including confirmation timestamp
            LocalDateTime confirmedAt = LocalDateTime.now();
            System.out.println("[RESERVATION CONFIRMED]");
            System.out.println("  Flight Num: " + flightNum);
            System.out.println("  Passenger ID: " + passengerId);
            System.out.println("  Name: " + name);
            System.out.println("  Surname: " + surname);
            System.out.println("  Contact: " + contact);
            System.out.println("  Baggage (kg): " + baggageWeight);
            System.out.println("  Class: " + (seatClassIndex == 1 ? "Business" : "Economy"));
            System.out.println("  Seat: " + selectedSeat);
            System.out.println(String.format("  Price: $%.2f", finalPrice));
            System.out.println("  Confirmed At: " + confirmedAt.toString());

            // Optionally mark the seat reserved in-memory so subsequent seat dialog shows it greyed out
            Seat s = plane.getSeatByNumber(selectedSeat);
            if (s != null) s.setReservedStatus(true);

            // Clear passenger input fields so the dialog inputs are empty next time
            tfId.setText("");
            tfName.setText("");
            tfSurname.setText("");
            tfBaggage.setText("");
            tfContact.setText("");
            cbClass.setSelectedIndex(0);

            JOptionPane.showMessageDialog(this, "Reservation confirmed and printed to console.", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Clear any persistent input fields used in admin create panels (if still visible)
            clearAdminCreationFields();

            // Refresh flights/reservations views
            refreshFlightsTable();
            refreshReservationsTable();
        });

        return panel;
    }

    private JPanel createReservationPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.add(new JLabel("Active Reservations:"));
        panel.add(header, BorderLayout.NORTH);

        // Table Setup
        String[] columnNames = {"Res Code", "Passenger Name", "Flight Num", "Seat", "Date"};
        reservationsTableModel = new DefaultTableModel(columnNames, 0);

        // Populate Table from HashMap
        refreshReservationsTable();

        reservationsTable = new JTable(reservationsTableModel);
        panel.add(new JScrollPane(reservationsTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton btnCancelRes = new JButton("Cancel Reservation");
        JButton btnRefresh = new JButton("Refresh");
        bottom.add(btnRefresh);
        bottom.add(btnCancelRes);
        panel.add(bottom, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> refreshReservationsTable());

        btnCancelRes.addActionListener(e -> {
            int sel = reservationsTable.getSelectedRow();
            String idStr = null;
            if (sel != -1) {
                idStr = reservationsTableModel.getValueAt(sel, 0).toString();
            } else {
                idStr = JOptionPane.showInputDialog(this, "Enter reservation ID to cancel:", "Cancel Reservation", JOptionPane.PLAIN_MESSAGE);
            }
            if (idStr == null || idStr.trim().isEmpty()) return;
            try {
                //int resId = Integer.parseInt(idStr.trim());
                boolean ok = ReservationManager.canelReservation(idStr.trim(), database);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Reservation canceled.", "Canceled", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Reservation not found or could not be canceled.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                refreshReservationsTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Reservation ID must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    private void refreshReservationsTable() {
        if (reservationsTableModel == null) return;
        reservationsTableModel.setRowCount(0);
        if (database.reservations != null) {
            for (Reservation r : database.reservations.values()) {
                Object[] row = {
                    r.getReservationCode(),
                    (r.getPassenger() != null) ? r.getPassenger().getName() : "Unknown",
                    (r.getFlight() != null) ? r.getFlight().getFlightNum() : "Unknown",
                    (r.getSeat() != null) ? r.getSeat().getSeatNum() : "Unknown",
                    r.getDateOfReservation()
                };
                reservationsTableModel.addRow(row);
            }
        }
    }

    // We'll keep references to the admin panel fields so we can clear them from other methods
    private JTextField admin_tfPlaneId, admin_tfModel, admin_tfCapacity, admin_tfManufacturer;
    private JTextField admin_tfFlightNum, admin_tfFrom, admin_tfTo, admin_tfDate, admin_tfTime, admin_tfDuration, admin_tfPlaneAssign;

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(null); // Absolute positioning for specific layout needs

        JLabel title = new JLabel("System Simulations (Multithreading)");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBounds(20, 20, 300, 30);
        panel.add(title);

        // --- Scenario 1: Seat Reservation Simulation ---
        JPanel sim1 = new JPanel();
        sim1.setBorder(BorderFactory.createTitledBorder("Scenario 1: Concurrent Seat Reservation"));
        sim1.setBounds(20, 70, 400, 200);
        sim1.setLayout(new BoxLayout(sim1, BoxLayout.Y_AXIS));

        JCheckBox syncCheck = new JCheckBox("Enable Synchronization");
        JButton runSimBtn = new JButton("Run Seat Simulation");
        JLabel simResult = new JLabel("Status: Idle");

        sim1.add(syncCheck);
        sim1.add(Box.createVerticalStrut(10));
        sim1.add(runSimBtn);
        sim1.add(Box.createVerticalStrut(10));
        sim1.add(simResult);

        panel.add(sim1);

        // --- Scenario 2: Report Generation ---
        JPanel sim2 = new JPanel();
        sim2.setBorder(BorderFactory.createTitledBorder("Scenario 2: Async Report Generation"));
        sim2.setBounds(450, 70, 400, 200);
        sim2.setLayout(new BoxLayout(sim2, BoxLayout.Y_AXIS));

        JButton reportBtn = new JButton("Generate Occupancy Report");
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);

        sim2.add(reportBtn);
        sim2.add(Box.createVerticalStrut(20));
        sim2.add(progressBar);

        panel.add(sim2);

        // --- New: Plane Creation Panel ---
        JPanel planePanel = new JPanel();
        planePanel.setBorder(BorderFactory.createTitledBorder("Create New Plane"));
        planePanel.setBounds(20, 290, 400, 220);
        planePanel.setLayout(null);

        JLabel lblPlaneId = new JLabel("Plane ID:");
        lblPlaneId.setBounds(10, 20, 100, 25);
        planePanel.add(lblPlaneId);
        admin_tfPlaneId = new JTextField();
        admin_tfPlaneId.setBounds(120, 20, 250, 25);
        planePanel.add(admin_tfPlaneId);

        JLabel lblModel = new JLabel("Model:");
        lblModel.setBounds(10, 55, 100, 25);
        planePanel.add(lblModel);
        admin_tfModel = new JTextField();
        admin_tfModel.setBounds(120, 55, 250, 25);
        planePanel.add(admin_tfModel);

        JLabel lblCapacity = new JLabel("Capacity:");
        lblCapacity.setBounds(10, 90, 100, 25);
        planePanel.add(lblCapacity);
        admin_tfCapacity = new JTextField();
        admin_tfCapacity.setBounds(120, 90, 250, 25);
        planePanel.add(admin_tfCapacity);

        JLabel lblManufacturer = new JLabel("Manufacturer:");
        lblManufacturer.setBounds(10, 125, 100, 25);
        planePanel.add(lblManufacturer);
        admin_tfManufacturer = new JTextField();
        admin_tfManufacturer.setBounds(120, 125, 250, 25);
        planePanel.add(admin_tfManufacturer);

        JButton btnCreatePlane = new JButton("Create Plane");
        btnCreatePlane.setBounds(120, 160, 140, 30);
        planePanel.add(btnCreatePlane);

        // When clicked, print the entered plane data to the console and clear fields
        btnCreatePlane.addActionListener(e -> {
            String id = admin_tfPlaneId.getText().trim();
            String model = admin_tfModel.getText().trim();
            String capacity = admin_tfCapacity.getText().trim();
            String manufacturer = admin_tfManufacturer.getText().trim();

            System.out.println("[ADMIN] Create Plane requested:");
            System.out.println("  Plane ID: " + id);
            System.out.println("  Model: " + model);
            System.out.println("  Capacity: " + capacity);
            System.out.println("  Manufacturer: " + manufacturer);

            // Clear fields after printing
            admin_tfPlaneId.setText("");
            admin_tfModel.setText("");
            admin_tfCapacity.setText("");
            admin_tfManufacturer.setText("");
        });

        panel.add(planePanel);

        // --- New: Flight Creation Panel ---
        JPanel flightCreatePanel = new JPanel();
        flightCreatePanel.setBorder(BorderFactory.createTitledBorder("Create New Flight"));
        flightCreatePanel.setBounds(450, 290, 400, 270);
        flightCreatePanel.setLayout(null);

        JLabel lblFlightNum = new JLabel("Flight Num:");
        lblFlightNum.setBounds(10, 20, 100, 25);
        flightCreatePanel.add(lblFlightNum);
        admin_tfFlightNum = new JTextField();
        admin_tfFlightNum.setBounds(120, 20, 250, 25);
        flightCreatePanel.add(admin_tfFlightNum);

        JLabel lblFrom = new JLabel("From:");
        lblFrom.setBounds(10, 55, 100, 25);
        flightCreatePanel.add(lblFrom);
        admin_tfFrom = new JTextField();
        admin_tfFrom.setBounds(120, 55, 250, 25);
        flightCreatePanel.add(admin_tfFrom);

        JLabel lblTo = new JLabel("To:");
        lblTo.setBounds(10, 90, 100, 25);
        flightCreatePanel.add(lblTo);
        admin_tfTo = new JTextField();
        admin_tfTo.setBounds(120, 90, 250, 25);
        flightCreatePanel.add(admin_tfTo);

        JLabel lblDate = new JLabel("Date (YYYY-MM-DD):");
        lblDate.setBounds(10, 125, 140, 25);
        flightCreatePanel.add(lblDate);
        admin_tfDate = new JTextField();
        admin_tfDate.setBounds(150, 125, 220, 25);
        flightCreatePanel.add(admin_tfDate);

        JLabel lblTime = new JLabel("Time (HH:MM):");
        lblTime.setBounds(10, 160, 100, 25);
        flightCreatePanel.add(lblTime);
        admin_tfTime = new JTextField();
        admin_tfTime.setBounds(120, 160, 100, 25);
        flightCreatePanel.add(admin_tfTime);

        JLabel lblDuration = new JLabel("Duration (mins):");
        lblDuration.setBounds(230, 160, 120, 25);
        flightCreatePanel.add(lblDuration);
        admin_tfDuration = new JTextField();
        admin_tfDuration.setBounds(350, 160, 20, 25);
        flightCreatePanel.add(admin_tfDuration);

        JLabel lblPlaneAssign = new JLabel("Assign Plane ID:");
        lblPlaneAssign.setBounds(10, 195, 120, 25);
        flightCreatePanel.add(lblPlaneAssign);
        admin_tfPlaneAssign = new JTextField();
        admin_tfPlaneAssign.setBounds(130, 195, 240, 25);
        flightCreatePanel.add(admin_tfPlaneAssign);

        JButton btnCreateFlight = new JButton("Create Flight");
        btnCreateFlight.setBounds(130, 230, 140, 30);
        flightCreatePanel.setPreferredSize(new Dimension(400, 270));
        flightCreatePanel.add(btnCreateFlight);

        // Print entered flight data to the console when clicked and clear fields
        btnCreateFlight.addActionListener(e -> {
            String flightNum = admin_tfFlightNum.getText().trim();
            String from = admin_tfFrom.getText().trim();
            String to = admin_tfTo.getText().trim();
            String date = admin_tfDate.getText().trim();
            String time = admin_tfTime.getText().trim();
            String duration = admin_tfDuration.getText().trim();
            String assignedPlaneId = admin_tfPlaneAssign.getText().trim();

            System.out.println("[ADMIN] Create Flight requested:");
            System.out.println("  Flight Num: " + flightNum);
            System.out.println("  From: " + from);
            System.out.println("  To: " + to);
            System.out.println("  Date: " + date);
            System.out.println("  Time: " + time);
            System.out.println("  Duration: " + duration);
            System.out.println("  Assigned Plane ID: " + assignedPlaneId);

            // Clear fields after printing
            admin_tfFlightNum.setText("");
            admin_tfFrom.setText("");
            admin_tfTo.setText("");
            admin_tfDate.setText("");
            admin_tfTime.setText("");
            admin_tfDuration.setText("");
            admin_tfPlaneAssign.setText("");
        });

        panel.add(flightCreatePanel);

        // --- New: Update and Delete Flight controls ---
        JButton btnUpdateFlight = new JButton("Update Flight (by ID)");
        btnUpdateFlight.setBounds(20, 570, 200, 30);
        panel.add(btnUpdateFlight);

        JButton btnDeleteFlight = new JButton("Delete Flight (by ID)");
        btnDeleteFlight.setBounds(240, 570, 200, 30);
        panel.add(btnDeleteFlight);

        btnDeleteFlight.addActionListener(e -> {
            String idStr = JOptionPane.showInputDialog(this, "Enter Flight ID to delete:", "Delete Flight", JOptionPane.PLAIN_MESSAGE);
            if (idStr == null || idStr.trim().isEmpty()) return;
            try {
                int flightId = Integer.parseInt(idStr.trim());
                boolean ok = FlightManager.deleteFlight(database, flightId);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Flight deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Flight not found or could not be deleted.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                refreshFlightsTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Flight ID must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnUpdateFlight.addActionListener(e -> {
            String idStr = JOptionPane.showInputDialog(this, "Enter Flight ID to update:", "Update Flight", JOptionPane.PLAIN_MESSAGE);
            if (idStr == null || idStr.trim().isEmpty()) return;
            try {
                int flightId = Integer.parseInt(idStr.trim());
                Flight existing = database.flights.get(flightId);
                if (existing == null) {
                    JOptionPane.showMessageDialog(this, "Flight not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Build edit panel with existing values
                JTextField tfFlightNum = new JTextField(String.valueOf(existing.getFlightNum())); tfFlightNum.setEditable(false);
                JTextField tfFrom = new JTextField(existing.getDeparturePlace());
                JTextField tfTo = new JTextField(existing.getArrivalPlace());
                JTextField tfDate = new JTextField((existing.getDate() != null) ? existing.getDate().toString() : "");
                JTextField tfTime = new JTextField((existing.getHour() != null) ? existing.getHour().toString() : "");
                JTextField tfDuration = new JTextField((existing.getDuration() != null) ? String.valueOf(existing.getDuration().toMinutes()) : "");
                JTextField tfPlaneId = new JTextField((existing.getPlane() != null) ? String.valueOf(existing.getPlane().getPlaneID()) : "");

                JPanel editPanel = new JPanel(new GridLayout(0,2));
                editPanel.add(new JLabel("Flight Num (readonly):")); editPanel.add(tfFlightNum);
                editPanel.add(new JLabel("From:")); editPanel.add(tfFrom);
                editPanel.add(new JLabel("To:")); editPanel.add(tfTo);
                editPanel.add(new JLabel("Date (YYYY-MM-DD):")); editPanel.add(tfDate);
                editPanel.add(new JLabel("Time (HH:MM):")); editPanel.add(tfTime);
                editPanel.add(new JLabel("Duration (mins):")); editPanel.add(tfDuration);
                editPanel.add(new JLabel("Assign Plane ID:")); editPanel.add(tfPlaneId);

                int r = JOptionPane.showConfirmDialog(this, editPanel, "Edit Flight", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (r != JOptionPane.OK_OPTION) return;

                // Parse updated values
                String from = tfFrom.getText().trim();
                String to = tfTo.getText().trim();
                LocalDate date = null;
                LocalTime time = null;
                Duration duration = null;
                Plane plane = null;

                try { if (!tfDate.getText().trim().isEmpty()) date = LocalDate.parse(tfDate.getText().trim()); } catch (Exception pe) { JOptionPane.showMessageDialog(this, "Invalid date format.", "Input Error", JOptionPane.ERROR_MESSAGE); return; }
                try { if (!tfTime.getText().trim().isEmpty()) time = LocalTime.parse(tfTime.getText().trim()); } catch (Exception pe) { JOptionPane.showMessageDialog(this, "Invalid time format.", "Input Error", JOptionPane.ERROR_MESSAGE); return; }
                try { if (!tfDuration.getText().trim().isEmpty()) duration = Duration.ofMinutes(Long.parseLong(tfDuration.getText().trim())); } catch (Exception pe) { JOptionPane.showMessageDialog(this, "Invalid duration.", "Input Error", JOptionPane.ERROR_MESSAGE); return; }
                try { if (!tfPlaneId.getText().trim().isEmpty()) plane = database.planes.get(Integer.parseInt(tfPlaneId.getText().trim())); } catch (Exception pe) { JOptionPane.showMessageDialog(this, "Invalid plane ID.", "Input Error", JOptionPane.ERROR_MESSAGE); return; }

                // Create a new Flight object with updated fields (maintain same flight number)
                Flight updated = new Flight(existing.getFlightNum(), from, to, date, time, duration, plane);
                FlightManager.updateFlight(database, updated);
                JOptionPane.showMessageDialog(this, "Flight updated.", "Updated", JOptionPane.INFORMATION_MESSAGE);
                refreshFlightsTable();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Flight ID must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return panel;
    }

    // Helper to clear admin creation fields from other actions
    private void clearAdminCreationFields() {
        if (admin_tfPlaneId != null) admin_tfPlaneId.setText("");
        if (admin_tfModel != null) admin_tfModel.setText("");
        if (admin_tfCapacity != null) admin_tfCapacity.setText("");
        if (admin_tfManufacturer != null) admin_tfManufacturer.setText("");

        if (admin_tfFlightNum != null) admin_tfFlightNum.setText("");
        if (admin_tfFrom != null) admin_tfFrom.setText("");
        if (admin_tfTo != null) admin_tfTo.setText("");
        if (admin_tfDate != null) admin_tfDate.setText("");
        if (admin_tfTime != null) admin_tfTime.setText("");
        if (admin_tfDuration != null) admin_tfDuration.setText("");
        if (admin_tfPlaneAssign != null) admin_tfPlaneAssign.setText("");
    }

    // Seat selection dialog returns selected seat string or null if cancelled
    private String showSeatSelectionDialog(Plane plane) {
        Seat[][] seatM = plane.getSeatM();
        int rows = seatM.length;
        int cols = (rows > 0) ? seatM[0].length : 0;

        JDialog dialog = new JDialog(this, "Select Seat", true);
        dialog.setLayout(new BorderLayout());

        JPanel grid = new JPanel(new GridLayout(rows, cols, 5, 5));

        final String[] chosen = {null};
        final JButton[] previous = {null};

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Seat seat = seatM[i][j];
                String seatNum = (seat != null) ? seat.getSeatNum() : (char)('A'+i) + String.valueOf(j+1);
                JButton btn = new JButton(seatNum);
                if (seat != null && seat.isReservedStatus()) {
                    btn.setEnabled(false);
                    btn.setBackground(Color.LIGHT_GRAY);
                }
                btn.addActionListener(e -> {
                    if (previous[0] != null) previous[0].setBackground(null);
                    btn.setBackground(Color.CYAN);
                    previous[0] = btn;
                    chosen[0] = seatNum;
                });
                grid.add(btn);
            }
        }

        dialog.add(new JScrollPane(grid), BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        JButton btnConfirm = new JButton("Confirm");
        JButton btnCancel = new JButton("Cancel");
        bottom.add(btnConfirm);
        bottom.add(btnCancel);
        dialog.add(bottom, BorderLayout.SOUTH);

        btnConfirm.addActionListener(e -> {
            if (chosen[0] == null) {
                JOptionPane.showMessageDialog(dialog, "Please select a seat before confirming.", "No Seat Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            dialog.dispose();
        });

        btnCancel.addActionListener(e -> {
            chosen[0] = null;
            dialog.dispose();
        });

        dialog.setSize(Math.min(600, cols * 80), Math.min(400, rows * 80));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        return chosen[0];
    }
}
