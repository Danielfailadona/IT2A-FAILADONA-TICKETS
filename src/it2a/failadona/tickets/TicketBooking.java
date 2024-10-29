package it2a.failadona.tickets;
    
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;


public class TicketBooking {

    static Config config = new Config(); // Instance of Config class for database operations
    static Scanner scan = new Scanner(System.in); // Scanner for user input

    public static void main(String[] args) {
        mainMenu();
    }

    // Method to clear the screen
    public static void clearScreen() {
        for (int x = 0; x < 50; x++) {
            System.out.println("\n");
        }
    }

    // Main menu
    public static void mainMenu() {
        while (true) {
            System.out.println("Menu:");
            System.out.println("1. Tickets");
            System.out.println("2. Customers");
            System.out.println("3. Exit");
            System.out.print("Select option: ");

            int choice = getIntInput(); // Helper method to get integer input
            switch (choice) {
                case 1:
                    ticketOptions();
                    break;
                case 2:
                    customerOptions();
                    break;
                case 3:
                    System.out.println("Exiting system...");
                    return;
                default:
                    System.out.println("Error: Option does not exist. Try again.");
            }
        }
    }

    // Ticket options menu
    public static void ticketOptions() {
        while (true) {
            System.out.println("Ticket Options:");
            System.out.println("1. Add Tickets");
            System.out.println("2. View Tickets");
            System.out.println("3. Update Tickets");
            System.out.println("4. Delete Tickets");
            System.out.println("5. Exit to Menu");

            int choice = getIntInput();
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
                    clearScreen();
                    return;
                default:
                    System.out.println("Error: Option does not exist. Try again.");
            }
        }
    }

    // Add a ticket
    public static void addTicket() {
        try {
            System.out.print("Enter movie name: ");
            String movieName = scan.nextLine();

            System.out.print("Enter maximum seats: ");
            int maxSeats = getIntInput();

            System.out.print("Enter price: ");
            double price = getDoubleInput();

            System.out.print("Enter morning discount: ");
            double morningDiscount = getDoubleInput();

            System.out.print("Enter afternoon discount: ");
            double afternoonDiscount = getDoubleInput();

            System.out.print("Enter evening discount: ");
            double eveningDiscount = getDoubleInput();

            String sql = "INSERT INTO tickets (movie_name, max_seats, price, morning_discount, afternoon_discount, evening_discount) VALUES (?, ?, ?, ?, ?, ?)";
            config.addRecord(sql, movieName, maxSeats, price, morningDiscount, afternoonDiscount, eveningDiscount);

            clearScreen();
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input. Try again.");
            scan.nextLine(); // Clear scanner buffer
        }
    }

    // View tickets
    public static void viewTickets() {
        String sql = "SELECT * FROM tickets";
        String[] headers = {"ID", "Movie", "Seats", "Price", "Morning", "Afternoon", "Evening"};
        String[] fields = {"id", "movie_name", "max_seats", "price", "morning_discount", "afternoon_discount", "evening_discount"};
        config.viewRecords(sql, headers, fields);

        System.out.println("Press any key to return to menu...");
        scan.nextLine();
        clearScreen();
    }

    // Update a ticket
    public static void updateTicket() {
        viewTickets(); // Show all tickets before updating

        try {
            System.out.print("Enter the ID of the movie to update: ");
            int id = getIntInput();

            System.out.print("Enter new movie name: ");
            String movieName = scan.nextLine();

            System.out.print("Enter new maximum seats: ");
            int maxSeats = getIntInput();

            System.out.print("Enter new price: ");
            double price = getDoubleInput();

            System.out.print("Enter new morning discount: ");
            double morningDiscount = getDoubleInput();

            System.out.print("Enter new afternoon discount: ");
            double afternoonDiscount = getDoubleInput();

            System.out.print("Enter new evening discount: ");
            double eveningDiscount = getDoubleInput();

            String sql = "UPDATE tickets SET movie_name = ?, max_seats = ?, price = ?, morning_discount = ?, afternoon_discount = ?, evening_discount = ? WHERE id = ?";
            config.updateRecord(sql, movieName, maxSeats, price, morningDiscount, afternoonDiscount, eveningDiscount, id);

            clearScreen();
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input. Try again.");
            scan.nextLine(); // Clear scanner buffer
        }
    }

    // Delete a ticket
    public static void deleteTicket() {
        viewTickets(); // Show all tickets before deleting

        try {
            System.out.print("Enter the ID of the movie to delete: ");
            int id = getIntInput();

            String sql = "DELETE FROM tickets WHERE id = ?";
            config.deleteRecord(sql, id);

            System.out.println("Ticket deleted successfully.");
            System.out.println("Press any key to return to menu...");
            scan.nextLine();
            clearScreen();
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input. Try again.");
            scan.nextLine(); // Clear scanner buffer
        }
    }

    // Customer options menu
    public static void customerOptions() {
        while (true) {
            System.out.println("Customer Options:");
            System.out.println("1. Add Customer");
            System.out.println("2. View Customers");
            System.out.println("3. Update Customer");
            System.out.println("4. Delete Customer");
            System.out.println("5. Exit to Menu");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    addCustomer();
                    break;
                case 2:
                    viewCustomers();
                    break;
                case 3:
                    updateCustomer();
                    break;
                case 4:
                    deleteCustomer();
                    break;
                case 5:
                    clearScreen();
                    return;
                default:
                    System.out.println("Error: Option does not exist. Try again.");
            }
        }
    }

    // Add a customer
    public static void addCustomer() {
        try {
            System.out.print("Enter customer's name: ");
            String customerName = scan.nextLine();

            viewTickets(); // Show all movies
            System.out.print("Enter the movie ID: ");
            int movieID = getIntInput();

            // Check if seats are available for the movie
            if (!config.checkSeatsAvailability(movieID)) {
                System.out.println("Seats are full. Returning to menu...");
                return;
            }

            System.out.print("Enter the number of tickets to buy: ");
            int ticketsBought = getIntInput();

            System.out.print("Enter M (morning), A (afternoon), or E (evening): ");
            char timeSlot = scan.nextLine().toUpperCase().charAt(0);

            double totalDue = config.calculateTotalDue(movieID, ticketsBought, timeSlot);
            System.out.printf("Total due: %.2f\n", totalDue);

            System.out.print("Enter payment: ");
            double payment = getDoubleInput();

            if (payment < totalDue) {
                System.out.println("Error: Insufficient payment. Try again.");
                return;
            }

            System.out.printf("Change: %.2f\n", payment - totalDue);

            String sql = "INSERT INTO customers (name, movie_id, tickets_bought, time_slot, total_due) VALUES (?, ?, ?, ?, ?)";
            config.addRecord(sql, customerName, movieID, ticketsBought, String.valueOf(timeSlot), totalDue);

            clearScreen();
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input. Try again.");
            scan.nextLine(); // Clear scanner buffer
        }
    }

    // View customers
    public static void viewCustomers() {
        String sql = "SELECT * FROM customers";
        String[] headers = {"ID", "Name", "Movie ID", "Tickets", "Time Slot", "Total Due"};
        String[] fields = {"id", "name", "movie_id", "tickets_bought", "time_slot", "total_due"};
        config.viewRecords(sql, headers, fields);

        System.out.println("Press any key to return to menu...");
        scan.nextLine();
        clearScreen();
    }

    // Update a customer
    public static void updateCustomer() {
        viewCustomers(); // Show all customers before updating

        try {
            System.out.print("Enter the customer ID to update: ");
            int id = getIntInput();

            System.out.print("Enter new customer name: ");
            String customerName = scan.nextLine();

            viewTickets(); // Show all movies
            System.out.print("Enter new movie ID: ");
            int movieID = getIntInput();

            // Check if seats are available for the movie
            if (!config.checkSeatsAvailability(movieID)) {
                System.out.println("Seats are full. Returning to menu...");
                return;
            }

            System.out.print("Enter new number of tickets to buy: ");
            int ticketsBought = getIntInput();

            System.out.print("Enter new M (morning), A (afternoon), or E (evening): ");
            char timeSlot = scan.nextLine().toUpperCase().charAt(0);

            double totalDue = config.calculateTotalDue(movieID, ticketsBought, timeSlot);
            System.out.printf("New total due: %.2f\n", totalDue);

            String sql = "UPDATE customers SET name = ?, movie_id = ?, tickets_bought = ?, time_slot = ?, total_due = ? WHERE id = ?";
            config.updateRecord(sql, customerName, movieID, ticketsBought, String.valueOf(timeSlot), totalDue, id);

            clearScreen();
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input. Try again.");
            scan.nextLine(); // Clear scanner buffer
        }
    }

    // Delete a customer
    public static void deleteCustomer() {
        viewCustomers(); // Show all customers before deleting

        try {
            System.out.print("Enter the customer ID to delete: ");
            int id = getIntInput();

            String sql = "DELETE FROM customers WHERE id = ?";
            config.deleteRecord(sql, id);

            System.out.println("Customer deleted successfully.");
            System.out.println("Press any key to return to menu...");
            scan.nextLine();
            clearScreen();
        } catch (InputMismatchException e) {
            System.out.println("Error: Invalid input. Try again.");
            scan.nextLine(); // Clear scanner buffer
        }
    }

    // Helper method to get integer input safely
    public static int getIntInput() {
        int input = -1;
        while (input == -1) {
            try {
                input = Integer.parseInt(scan.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a number.");
            }
        }
        return input;
    }

    // Helper method to get double input safely
    public static double getDoubleInput() {
        double input = -1;
        while (input == -1) {
            try {
                input = Double.parseDouble(scan.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a number.");
            }
        }
        return input;
    }
}


