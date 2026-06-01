package server;

import server.auth.UserSession;

import java.nio.ByteBuffer;

public class ConnectionState {
    ByteBuffer headerBuffer = ByteBuffer.allocate(4);   // 4 byte for header
    ByteBuffer dataBuffer = null;                               // data
    boolean hasReadSize = false;

    private UserSession userSession;

    public UserSession getUserSession() { return userSession; }
    public void setUserSession(UserSession userSession) { this.userSession = userSession; }

    public boolean isLoggedIn() { return userSession != null; }
}
