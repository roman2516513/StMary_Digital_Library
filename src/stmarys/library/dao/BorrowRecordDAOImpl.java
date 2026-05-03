package stmarys.library.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import stmarys.library.DatabaseManager;
import stmarys.library.model.BorrowRecord;

public class BorrowRecordDAOImpl {
    private final DatabaseManager databaseManager;

    public BorrowRecordDAOImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void save(BorrowRecord record) throws DaoException {
        try (Connection connection = databaseManager.getConnection()) {
            String sql;
            if (record.getRecordId() <= 0) {
                // New record - auto-increment
                sql = "INSERT INTO borrow_records (book_id, member_id, borrow_date, due_date, return_status) VALUES (?, ?, ?, ?, ?)";
            } else {
                // Update existing record
                sql = "INSERT OR REPLACE INTO borrow_records (record_id, book_id, member_id, borrow_date, due_date, return_status) VALUES (?, ?, ?, ?, ?, ?)";
            }
            
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                if (record.getRecordId() <= 0) {
                    ps.setInt(1, record.getBookId());
                    ps.setInt(2, record.getMemberId());
                    ps.setDate(3, Date.valueOf(record.getBorrowDate()));
                    ps.setDate(4, Date.valueOf(record.getDueDate()));
                    ps.setString(5, record.getReturnStatus());
                } else {
                    ps.setInt(1, record.getRecordId());
                    ps.setInt(2, record.getBookId());
                    ps.setInt(3, record.getMemberId());
                    ps.setDate(4, Date.valueOf(record.getBorrowDate()));
                    ps.setDate(5, Date.valueOf(record.getDueDate()));
                    ps.setString(6, record.getReturnStatus());
                }
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DaoException("Error saving borrow record: " + ex.getMessage(), ex);
        }
    }

    public BorrowRecord findById(int id) throws DaoException {
        try (Connection connection = databaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM borrow_records WHERE record_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBorrowRecord(rs);
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error finding borrow record: " + ex.getMessage(), ex);
        }
        return null;
    }

    public List<BorrowRecord> findAll() throws DaoException {
        List<BorrowRecord> records = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM borrow_records")) {
            while (rs.next()) {
                records.add(mapBorrowRecord(rs));
            }
        } catch (SQLException ex) {
            throw new DaoException("Error fetching all borrow records: " + ex.getMessage(), ex);
        }
        return records;
    }

    public void delete(int id) throws DaoException {
        try (Connection connection = databaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement("DELETE FROM borrow_records WHERE record_id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Error deleting borrow record: " + ex.getMessage(), ex);
        }
    }

    private BorrowRecord mapBorrowRecord(ResultSet rs) throws SQLException {
        int recordId = rs.getInt("record_id");
        int bookId = rs.getInt("book_id");
        int memberId = rs.getInt("member_id");
        String borrowDateRaw = rs.getString("borrow_date");
        String dueDateRaw = rs.getString("due_date");
        LocalDate borrowDate = parseDateLenient(borrowDateRaw);
        LocalDate dueDate = parseDateLenient(dueDateRaw);
        String status = rs.getString("return_status");
        return new BorrowRecord(recordId, bookId, memberId, borrowDate, dueDate, status);
    }

    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})");

    private LocalDate parseDateLenient(String raw) throws SQLException {
        if (raw == null) {
            throw new SQLException("Missing date value");
        }
        String trimmed = raw.trim();

        // Try to extract an ISO date substring (yyyy-mm-dd) from various timestamp formats
        Matcher m = DATE_PATTERN.matcher(trimmed);
        if (m.find()) {
            String datePart = m.group(1);
            try {
                return LocalDate.parse(datePart);
            } catch (DateTimeParseException ex) {
                throw new SQLException("Error parsing time stamp: " + raw, ex);
            }
        }

        // Fallback: try direct parse
        try {
            return LocalDate.parse(trimmed);
        } catch (DateTimeParseException ex) {
            throw new SQLException("Error parsing time stamp: " + raw, ex);
        }
    }
}
