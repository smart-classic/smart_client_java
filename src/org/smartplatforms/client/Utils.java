package org.smartplatforms.client;

// testing git origin
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import java.net.URLEncoder;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpResponse;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.params.HttpParams;
import org.apache.http.params.CoreConnectionPNames;
//import org.apache.http.entity.StringEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.StatusLine;
import org.apache.http.HttpEntity;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.commonshttp.HttpRequestAdapter;

import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.http.HttpParameters;

public class Utils {

    private Log logger = null;
    private String consumerKey = null;
    private String consumerSecret = null;
    private String baseURL = null;

    private int defaultHttpTimeout = 7000;
    private ResponseTypeConversion responseTypeConversion = null;

    public Utils(String consumerKey, String consumerSecret, String baseURL,
            ResponseTypeConversion responseTypeConversion, Integer httpTimeout)
            throws SmartClientException {
        logger = LogFactory.getLog(this.getClass());
        
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;

        logger.info(" -- init with ckey: " + consumerKey);
        logger.info(" -- init with csecret: " + consumerSecret);

        this.baseURL = baseURL;
        if (this.baseURL.charAt(this.baseURL.length() -1) == '/') {
            this.baseURL = this.baseURL.substring(0, this.baseURL.length() -1);
        }
        this.responseTypeConversion = responseTypeConversion;
        if (httpTimeout != null) {
            defaultHttpTimeout = httpTimeout;
        }
    }

    String dataFromStream(InputStream inputStrm) throws SmartClientException {
        String xstr = null;
        try {
            int xcc = inputStrm.read();
            StringBuffer xstrb = new StringBuffer();
            while (xcc != -1) {
                xstrb.append((char) xcc);
                xcc = inputStrm.read();
            }
            xstr = xstrb.toString();

        } catch (java.io.IOException ioe) {
            throw new SmartClientException(ioe);
        }
        return xstr;
    }

    /**
     *
     * @param hcRequest the request being signed
     * @param consumerKey0 null if using the consumerKey this instance was built with
     * @param consumerSecret0 null if using the consumerKey this instance was built with
     * @param token    null in case of signing a 2-legged call
     * @param tokenSecret   null in case of signing a 2-legged call
     * @throws SmartClientException
     */
    void signWithSignpost(
            HttpUriRequest hcRequest,
            String consumerKey0,
            String consumerSecret0,
            String token,
            String tokenSecret) throws SmartClientException {

         String consumerKeyLocal = null;
         String consumerSecretLocal = null;
         if (consumerKey0 == null) {
             consumerKeyLocal = consumerKey;
             consumerSecretLocal = consumerSecret;
         } else {
             consumerKeyLocal = consumerKey0;
             consumerSecretLocal = consumerSecret0;
         }

         OAuthConsumer oauthConsumer = new DefaultOAuthConsumer(consumerKeyLocal, consumerSecretLocal);

         oauth.signpost.http.HttpRequest spRequest = new HttpRequestAdapter(hcRequest);
         if (token == null) {
             // just how OauthConsumer needs to see it in case of no authorized token/secret
             oauthConsumer.setTokenWithSecret(null, "");
         } else {
             oauthConsumer.setTokenWithSecret(token, tokenSecret);
         }
         try {
             String whatDebugWas = null;
///* un-comment to get SBS    */    whatDebugWas = System.setProperty("debug", "true");
             oauthConsumer.sign(spRequest);
///* un-comment this also when un-commenting the above    */ System.setProperty("debug", whatDebugWas);
         } catch (OAuthMessageSignerException omse) {
             throw new SmartClientException(omse);
         } catch (OAuthExpectationFailedException oefe) {
             throw new SmartClientException(oefe);
         } catch (oauth.signpost.exception.OAuthCommunicationException oce) {
             throw new SmartClientException(oce);
         }
    }


