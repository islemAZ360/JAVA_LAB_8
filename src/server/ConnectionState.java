package server;

import java.nio.ByteBuffer;

class ConnectionState {
    ByteBuffer headerBuffer = ByteBuffer.allocate(4);   // 4 byte for header
    ByteBuffer dataBuffer = null;                               // data
    boolean hasReadSize = false;
}

