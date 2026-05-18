package client;

import common.Request;
import common.Response;
import common.Serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestSender implements AutoCloseable {
    private final DataOutputStream dos;
    private final DataInputStream dis;

    RequestSender(DataOutputStream dos, DataInputStream dis) {
        this.dos = dos;
        this.dis = dis;
    }

    public Response sendRequest(Request req) throws IOException {
        // 2. Serialize Object to byte array to count size
        byte[] dataBytes = Serializer.serialize(req);

        // 3. Send: 4 byte Header + data
        this.dos.writeInt(dataBytes.length); // Write 4 byte int
        this.dos.write(dataBytes);           // Write byte array
        this.dos.flush();
        System.out.println("🚀 Request was sent (" + dataBytes.length + " bytes)");

        // 4. Receipt: Waiting response from Server (Blocking)
        // NOTE: Server have to send by one principle
        // Read Object if Server use ObjectOutputStream
        try {
            // Đọc nhãn (4 byte)
            int size = this.dis.readInt();
            System.out.printf("First 4 received bytes in (Hex): %08X\n", size);
            // Đọc gói hàng
            byte[] data = new byte[size];
            this.dis.readFully(data);

            // Deserialize (Trong này vẫn dùng ObjectInputStream nhưng là dùng "nội bộ" with byte arr)
            Response resp = (Response) Serializer.deserialize(data);

            return resp;
        } catch (ClassNotFoundException e) {
//            System.err.println("❌ Can not understand data type from Server.");
            throw new RuntimeException("❌ Can not understand data type from Server.");
        }
    }

    @Override
    public void close() throws Exception {
        Exception exception = null;

        // Close DataOutputStream
        try {
            if (dos != null) {
                dos.close();
            }
        } catch (Exception e) {
            exception = e;
        }

        // Close DataInputStream
        try {
            if (dis != null) {
                dis.close();
            }
        } catch (Exception e) {
            if (exception != null) {
                exception.addSuppressed(e);
            } else {
                exception = e;
            }
        }

        // exception if bug
        if (exception != null) {
            throw exception;
        }
    }
}
