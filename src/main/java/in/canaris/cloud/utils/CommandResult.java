package in.canaris.cloud.utils;

public class CommandResult {
    private boolean status;
    private StringBuilder message;

    public CommandResult(boolean status, StringBuilder message) {
        this.status = status;
        this.message = message;
    }

    public boolean isStatus() {
        return status;
    }

    public StringBuilder getMessage() {
        return message;
    }
}
