package org.smartplatforms.client;

public class SmartClientException extends Exception {
    public SmartClientException() { super(); }
    public SmartClientException(String message) { super(message); }
    public SmartClientException(String message, Throwable cause) { super(message, cause); }
    public SmartClientException(Throwable cause)  { super(cause); }
}
