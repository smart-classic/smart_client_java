package org.smartplatforms.client;


/**
 *
 * distinct exception class because 404 is sometimes not an error, but the
 * equivalent to an empty list.
 *
 * @author nate
 */
public class SMArtClientExceptionHttp404 extends SMArtClientException {
    public SMArtClientExceptionHttp404() { super(); }
    public SMArtClientExceptionHttp404(String message) { super(message); }
    public SMArtClientExceptionHttp404(String message, Throwable cause) { super(message, cause); }
    public SMArtClientExceptionHttp404(Throwable cause)  { super(cause); }
}