    /**
    * General wrapper for all Indivo PHA REST calls, where
    * URL params are to be added.  Most applications will not
    * use this, the more specific methods are recommended.
    *
    * This more general method might be useful, for example,
    * where requests of various types are generated dynamically.
    * Therfore it is public.
    *
    * @param reqMeth POST, GET, or .....
    * @param reletivePath as documented at <code>http://wiki.chip.org/indivo</code>
    * @param queryString part of URL following '?', but not including '?'
    *     See <b>queryString param</b> note in the class description, above.
    * @param accessTokenAndSecret authorized request token and secret
    * @param requestBody value to PUT or POST, not necessarily a Document
    * @param options possible options include: sockentTimeout; connectionTimeout
    */
    public SmartResponse smartRequest(
            String reqMeth,
            String reletivePath,
            Object queryString,
            TokenSecret accessTokenAndSecret,
            Object requestBody,   // String or byte[]
            Map<String,Object> options) throws SmartClientException {
        
        if (options == null) { options = new HashMap<String,Object>(); }

        String aToken = null;
        String aSecret = null;
        if (accessTokenAndSecret != null) {
            aToken = accessTokenAndSecret.getToken();
            aSecret = accessTokenAndSecret.getTokenSecret();
        }
        HttpResponse response = phaRequestPart1(
            reqMeth, reletivePath, queryString, aToken, aSecret, requestBody, options);

        return smartRequestResponse(response, reqMeth + " " + reletivePath, options);
    }

    public SmartResponse smartRequestResponse(HttpResponse response, String requestdisplay, Map<String,Object> options)
            throws SmartClientException {

        StatusLine statusLine = response.getStatusLine();
        HttpEntity httpEntity = response.getEntity();

        int statusCode = statusLine.getStatusCode();
        if (statusCode == 404) {
            String errText = dataFromStream(getContent(httpEntity));
            throw new SmartClientExceptionHttp404("response code from indivo 404: " + errText);
        } else if (statusCode != 200) {
            String errText = dataFromStream(getContent(httpEntity));
            throw new SmartClientException(
                    "response code from indivo not 200, was: " + statusCode + "\n" + errText);
        }

//        String contentTypeReceived = null;

        Header[] ctHeaders = response.getHeaders("Content-Type");
        if (ctHeaders.length == 0) {
            logger.warn("no Content-Type header found");
        } else {
            if (ctHeaders.length > 1) {
                String multHeaders = "";
                for (int ii = 0; ii < ctHeaders.length; ii++) {
                    multHeaders += "\n" + ctHeaders[ii].getValue();
                }
                logger.warn("more than one Content-Type header in response to "
                    + requestdisplay + ".  Using first of:" + multHeaders );
            }

//            contentTypeReceived = ctHeaders[0].getValue();
        }

        HttpEntity entityToConvert = response.getEntity();

//        ResponseTypeConversion responseTypeConversion0 = (ResponseTypeConversion) options.get("responseTypeConversion");
//        if (responseTypeConversion0 == null) { responseTypeConversion0 = defaultResponseTypeConversion; }
        return responseTypeConversion.responseToObject(/*response.getEntity()*/ entityToConvert);
    }


