package server.auth;

public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository, SessionManager sessionManager) {
        this.accountRepository = accountRepository;
    }

    public boolean registerNewAccount(String username, String rawPassword, String email) throws Exception {
        if (accountRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        String hashedPassword = PasswordHasher.hashSha384(rawPassword);
        Account newAccount = new Account(username, hashedPassword, email);
        accountRepository.save(newAccount);
        return true;
    }

    public boolean login(String username, String rawPassword) throws Exception {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            return false;
        }
        String hashedInputPassword = PasswordHasher.hashSha384(rawPassword);
        return account.getHashedPassword().equals(hashedInputPassword);
    }

    public boolean logout(String username) throws Exception {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            return false;
        }

        return account.getStatus() == UserStatus.OFFLINE;
    }

}