import java.time.LocalDateTime;

public class User {
    public enum Role {
        ADMIN,
        MANAGER,
        EMPLOYEE,
        VISITOR
    }

    private int userId;
    private String userFirstName;
    private String userLastName;
    private Role permissionLevel;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private String passwordHash;

    public User(int userId, String userFirstName, String userLastName,
                Role permissionLevel, LocalDateTime createdAt,
                LocalDateTime lastLogin, String passwordHash) {
        this.userId = userId;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.permissionLevel = permissionLevel;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
        this.passwordHash = passwordHash;
    }

    // Getter & Setter

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public Role getRole() {
        return permissionLevel;
    }

    public void setRole(Role permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // Alles als String anzeigen
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userFirstName='" + userFirstName + '\'' +
                ", userLastName='" + userLastName + '\'' +
                ", role=" + permissionLevel +
                ", createdAt=" + createdAt +
                ", lastLogin=" + lastLogin +
                '}';
    }
}