    private InputStream getContent(HttpEntity httpEntity) throws SmartClientException {
        InputStream istrm = null;
        try {
            istrm = httpEntity.getContent();
        } catch (IOException ioe) {
            throw new SmartClientException(ioe);
        }
        return  istrm;
    }

/**
 * 
 * @param reqMeth  "GET", "POST" or etc.
 * @param reletivePath  starting with '/'
 * @param queryString   for example: "a=b&c=d"
 * @param phaToken
 * @param phaTokenSecret
 * @param requestBody    null in case of "GET"
 * @param options only current options are connectionTimeout and socketTimeout
 * @return
 * @throws SmartClientException
 */
    private HttpResponse phaRequestPart1(
            String reqMeth,
            String reletivePath,
            Object queryString,
            String phaToken,
            String phaTokenSecret,
            Object requestBody,   // String or byte[]
            Map<String,Object> options) throws SmartClientException {

        String displayQS = "null";
        if (queryString != null) {
            displayQS = queryString.getClass().getName() + " " + queryString;
        };

        logger.info("reletivePath, queryString, requestXmlOrParams: "
                + reletivePath + ",  " + displayQS + '\n' + requestBody + "\n\n");
        String queryString0;
        if (queryString == null
                || ((queryString instanceof String) && ((String) queryString).length() == 0)
           ) {
            queryString0 = "";
        } else if (queryString instanceof String) {
            String qsString = (String) queryString;
            if (qsString.indexOf('=') < 1) {
                throw new SmartClientException(
        	        "unexpected queryString, did not have any key/value delimiter of '=': " + queryString);
            }
        	queryString0 = qsString;
        } else if (queryString instanceof Map) {
        	StringBuffer qsBuff = new StringBuffer();
            Map qsMap = (Map) queryString;
        	Iterator iter = qsMap.keySet().iterator();
        	while (iter.hasNext()) {
                if (qsBuff.length() > 0) { qsBuff.append('&'); }

                Object keyObj = iter.next();
                if (! (keyObj instanceof String)) {
                	throw new SmartClientException("queryString map key of unexpected type: "
                		+ keyObj.getClass().getName() + " -- " + keyObj);
                }
                String key = (String) keyObj;

                Object valueObj = qsMap.get(key);
                try {
                    if (valueObj instanceof String) {
                        qsBuff.append(URLEncoder.encode(key,"UTF-8") + '=' + URLEncoder.encode((String) valueObj,"UTF-8"));
                    } else if (valueObj instanceof String[]) {
                    	String[] valueArr = (String[]) valueObj;
                    	for (int ii = 0; ii < valueArr.length; ii++) {
                           qsBuff.append(URLEncoder.encode(key,"UTF-8") + '=' + URLEncoder.encode(valueArr[ii],"UTF-8"));
                        }
                    } else {
                   	    throw new SmartClientException("queryString map value of unexpected type: "
                   		    + valueObj.getClass().getName() + " -- " + valueObj);
                    }
                } catch (java.io.UnsupportedEncodingException uee) {
                    throw new SmartClientException(uee);
                }
            }
            queryString0 = qsBuff.toString();
        } else {
            throw new SmartClientException(
                "queryString not String or Map, type is: " + queryString.getClass().getName());
        }

        String phaURLString = baseURL + reletivePath;
        if (queryString0.length() > 0) { phaURLString += "?" + queryString0; }

///* FIXME temp for test*/ System.out.println(phaURLString); if (requestBody != null) { System.out.println(requestBody); }


        if (requestBody != null
             && (! (requestBody instanceof String || requestBody instanceof Byte[])) ) {
            throw new SmartClientException("requestBody must be either String or byte[], was: " +
                    requestBody.getClass().getName());
        }

        HttpUriRequest hcRequest = null;

        if (requestBody == null) { requestBody = ""; }

        logger.info("reqMeth: " + reqMeth);
        try {
            if (reqMeth.equals("PUT") || reqMeth.equals("POST")) {
                if (reqMeth.equals("PUT")) {
                    hcRequest = new HttpPut(phaURLString);
                } else {
                    hcRequest = new HttpPost(phaURLString);
                }

                byte[] requestBodyB = null;
                if (requestBody instanceof String) {
                    String requestBodyStr = (String) requestBody;
                    requestBodyB = requestBodyStr.getBytes("UTF-8");
                } else {
                    requestBodyB = (byte[]) requestBody;
                }
                ByteArrayEntity bae = new ByteArrayEntity(requestBodyB);
//                bae.setContentType(contentType);
                ((HttpEntityEnclosingRequestBase)hcRequest).setEntity(bae);
//                hcRequest.addHeader("Content-Type",contentType);
            } else if (reqMeth.equals("GET")) {
                hcRequest = new HttpGet(phaURLString);
            } else if (reqMeth.equals("DELETE")) {
                hcRequest = new HttpDelete(phaURLString);
            }
        } catch (java.io.UnsupportedEncodingException uee) {
            throw new SmartClientException(uee);
        }

        logger.info("about to sign  -- consumerKey, consumerSecret, phaToken, phaTokenSecret: " +
                consumerKey + ", " + consumerSecret + ", " + phaToken + ", " + phaTokenSecret);
        // in case of form-url-encoded, will signpost know to look at Content-Type header and entity??
        signWithSignpost(hcRequest, consumerKey, consumerSecret, phaToken, phaTokenSecret);

        // FIXME, is this good?/**/hcRequest.addHeader("Accept", "text/plain,application/xml");      // don't be mistaken for a browser
        HttpResponse httpResponse = smartExecute(hcRequest, options);
        return httpResponse;
    }

