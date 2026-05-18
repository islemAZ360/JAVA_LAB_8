package common;

import java.io.Serializable;

public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    private String commandName;
    private String stringArgument;
    private Object objectArgument;

    public Request(String commandName, String stringArgument, Object objectArgument) {
        this.commandName = commandName;
        this.stringArgument = stringArgument;
        this.objectArgument = objectArgument;
    }

    public String getCommandName() {
        return commandName;
    }

    public String getStringArgument() {
        return stringArgument;
    }

    public Object getObjectArgument() {
        return objectArgument;
    }

    @Override
    public String toString() {
        return "Request{cmd='" + this.commandName + "', stringArg='" + this.stringArgument + "'" + ", objectArg='" + this.objectArgument +"'}";
    }
}
