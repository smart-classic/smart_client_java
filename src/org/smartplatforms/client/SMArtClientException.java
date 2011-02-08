package org.smartplatforms.client;

public class SMArtClientException extends Exception {
    public SMArtClientException() { super(); }
    public SMArtClientException(String message) { super(message); }
    public SMArtClientException(String message, Throwable cause) { super(message, cause); }
    public SMArtClientException(Throwable cause)  { super(cause); }
}
