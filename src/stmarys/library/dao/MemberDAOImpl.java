package stmarys.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import stmarys.library.DatabaseManager;
import stmarys.library.model.Member;

public class MemberDAOImpl {
    private final DatabaseManager databaseManager;

    public MemberDAOImpl(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void save(Member member) throws DaoException {
        try (Connection connection = databaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT OR REPLACE INTO members (member_id, member_name, email, membership_type) VALUES (?, ?, ?, ?)")) {
            ps.setInt(1, member.getMemberId());
            ps.setString(2, member.getMemberName());
            ps.setString(3, member.getEmail());
            ps.setString(4, member.getMembershipType());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Error saving member: " + ex.getMessage(), ex);
        }
    }

    public Member findById(int id) throws DaoException {
        try (Connection connection = databaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM members WHERE member_id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapMember(rs);
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error finding member: " + ex.getMessage(), ex);
        }
        return null;
    }

    public List<Member> findAll() throws DaoException {
        List<Member> members = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT * FROM members")) {
            while (rs.next()) {
                members.add(mapMember(rs));
            }
        } catch (SQLException ex) {
            throw new DaoException("Error fetching all members: " + ex.getMessage(), ex);
        }
        return members;
    }

    public void delete(int id) throws DaoException {
        try (Connection connection = databaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement("DELETE FROM members WHERE member_id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Error deleting member: " + ex.getMessage(), ex);
        }
    }

    public List<Member> searchByName(String name) throws DaoException {
        List<Member> members = new ArrayList<>();
        try (Connection connection = databaseManager.getConnection();
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM members WHERE member_name LIKE ?")) {
            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    members.add(mapMember(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error searching members: " + ex.getMessage(), ex);
        }
        return members;
    }

    private Member mapMember(ResultSet rs) throws SQLException {
        return new Member(rs.getInt("member_id"), rs.getString("member_name"), rs.getString("email"),
                rs.getString("membership_type"));
    }
}
