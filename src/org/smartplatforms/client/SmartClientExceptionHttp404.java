package org.smartplatforms.client;


/**
 *
 * distinct exception class because 404 is sometimes not an error, but the
 * equivalent to an empty list.
 *
 * @author nate
 */
public class SmartClientExceptionHttp404 extends SmartClientException {
    public SmartClientExceptionHttp404() { super(); }
    public SmartClientExceptionHttp404(String message) { super(message); }
    public SmartClientExceptionHttp404(String message, Throwable cause) { super(message, cause); }
    public SmartClientExceptionHttp404(Throwable cause)  { super(cause); }
}
