package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.*;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) {
        //System.out.println("yes it works");

        class H2DatabaseExample {
            public static void DatabaseImplementation(String[] args) {
                Connection connection = null;
                try {
                    connection = H2DatabaseConnection.connect();

                    // Example: Select all rows from a table
                    String query = "SELECT * FROM TEST";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                         ResultSet resultSet = preparedStatement.executeQuery()) {

                        while (resultSet.next()) {
                            // Process each row
                            int id = resultSet.getInt("id");
                            String name = resultSet.getString("name");

                            // Perform operations with retrieved data
                            System.out.println("ID: " + id + ", NAME: " + name);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    H2DatabaseConnection.close(connection);
                }
            }

            public static class H2DatabaseConnection {
                public static Connection connect() {
                    try {
                        Class.forName("org.h2.Driver");
                        return DriverManager.getConnection("jdbc:h2:~/test", "anastasijaizv", "izvrtu");
                    } catch (ClassNotFoundException | SQLException e) {
                        throw new RuntimeException("Error connecting to the database", e);
                    }
                }

                public static void close(Connection connection) {
                    try {
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
    }
    }
    }
}
