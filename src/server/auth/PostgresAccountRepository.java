package server.auth;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.*;

public class PostgresAccountRepository implements AccountRepository {
    private final Connection connection;

    public PostgresAccountRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Account account) {
        String sql = "INSERT INTO users (user_name, hashed_password, email) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, account.getUsername());
            statement.setString(2, account.getHashedPassword());
            statement.setString(3, account.getEmail());
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e);
            throw new RuntimeException("Error saving account to PostgreSQL", e);
        }
    }

    @Override
    public Account findByUsername(String username) {
        String sql = "SELECT user_name, hashed_password, email FROM users WHERE user_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Account account = new Account(
                            resultSet.getString("user_name"),
                            resultSet.getString("hashed_password"),
                            resultSet.getString("email")
                    );
                    return account;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding account from PostgreSQL", e);
        }
        return null;
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM users WHERE user_name = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking account existence in PostgreSQL", e);
        }
    }

    @Override
    public boolean updateStatus(Long userId, UserStatus status) {
        String sql = "UPDATE users SET status = ?::user_status WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, status.name());
            statement.setLong(2, userId);
            int resultUpdate = statement.executeUpdate();
            return resultUpdate > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error update user status in PostgreSQL", e);
        }
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        String sql = "UPDATE users SET hashed_password = ? WHERE user_id = ? AND hashed_password = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, PasswordHasher.hashSha384(newPassword));
            statement.setLong(2, userId);
            statement.setString(3, PasswordHasher.hashSha384(oldPassword));
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error occurred when changing password in PostgreSQL", e);
        }
    }

}
