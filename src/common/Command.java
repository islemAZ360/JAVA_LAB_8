package common;

public interface Command {
    String getName();

    String getDescription();

//    Object execute(String[] arg);

    Response execute(Request request);
}
