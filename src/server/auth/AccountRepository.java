package server.auth;

//import java.util.Optional;

public interface AccountRepository {

    void save(Account account);

//    Optional<Account> findByUsername(String username);
    Account findByUsername(String username); // Return null if account not found

    boolean existsByUsername(String username);

    boolean updateStatus(Long userId, UserStatus newStatus);

    boolean changePassword(Long userId, String oldPassword, String newPassword);
}
