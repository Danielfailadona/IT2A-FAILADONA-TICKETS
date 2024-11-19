package it2a.failadona.tickets;

import java.sql.*;
import java.util.Scanner;

public class TicketBooking {

    private static final String DATABASE_URL = "jdbc:sqlite:Tickets.db";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection conn = connect()) {
            createTablesIfNotExists(conn);  // Ensure tables exist

            mainMenu(conn, scanner); // Run the main menu loop
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void mainMenu(Connection conn, Scanner scanner) {
        boolean exit = false;  // Flag to exit the menu loop
        while (!exit) {
            System.out.println("\n--- Movie Theater System ---");
            System.out.println("1. View Movies");
            System.out.println("2. Purchase Tickets");
            System.out.println("3. View Purchase History");
            System.out.println("4. Add New Movie");
            System.out.println("5. Update Movie Price");
            System.out.println("6. Add Showtime (Available Tickets)");
            System.out.println("7. Exit");

            System.out.print("Select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    try {
                        viewMovies(conn, scanner);
                    } catch (SQLException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        purchaseTicket(conn, scanner);
                    } catch (SQLException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 3:
                    try {
                        viewPurchaseHistory(conn);
                    } catch (SQLException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 4:
                    try {
                        addNewMovie(conn, scanner);
                    } catch (SQLException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 5:
                    try {
                        updateMoviePrice(conn, scanner);
                    } catch (SQLException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 6:
                    try {
                        insertAvailableTickets(conn, scanner);
                    } catch (SQLException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 7:
                    System.out.println("Exiting... Goodbye!");
                    exit = true;  // Set exit flag to true to break the loop
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }   
    private static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DATABASE_URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
        return conn;
    }

    private static void createTablesIfNotExists(Connection conn) throws SQLException {
        String createMoviesTable = "CREATE TABLE IF NOT EXISTS movies ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "title TEXT NOT NULL, "
                + "director TEXT, "
                + "release_year INTEGER, "
                + "genre TEXT, "
                + "rating REAL DEFAULT 0.0, "
                + "theater_type TEXT NOT NULL)";  // Added theater_type column

        String createPurchasesTable = "CREATE TABLE IF NOT EXISTS purchases ("
                + "purchase_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "movie_id INTEGER NOT NULL, "
                + "customer_name TEXT NOT NULL, "
                + "ticket_count INTEGER NOT NULL, "
                + "total_amount REAL NOT NULL, "
                + "purchase_date TEXT NOT NULL, "
                + "payment_status TEXT NOT NULL, "
                + "theater_type TEXT NOT NULL, "
                + "showtime TEXT NOT NULL, "
                + "FOREIGN KEY (movie_id) REFERENCES movies(id))";

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createMoviesTable);
            stmt.executeUpdate(createPurchasesTable);
        }
    }

    private static void viewMovies(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Select Theater Type:");
        System.out.println("1. Premium Theater");
        System.out.println("2. VIP Theater");
        System.out.print("Enter your choice (1 or 2): ");
        int theaterChoice = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        String theaterType = "";
        double ticketPrice = 0.0;

        if (theaterChoice == 1) {
            theaterType = "Premium Theater";
            ticketPrice = 12.0;
        } else if (theaterChoice == 2) {
            theaterType = "VIP Theater";
            ticketPrice = 20.0;
        } else {
            System.out.println("Invalid choice. Defaulting to Premium Theater.");
            theaterType = "Premium Theater";
            ticketPrice = 12.0;
        }

        // Modify the query to filter by the selected theater type
        String query = "SELECT id, title, director, release_year, genre, rating, theater_type FROM movies WHERE theater_type = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, theaterType);  // Set the selected theater type in the query
            try (ResultSet rs = stmt.executeQuery()) {

                System.out.println("\nMovies List for " + theaterType + ":");
                System.out.println("+-----+------------------------+-------------------+-------------+-------------------+--------+-------------------+");
                System.out.println("| ID  | Title                  | Director          | Year        | Genre             | Rating | Theater Type       |");
                System.out.println("+-----+------------------------+-------------------+-------------+-------------------+--------+-------------------+");

                if (!rs.isBeforeFirst()) {
                    System.out.println("|                      No movies found for " + theaterType + "                        |");
                } else {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String title = rs.getString("title");
                        String director = rs.getString("director");
                        int releaseYear = rs.getInt("release_year");
                        String genre = rs.getString("genre");
                        double rating = rs.getDouble("rating");
                        String movieTheaterType = rs.getString("theater_type");  // Fetch the theater type to confirm
                        System.out.printf("| %-3d | %-22s | %-17s | %-11d | %-17s | %-6.1f | %-17s |\n", 
                                          id, title, director, releaseYear, genre, rating, movieTheaterType);
                    }
                }
                System.out.println("+-----+------------------------+-------------------+-------------+-------------------+--------+-------------------+");
            }
        }
    }

    private static void addNewMovie(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter movie title: ");
        String title = scanner.nextLine().trim();

        System.out.print("Enter movie director: ");
        String director = scanner.nextLine().trim();

        System.out.print("Enter release year: ");
        int releaseYear = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter genre: ");
        String genre = scanner.nextLine().trim();

        System.out.print("Enter movie rating (e.g., 8.5): ");
        double rating = scanner.nextDouble();
        scanner.nextLine();  // Consume newline

        // Ask the user for the theater type
        System.out.println("Select the theater type for this movie:");
        System.out.println("1. Premium Theater");
        System.out.println("2. VIP Theater");
        System.out.print("Enter your choice (1 or 2): ");
        int theaterChoice = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        String theaterType = "";
        if (theaterChoice == 1) {
            theaterType = "Premium Theater";
        } else if (theaterChoice == 2) {
            theaterType = "VIP Theater";
        } else {
            System.out.println("Invalid choice. Defaulting to Premium Theater.");
            theaterType = "Premium Theater";
        }

        String insertMovieQuery = "INSERT INTO movies (title, director, release_year, genre, rating, theater_type) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insertMovieQuery)) {
            stmt.setString(1, title);
            stmt.setString(2, director);
            stmt.setInt(3, releaseYear);
            stmt.setString(4, genre);
            stmt.setDouble(5, rating);
            stmt.setString(6, theaterType);  // Add theater type to the database

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Movie added successfully to the database!");
            } else {
                System.out.println("Failed to add the movie.");
            }
        }
    }

