
package net.iaround.connector;

public class ConnectionException extends Exception {

    private static final long serialVersionUID = -954955657232341345L;

    public ConnectionException(String message) {
        super(message);
    }

    public ConnectionException(int message) {
        super("" + message);
    }

    public ConnectionException(Exception e) {
        super(e);
    }

    public ConnectionException(String message, Exception e) {
        super(message, e);
    }

    public ConnectionException(int message, Throwable e) {
        super("" + message, e);
    }
}
