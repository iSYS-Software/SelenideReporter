package de.isys.selrep;

/**
 * Marker-Exception used for structuring the code better.
 */
public class UITestException extends RuntimeException {

    public UITestException() {
    }

    public UITestException(String message) {
        super(message);
    }

    public UITestException(String message, Throwable cause) {
        super(message, cause);
    }

    public UITestException(Throwable cause) {
        super(cause);
    }

    public UITestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