private static void purchaseTicket(Connection conn, Scanner scanner) throws SQLException {
    System.out.println("Select Theater Type:");
    System.out.println("1. Premium Theater");
    System.out.println("2. VIP Theater");
    System.out.print("Enter your choice (1 or 2): ");
    int theaterChoice = scanner.nextInt();
    scanner.nextLine();  // Consume newline

    String theaterType = "";
    double ticketPrice = 0.0;

    // Set theater type and ticket price based on user choice
    if (theaterChoice == 1) {
        theaterType = "Premium Theater";
        ticketPrice = 12.0;
    } else if (theaterChoice == 2) {
        theaterType = "VIP Theater";
        ticketPrice = 20.0;
    } else {
        System.out.println("Invalid choice. Defaulting to Premium Theater.");
        theaterType = "Premium Theater";
        ticketPrice = 12.0;
    }

    // Fetch and display available movies in the selected theater type
    System.out.println("\n--- Available Movies in " + theaterType + " ---");
    String query = "SELECT id, title, director, release_year, genre, rating FROM movies WHERE theater_type = ?";
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, theaterType);
        try (ResultSet rs = stmt.executeQuery()) {
            System.out.println("+-----+------------------------+-------------------+-------------+-------------------+--------+");
            System.out.println("| ID  | Title                  | Director          | Year        | Genre             | Rating |");
            System.out.println("+-----+------------------------+-------------------+-------------+-------------------+--------+");

            if (!rs.isBeforeFirst()) {
                System.out.println("|                      No movies found for " + theaterType + "                        |");
            } else {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String director = rs.getString("director");
                    int releaseYear = rs.getInt("release_year");
                    String genre = rs.getString("genre");
                    double rating = rs.getDouble("rating");
                    System.out.printf("| %-3d | %-22s | %-17s | %-11d | %-17s | %-6.1f |\n",
                            id, title, director, releaseYear, genre, rating);
                }
            }
            System.out.println("+-----+------------------------+-------------------+-------------+-------------------+--------+");
        }
    }

    // Ask the user to select a movie by ID
    int movieId = -1;
    while (movieId <= 0) {
        System.out.print("\nEnter the ID of the movie you want to purchase tickets for: ");
        if (scanner.hasNextInt()) {
            movieId = scanner.nextInt();
            if (movieId <= 0) {
                System.out.println("Invalid movie ID. Please enter a valid ID.");
            }
        } else {
            scanner.next();  // Consume invalid input
            System.out.println("Invalid input. Please enter a number.");
        }
    }
    scanner.nextLine();  // Consume newline

    // Fetch available movie showing times for the selected movie
    String showtimesQuery = "SELECT id, showing_time, available_tickets FROM movie_showtimes WHERE movie_id = ?";
    boolean hasShowtimes = false;  // Flag to track if showtimes are available

    try (PreparedStatement stmt = conn.prepareStatement(showtimesQuery)) {
        stmt.setInt(1, movieId);
        try (ResultSet rs = stmt.executeQuery()) {
            System.out.println("\n--- Available Showing Times for Selected Movie ---");
            if (!rs.isBeforeFirst()) {
                System.out.println("No showing times available for this movie.");
                return; // Exit if no showtimes are available
            } else {
                System.out.println("+-----+---------------------+-------------------+");
                System.out.println("| ID  | Showing Time        | Available Tickets |");
                System.out.println("+-----+---------------------+-------------------+");
                while (rs.next()) {
                    int showtimeId = rs.getInt("id");
                    String showingTime = rs.getString("showing_time");
                    int availableTickets = rs.getInt("available_tickets");
                    System.out.printf("| %-3d | %-19s | %-17d |\n", showtimeId, showingTime, availableTickets);
                    hasShowtimes = true; // Set the flag to true if showtimes exist
                }
                System.out.println("+-----+---------------------+-------------------+");
            }
        }
    }

    if (!hasShowtimes) {
        System.out.println("No available showtimes for this movie. Would you like to choose a different movie? (yes/no)");
        String response = scanner.nextLine();
        if (response.equalsIgnoreCase("yes")) {
            purchaseTicket(conn, scanner); // Recursively allow them to pick another movie
        } else {
            return; // Exit if no valid selection
        }
    }

    // Allow user to select a showing time
    System.out.print("\nSelect a showing time ID: ");
    int selectedShowtimeId = scanner.nextInt();
    scanner.nextLine();  // Consume newline

    // Check if the showing time ID is valid and if enough tickets are available
    String selectedShowingTime = "";
    int availableTickets = 0;
    try (PreparedStatement stmt = conn.prepareStatement("SELECT showing_time, available_tickets FROM movie_showtimes WHERE id = ?")) {
        stmt.setInt(1, selectedShowtimeId);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                selectedShowingTime = rs.getString("showing_time");
                availableTickets = rs.getInt("available_tickets");
            } else {
                System.out.println("Invalid showing time selection.");
                return; // Exit if invalid showtime
            }
        }
    }

    // Ask for the number of tickets
    System.out.print("Enter the number of tickets: ");
    int ticketCount = scanner.nextInt();
    scanner.nextLine();  // Consume newline

    // Check if there are enough available tickets
    if (ticketCount > availableTickets) {
        System.out.println("Not enough tickets available. Only " + availableTickets + " tickets are left.");
        return;
    }

    double totalAmount = ticketCount * ticketPrice;

    // Set payment status to "Pending" by default
    String paymentStatus = "Pending";

    System.out.println("Payment status is currently: " + paymentStatus);
    System.out.println("To proceed, please confirm your payment.");

    // After confirming payment (simulate this with an input)
    System.out.print("Have you completed the payment? (yes/no): ");
    String paymentConfirmation = scanner.nextLine().toLowerCase();

    if (paymentConfirmation.equals("yes")) {
        paymentStatus = "Paid";  // Update the payment status to "Paid"
        System.out.println("Payment confirmed. Status updated to: " + paymentStatus);
    } else {
        System.out.println("Payment is still pending.");
    }

    // Default customer name if not needed
    String customerName = "Anonymous";

    // SQL query to insert purchase into the database with "Pending" or "Paid" status
    String insertPurchaseQuery = "INSERT INTO purchases (movie_id, customer_name, ticket_count, total_amount, "
            + "purchase_date, payment_status, theater_type, showing_time) "
            + "VALUES (?, ?, ?, ?, datetime('now'), ?, ?, ?)";

    try (PreparedStatement stmt = conn.prepareStatement(insertPurchaseQuery)) {
        stmt.setInt(1, movieId);
        stmt.setString(2, customerName);  // Insert default customer name
        stmt.setInt(3, ticketCount);
        stmt.setDouble(4, totalAmount);
        stmt.setString(5, paymentStatus);
        stmt.setString(6, theaterType);
        stmt.setString(7, selectedShowingTime);

        // Update available tickets after purchase
        String updateTicketsQuery = "UPDATE movie_showtimes SET available_tickets = available_tickets - ? WHERE id = ?";
        try (PreparedStatement updateStmt = conn.prepareStatement(updateTicketsQuery)) {
            updateStmt.setInt(1, ticketCount);
            updateStmt.setInt(2, selectedShowtimeId);
            updateStmt.executeUpdate();
        }

        int rowsInserted = stmt.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("Purchase successful!");
        } else {
            System.out.println("Failed to complete purchase.");
        }
    }
}
private static void insertAvailableTickets(Connection conn, Scanner scanner) throws SQLException {
    System.out.print("Enter Movie ID to add showtime: ");
    int movieId = scanner.nextInt();
    scanner.nextLine();  // Consume newline

    // Ask for the showing time and available tickets
    System.out.print("Enter Showing Time (e.g., '1:00 PM'): ");
    String showingTime = scanner.nextLine();

    System.out.print("Enter Available Tickets: ");
    int availableTickets = scanner.nextInt();
    scanner.nextLine();  // Consume newline

    // Insert the new showing time with available tickets
    String insertShowtimeQuery = "INSERT INTO movie_showtimes (movie_id, showing_time, available_tickets) "
            + "VALUES (?, ?, ?)";
    try (PreparedStatement stmt = conn.prepareStatement(insertShowtimeQuery)) {
        stmt.setInt(1, movieId);
        stmt.setString(2, showingTime);
        stmt.setInt(3, availableTickets);

        int rowsInserted = stmt.executeUpdate();
        if (rowsInserted > 0) {
            System.out.println("Showtime added successfully with " + availableTickets + " available tickets.");
        } else {
            System.out.println("Failed to add showtime.");
        }
    }
}

