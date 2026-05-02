package stmarys.library;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DATABASE_PATH = "data/library.db";
    private static final String URL = "jdbc:sqlite:" + DATABASE_PATH;

    public DatabaseManager() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            Files.createDirectories(Paths.get("data"));
            initializeDatabase();
        } catch (ClassNotFoundException ex) {
            throw new SQLException("SQLite JDBC driver was not found in lib/sqlite-jdbc.jar.", ex);
        } catch (IOException ex) {
            throw new SQLException("Could not create the data folder.", ex);
        }
    }

    public synchronized Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(URL);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    private void initializeDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL);
                Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS books (book_id INTEGER PRIMARY KEY, title TEXT NOT NULL, author TEXT NOT NULL, category TEXT NOT NULL, availability_status TEXT NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS members (member_id INTEGER PRIMARY KEY, member_name TEXT NOT NULL, email TEXT NOT NULL, membership_type TEXT NOT NULL)");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS borrow_records (record_id INTEGER PRIMARY KEY, book_id INTEGER NOT NULL, member_id INTEGER NOT NULL, borrow_date DATE NOT NULL, due_date DATE NOT NULL, return_status TEXT NOT NULL)");
            if (isTableEmpty(statement, "books")) {
                insertSampleData(statement);
            }
        }
    }

    private boolean isTableEmpty(Statement statement, String tableName) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            return resultSet.next() && resultSet.getInt(1) == 0;
        }
    }

    private void insertSampleData(Statement statement) throws SQLException {
        statement.executeUpdate("INSERT INTO books (book_id, title, author, category, availability_status) VALUES (1, 'Introduction to Java', 'John Smith', 'Programming', 'Available')");
        statement.executeUpdate("INSERT INTO books (book_id, title, author, category, availability_status) VALUES (2, 'Database Systems', 'Maria Garcia', 'Computer Science', 'Borrowed')");
        statement.executeUpdate("INSERT INTO books (book_id, title, author, category, availability_status) VALUES (3, 'Software Engineering Principles', 'Alan Brown', 'Engineering', 'Available')");
        statement.executeUpdate("INSERT INTO members (member_id, member_name, email, membership_type) VALUES (1, 'Alice Johnson', 'alice.johnson@stmarys.ac.uk', 'Student')");
        statement.executeUpdate("INSERT INTO members (member_id, member_name, email, membership_type) VALUES (2, 'Michael Lee', 'michael.lee@stmarys.ac.uk', 'Staff')");
        statement.executeUpdate("INSERT INTO members (member_id, member_name, email, membership_type) VALUES (3, 'Sara Ahmed', 'sara.ahmed@stmarys.ac.uk', 'Student')");
        statement.executeUpdate("INSERT INTO borrow_records (record_id, book_id, member_id, borrow_date, due_date, return_status) VALUES (1, 2, 1, '2025-03-01', '2025-03-15', 'Borrowed')");
        statement.executeUpdate("INSERT INTO borrow_records (record_id, book_id, member_id, borrow_date, due_date, return_status) VALUES (2, 1, 2, '2025-03-02', '2025-03-16', 'Returned')");
        statement.executeUpdate("INSERT INTO borrow_records (record_id, book_id, member_id, borrow_date, due_date, return_status) VALUES (3, 3, 3, '2025-03-05', '2025-03-19', 'Borrowed')");
    }

    public Path getDatabasePath() {
        return Paths.get(DATABASE_PATH);
    }
}
