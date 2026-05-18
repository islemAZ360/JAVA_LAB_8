package common;

import java.io.Serializable;

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private String message;
    private StatusCode status;
    private Object data;

    public Response(String message, StatusCode status, Object data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public Boolean isSuccess() {
        return this.status.isSuccess();
    }

    public StatusCode getStatusCode() {
        return this.status;
    }

    public Object getData() {
        return data;
    }
}
