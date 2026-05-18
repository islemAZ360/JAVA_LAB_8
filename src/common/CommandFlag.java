package common;

public enum CommandFlag {
    FORCE("-f", "--force"),
    YES("-y", "--yes");

    private final String shortFlag;
    private final String longFlag;

    CommandFlag(String shortFlag, String longFlag) {
        this.shortFlag = shortFlag;
        this.longFlag = longFlag;
    }

    public String getShortFlag() { return shortFlag; }
    public String getLongFlag() { return longFlag; }

    public boolean matches(String input) {
        return input.equals(shortFlag) || input.equals(longFlag);
    }
}
