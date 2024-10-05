package it2a.failadona.tickets;
    
import java.sql.*;
import java.util.Scanner;

public class TicketBooking {


    public static Connection connectDB() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC"); 
            con = DriverManager.getConnection("jdbc:sqlite:Tickets.db"); 
            System.out.println("Connection Successful");
        } catch (Exception e) {
            System.out.println("Connection Failed: " + e);
        }
        return con;
    }


    public void addRecord(String sql, Object... values) {
        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            for (int i = 0; i < values.length; i++) {
                if (values[i] instanceof Integer) {
                    pstmt.setInt(i + 1, (Integer) values[i]);
                } else if ( values[i] instanceof Double) {
                    pstmt.setDouble(i + 1, (Double) values[i]); 
                } else {
                    pstmt.setString(i + 1, values[i].toString()); 
                }
            }

            pstmt.executeUpdate();
            System.out.println("Record added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding record: " + e.getMessage());
        }
    }


    public void addTicket() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter movie ticket name: ");
        String ticketName = sc.nextLine();
        System.out.print("Enter Quantity: ");
        int quantity = sc.nextInt();
        System.out.print("Enter Price: ");
        double price = sc.nextDouble();
        System.out.print("Discount if Morning: ");
        double morningDiscount = sc.nextDouble();
        System.out.print("Discount if Afternoon: ");
        double afternoonDiscount = sc.nextDouble();
        System.out.print("Discount if Evening: ");
        double eveningDiscount = sc.nextDouble();

        String sql = "INSERT INTO Tickets (ticket_name, quantity, price, morning_discount, afternoon_discount, evening_discount) VALUES (?, ?, ?, ?, ?, ?)";

        addRecord(sql, ticketName, quantity, price, morningDiscount, afternoonDiscount, eveningDiscount);
    }


public void updateTicket() {
    Scanner sc = new Scanner(System.in);
    System.out.print("Enter movie ticket name to update: ");
    String ticketName = sc.nextLine();

    String query = "SELECT * FROM Tickets WHERE ticket_name = ?";
    try (Connection conn = this.connectDB();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        pstmt.setString(1, ticketName);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
        
            System.out.println("Updating details for ticket: " + ticketName);

           
            System.out.print("Enter new Quantity: ");
            int newQuantity = sc.nextInt();
            System.out.print("Enter new Price: ");
            double newPrice = sc.nextDouble();
            System.out.print("Discount if Morning: ");
            double newMorningDiscount = sc.nextDouble();
            System.out.print("Discount if Afternoon: ");
            double newAfternoonDiscount = sc.nextDouble();
            System.out.print("Discount if Evening: ");
            double newEveningDiscount = sc.nextDouble();

           
            String updateSQL = "UPDATE Tickets SET quantity = ?, price = ?, morning_discount = ?, afternoon_discount = ?, evening_discount = ? WHERE ticket_name = ?";
            try (PreparedStatement updatePstmt = conn.prepareStatement(updateSQL)) {
                updatePstmt.setInt(1, newQuantity);
                updatePstmt.setDouble(2, newPrice);
                updatePstmt.setDouble(3, newMorningDiscount);
                updatePstmt.setDouble(4, newAfternoonDiscount);
                updatePstmt.setDouble(5, newEveningDiscount);
                updatePstmt.setString(6, ticketName);
                updatePstmt.executeUpdate();
                System.out.println("Ticket updated successfully!");
            }

        } else {
        
            System.out.println("No Ticket Movie");
        }
    } catch (SQLException e) {
        System.out.println("Error retrieving records: " + e.getMessage());
    }
}

  
    public void deleteTicket() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter movie ticket name to delete: ");
        String ticketName = sc.nextLine();

        String sql = "DELETE FROM Tickets WHERE ticket_name = ?";
        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ticketName);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Ticket deleted successfully!");
            } else {
               
                System.out.println("No Ticket Movie");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting ticket: " + e.getMessage());
        }
    }

    
    public void viewRecords(String sqlQuery, String[] columnHeaders, String[] columnNames) {
   
        if (columnHeaders.length != columnNames.length) {
            System.out.println("Error: Mismatch between column headers and column names.");
            return;
        }

        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sqlQuery);
             ResultSet rs = pstmt.executeQuery()) {

         
            StringBuilder headerLine = new StringBuilder();
            headerLine.append("--------------------------------------------------------------------------------\n| ");
            for (String header : columnHeaders) {
                headerLine.append(String.format("%-20s | ", header)); 
            }
            headerLine.append("\n--------------------------------------------------------------------------------");

            System.out.println(headerLine.toString());

      
            while (rs.next()) {
                StringBuilder row = new StringBuilder("| ");
                for (String colName : columnNames) {
                    String value = rs.getString(colName);
                    row.append(String.format("%-20s | ", value != null ? value : "")); 
                }
                System.out.println(row.toString());
            }
            System.out.println("--------------------------------------------------------------------------------");

        } catch (SQLException e) {
            System.out.println("Error retrieving records: " + e.getMessage());
        }
    }

   
    private void viewTickets() {
        String ticketsQuery = "SELECT * FROM Tickets";
        String[] ticketsHeaders = {"ID", "Ticket Name", "Quantity", "Price", "Morning Discount", "Afternoon Discount", "Evening Discount"};
        String[] ticketsColumns = {"id", "ticket_name", "quantity", "price", "morning_discount", "afternoon_discount", "evening_discount"};

        viewRecords(ticketsQuery, ticketsHeaders, ticketsColumns);
    }

   
    public void displayMenu() {
        Scanner sc = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Add Ticket");
            System.out.println("2. View Tickets");
            System.out.println("3. Update Ticket");
            System.out.println("4. Delete Ticket");
            System.out.println("5. Exit");

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    addTicket();
                    break;
                case 2:
                    viewTickets();
                    break;
                case 3:
                    updateTicket();
                    break;
                case 4:
                    deleteTicket();
                    break;
                case 5:
                    exit = true;
                    System.out.println("Exiting the program.");
                    break;
                default:
                    System.out.println("Invalid option. Please select again.");
            }
        }
    }

    public static void main(String[] args) {
        TicketBooking booking = new TicketBooking();
        booking.displayMenu(); 
    }
}
