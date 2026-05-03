package stmarys.library.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import stmarys.library.DatabaseManager;
import stmarys.library.model.BorrowRecord;

public class BorrowRecordDAOImpl implements BorrowRecordDAO {
    private final DatabaseManager databaseManager;

    public BorrowRecordDAOImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void save(BorrowRecord record) throws DaoException {
        try (Connection connection = databaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT OR REPLACE INTO borrow_records (record_id, book_id, member_id, borrow_date, due_date, return_status) VALUES (?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, record.getRecordId());
            ps.setInt(2, record.getBookId());
            ps.setInt(3, record.getMemberId());
            ps.setDate(4, Date.valueOf(record.getBorrowDate()));
            ps.setDate(5, Date.valueOf(record.getDueDate()));
            ps.setString(6, record.getReturnStatus());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Error saving borrow record: " + ex.getMessage(), ex);
        }
    }

    @Override
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

    @Override
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
        return new BorrowRecord(rs.getInt("record_id"), rs.getInt("book_id"), rs.getInt("member_id"),
                rs.getDate("borrow_date").toLocalDate(), rs.getDate("due_date").toLocalDate(),
                rs.getString("return_status"));
    }
}
