package com.bank.demo.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.sql.*;

@Service
public class JDBCUtil implements CommandLineRunner {

    /**
     * ObjectMapper instance. Used for JsonNode mapping
     */
    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * JDBC driver name and database URL
     */
    static final String JDBC_DRIVER = "org.h2.Driver";

    /**
     * JDBC database URL
     */
    static final String DB_URL = "jdbc:h2:~/test";

    /**
     * Database username
     */
    static final String USER = "sa";
    /**
     * Database password
     */
    static final String PASS = "";

    /**
     * autocreat table and init values
     *
     * @param args
     */
    @Override
    public void run(String[] args) {
        try (Statement stmt = DriverManager.getConnection(DB_URL, USER, PASS).createStatement()) {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Create new table
            String sql = "CREATE TABLE   ACCOUNTT " +
                    "(number VARCHAR(255) not NULL, " +
                    " pin VARCHAR(255), " +
                    " balance VARCHAR(255))";
            stmt.executeUpdate(sql);

            System.out.println("Created table in given database...");

            // Insert data to DB
            System.out.println("Inserting records into the table...");

            sql = "INSERT INTO ACCOUNTT VALUES ('000000000000', '0000', 2000)";
            stmt.executeUpdate(sql);

            sql = "INSERT INTO ACCOUNTT VALUES ('111111111111', '1111', 500)";
            stmt.executeUpdate(sql);

            sql = "INSERT INTO ACCOUNTT VALUES ('222222222222', '2222', 6000)";
            stmt.executeUpdate(sql);

            sql = "INSERT INTO ACCOUNTT VALUES ('333333333333', '3333', 100)";
            stmt.executeUpdate(sql);

            sql = "INSERT INTO ACCOUNTT VALUES ('444444444444', '4444', 50)";
            stmt.executeUpdate(sql);

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Goodbye!");
    }

    /**
     * Drop previous table
     *
     * @return true if it successful
     */
    public boolean dropTable() {
        try (Statement stmt = DriverManager.getConnection(DB_URL, USER, PASS).createStatement()) {
            // Delete previous table
            String sql = "DROP TABLE ACCOUNTT";
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }

    /**
     * Find account in DB
     *
     * @param number of card
     * @param pin    of card
     * @return JsonNode from data
     */
    public JsonNode getAccount(String number, String pin) {
        try (Statement stmt = DriverManager.getConnection(DB_URL, USER, PASS).createStatement()) {
            String sql = "SELECT * FROM ACCOUNTT" +
                    " WHERE NUMBER = '" + number + "'" +
                    " AND PIN = '" + pin + "'";


            ResultSet rs = stmt.executeQuery(sql);

            System.out.println(rs.toString());

            // Retrieve by column name
            rs.first();
            if (rs.getRow() != 0) {
                String currNumber = rs.getString("number");
                String currPin = rs.getString("pin");
                int currBalance = rs.getInt("balance");

                // Display values
                System.out.print("NUMBER: " + currNumber);
                System.out.print(", PIN: " + currPin);
                System.out.print(", BALANCE: " + currBalance);

                rs.close();
                return objectMapper.valueToTree(new Account(currNumber, currPin, currBalance));
            } else throw new SQLException("Dont find any data");
        } catch (SQLException se) {
            se.printStackTrace();
            return objectMapper.valueToTree(new Account("Error", "Error", 0));
        }
    }

    public boolean giveMoney(String number, String pin, int money) {
        try (Statement stmt = DriverManager.getConnection(DB_URL, USER, PASS).createStatement()) {
            JsonNode account = getAccount(number, pin);
            int balance = account.path("balance").asInt();
            if (balance < money) {
                return false;
            } else {
                String sql = "UPDATE ACCOUNTT SET balance = " + (balance - money) +
                        " WHERE NUMBER = '" + number + "'";
                stmt.executeUpdate(sql);
                return true;
            }
        } catch (SQLException se) {
            se.printStackTrace();
            return false;
        }
    }
}
