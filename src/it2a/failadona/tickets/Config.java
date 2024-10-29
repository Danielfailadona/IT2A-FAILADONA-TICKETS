/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it2a.failadona.tickets;

import java.sql.*;

public class Config {

    // Method to establish a connection to the SQLite database
    public static Connection connectDB() {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC"); // Load the SQLite JDBC driver
            con = DriverManager.getConnection("jdbc:sqlite:Tickets.db"); // Establish connection
            System.out.println("Connection Successful");
        } catch (Exception e) {
            System.out.println("Connection Failed: " + e);
        }
        return con;
    }

    // Method to display records from the database
    public void viewRecords(String query, String[] columnHeaders, String[] columnFields) {
        try (Connection conn = this.connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println(String.join(" | ", columnHeaders));
            while (rs.next()) {
                for (String field : columnFields) {
                    System.out.print(rs.getString(field) + " | ");
                }
                System.out.println();
            }

        } catch (SQLException e) {
            System.out.println("Error viewing records: " + e.getMessage());
        }
    }

    // Method to add a record to the database
    public void addRecord(String sql, Object... values) {
        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }
            pstmt.executeUpdate();
            System.out.println("Record added successfully.");

        } catch (SQLException e) {
            System.out.println("Error adding record: " + e.getMessage());
        }
    }

    // Method to update a record in the database
    public void updateRecord(String sql, Object... values) {
        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < values.length; i++) {
                pstmt.setObject(i + 1, values[i]);
            }
            pstmt.executeUpdate();
            System.out.println("Record updated successfully.");

        } catch (SQLException e) {
            System.out.println("Error updating record: " + e.getMessage());
        }
    }

    // Method to delete a record from the database
    public void deleteRecord(String sql, int id) {
        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Record deleted successfully.");

        } catch (SQLException e) {
            System.out.println("Error deleting record: " + e.getMessage());
        }
    }

    // Check seat availability for a specific movie
    public boolean checkSeatsAvailability(int movieID) {
        String sql = "SELECT max_seats FROM tickets WHERE id = ?";
        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int availableSeats = rs.getInt("max_seats");
                return availableSeats > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking seat availability: " + e.getMessage());
        }
        return false;
    }

    // Calculate total due for a ticket purchase with discounts
    public double calculateTotalDue(int movieID, int ticketsBought, char timeSlot) {
        double total = 0;
        String sql = "SELECT price, morning_discount, afternoon_discount, evening_discount FROM tickets WHERE id = ?";

        try (Connection conn = this.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, movieID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double price = rs.getDouble("price");
                double discount = 0;

                switch (timeSlot) {
                    case 'M':
                        discount = rs.getDouble("morning_discount");
                        break;
                    case 'A':
                        discount = rs.getDouble("afternoon_discount");
                        break;
                    case 'E':
                        discount = rs.getDouble("evening_discount");
                        break;
                }

                total = (price * ticketsBought) * (1 - discount / 100);
            }

        } catch (SQLException e) {
            System.out.println("Error calculating total due: " + e.getMessage());
        }

        return total;
    }

    // Additional helper methods could be added here based on future requirements
}

    