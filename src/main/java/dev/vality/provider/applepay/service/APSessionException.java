package dev.vality.provider.applepay.service;

public class APSessionException extends Exception {
    private String payload;

    public APSessionException() {
    }

    public APSessionException(String message, String payload) {
        super(message);
        this.payload = payload;
    }

    public APSessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public APSessionException(Throwable cause) {
        super(cause);
    }

    public APSessionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getPayload() {
        return payload;
    }
}
