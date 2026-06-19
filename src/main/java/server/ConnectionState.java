package main.java.server;

import main.java.server.auth.UserSession;

import java.nio.ByteBuffer;

/**
 * Класс ConnectionState представляет собой изолированный узел, хранящий состояние
 * текущего сетевого соединения клиента с сервером.
 * 
 * Используется в главном цикле селектора (Selector) внутри ServerMain для:
 * - Управления буферами чтения (headerBuffer, dataBuffer), предотвращая 
 *   блокировку потока при частичном получении данных.
 * - Отслеживания состояния авторизации пользователя (UserSession), 
 *   позволяя связывать каждый SocketChannel с конкретным пользователем
 *   и управлять сессией в связке с ClientMain, ServerMain и ReconnectingEffectManager.
 */
public class ConnectionState {
    ByteBuffer headerBuffer = ByteBuffer.allocate(4);   // 4 byte for header
    ByteBuffer dataBuffer = null;                               // data
    boolean hasReadSize = false;

    private UserSession userSession;

    public UserSession getUserSession() { return userSession; }
    public void setUserSession(UserSession userSession) { this.userSession = userSession; }

    public boolean isLoggedIn() { return userSession != null; }
}