    public HttpResponse smartExecute(HttpUriRequest hcRequest, Map<String,Object> options) throws SmartClientException {

        AbstractHttpClient httpClient = new DefaultHttpClient();
        HttpParams httpParams0 = httpClient.getParams();

        Object connectionTimeout = options.get("connectionTimeout");
        Object socketTimeout = options.get("socketTimeout");
        if (connectionTimeout == null) {
            connectionTimeout = defaultHttpTimeout;
        }
        if (socketTimeout == null) {
            socketTimeout = defaultHttpTimeout;
        }

        if (! ((socketTimeout instanceof Integer) && (connectionTimeout instanceof Integer)) ) {
            throw new SmartClientException("socketTimeout and connectionTimeout options must be ingeters. "
                    + "sockenTimeout was " + socketTimeout.getClass().getName()
                    + ", and connectionTimeout was " + connectionTimeout.getClass().getName());
        }

        httpParams0 = httpParams0.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT , (Integer) connectionTimeout);
        httpParams0 = httpParams0.setIntParameter(CoreConnectionPNames.SO_TIMEOUT ,  (Integer) socketTimeout);
        httpClient.setParams(httpParams0);

        HttpResponse httpResponse = null;
        try {
            org.apache.http.Header[] allheaders = hcRequest.getAllHeaders();
            StringBuffer allheadersSB = new StringBuffer("\nall request headers:");
            String authHeadVal = null;
            for (int ii = 0; ii < allheaders.length; ii++) {
                allheadersSB.append("\n" + allheaders[ii].getName() + " : " + allheaders[ii].getValue());
                if (allheaders[ii].getName().equals("Authorization")) {
                    authHeadVal = allheaders[ii].getValue();
                }
            }
            logger.info("request: " + hcRequest.getMethod() + " " + hcRequest.getURI() + allheadersSB);
            if (authHeadVal.toUpperCase().startsWith("OAUTH")
                    && (! authHeadVal.toUpperCase().contains("REALM=\""))) {
                authHeadVal = "OAuth " + "realm=\"\", " + authHeadVal.substring(6).trim();
                hcRequest.setHeader("Authorization", authHeadVal);

                allheaders = hcRequest.getAllHeaders();
                allheadersSB = new StringBuffer("\nALL REQUEST HEADERS:");
                for (int ii = 0; ii < allheaders.length; ii++) {
                    allheadersSB.append("\n" + allheaders[ii].getName() + " : " + allheaders[ii].getValue());
                }
                logger.info(allheadersSB.toString());
            }


            org.apache.http.RequestLine testRL = hcRequest.getRequestLine();
            logger.info("hcRequest:\n" +
                    "  getMethod(): " + hcRequest.getMethod() +
                    "  getProcolVersion(): " + hcRequest.getProtocolVersion() +
                    "  getRequestLine(): " + testRL.getMethod() + "   " +
                                   testRL.getUri() + "   " + testRL.getProtocolVersion() +
                    "  getUri(): " + hcRequest.getURI()
            );

            httpResponse = httpClient.execute(hcRequest);
        } catch (IOException ioe) {
            logger.warn("connectionTimeout, socketTimeout: " + connectionTimeout + ", " + socketTimeout);
            throw new SmartClientException("connectionTimeout, socketTimeout: " + connectionTimeout + ", " + socketTimeout, ioe);
        }

        return httpResponse;
    }

}