private static void viewPurchaseHistory(Connection conn) throws SQLException {
    String query = "SELECT p.purchase_id, m.title, p.customer_name, p.ticket_count, p.total_amount, "
                 + "p.purchase_date, p.payment_status, p.theater_type, ms.showing_time "
                 + "FROM purchases p "
                 + "JOIN movies m ON p.movie_id = m.id "
                 + "JOIN movie_showtimes ms ON p.showtime_id = ms.id";
    try (PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        System.out.println("\nPurchase History:");
        System.out.println("+-------------+------------------------+-------------------+-------------+-------------+-------------------+-------------------+-------------------+-------------------+");
        System.out.println("| Purchase ID | Movie Title            | Customer Name     | Tickets     | Total Amount| Purchase Date     | Payment Status    | Theater Type      | Showtime          |");
        System.out.println("+-------------+------------------------+-------------------+-------------+-------------+-------------------+-------------------+-------------------+-------------------+");

        while (rs.next()) {
            int purchaseId = rs.getInt("purchase_id");
            String title = rs.getString("title");
            String customerName = rs.getString("customer_name");
            int ticketCount = rs.getInt("ticket_count");
            double totalAmount = rs.getDouble("total_amount");
            String purchaseDate = rs.getString("purchase_date");
            String paymentStatus = rs.getString("payment_status");
            String theaterType = rs.getString("theater_type");
            String showtime = rs.getString("showing_time");

            System.out.printf("| %-11d | %-22s | %-17s | %-11d | %-11.2f | %-17s | %-17s | %-17s | %-17s |\n", 
                              purchaseId, title, customerName, ticketCount, totalAmount, purchaseDate, paymentStatus, theaterType, showtime);
        }
        System.out.println("+-------------+------------------------+-------------------+-------------+-------------+-------------------+-------------------+-------------------+-------------------+");
    } catch (SQLException e) {
        System.out.println("Error fetching purchase history: " + e.getMessage());
    }
}

    private static void updateMoviePrice(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter movie ID to update price:");
        int movieId = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.println("Enter new movie price:");
        double newPrice = scanner.nextDouble();
        scanner.nextLine();  // Consume newline

        String updateQuery = "UPDATE movies SET rating = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setDouble(1, newPrice);
            stmt.setInt(2, movieId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Movie price updated successfully!");
            } else {
                System.out.println("Movie not found with the specified ID.");
            }
        }
    }
}
