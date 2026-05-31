package common;

import java.nio.channels.SelectionKey;

public interface Command {
    String getName();

    String getDescription();

//    Object execute(String[] arg);

    Response execute(Request request);

    default Response execute(Request request, SelectionKey key) {
        return execute(request);
    };
}
