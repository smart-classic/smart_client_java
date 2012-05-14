

// Start of EndClient.java

//#if(1 == 2)
/**
 * This is both valid Java and valid Velocity template
 * Valid Java so it can be edited as Java, there are only a few velocity directives
 * Valid Velocity so it can be used to easily generate SMART client
 * This is meant to be input to Velocity.mergeTemplate(...).
 * There is no need to compile this file (it may be compiled incidentally)
 * The result of this file, after mergeTemplate, is part of a needed Java class.
 */

package org.smartplatforms.client.codegen;  /* NOT PART OF GENERATED CLIENT */

import java.util.List;  /* NOT PART OF GENERATED CLIENT */
import java.util.ArrayList;  /* NOT PART OF GENERATED CLIENT */
import java.util.Map;  /* NOT PART OF GENERATED CLIENT */
import java.util.HashMap;  /* NOT PART OF GENERATED CLIENT */

import org.apache.http.impl.client.AbstractHttpClient;  /* NOT PART OF GENERATED CLIENT */
import org.apache.http.impl.client.DefaultHttpClient;  /* NOT PART OF GENERATED CLIENT */
import org.apache.http.client.methods.HttpGet;  /* NOT PART OF GENERATED CLIENT */
import org.apache.http.client.methods.HttpPost;  /* NOT PART OF GENERATED CLIENT */
import org.apache.http.params.HttpParams;  /* NOT PART OF GENERATED CLIENT */
import org.apache.http.client.methods.HttpUriRequest;  /* NOT PART OF GENERATED CLIENT */
import org.apache.http.entity.ByteArrayEntity;  /* NOT PART OF GENERATED CLIENT */

import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;  /* NOT PART OF GENERATED CLIENT */
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;  /* NOT PART OF GENERATED CLIENT */
import oauth.signpost.OAuthProviderListener;  /* NOT PART OF GENERATED CLIENT */
import oauth.signpost.http.HttpParameters;  /* NOT PART OF GENERATED CLIENT */
import oauth.signpost.http.HttpRequest;  /* NOT PART OF GENERATED CLIENT */
import oauth.signpost.http.HttpResponse;  /* NOT PART OF GENERATED CLIENT */

import org.smartplatforms.client.SmartClientException;  /* NOT PART OF GENERATED CLIENT */
import org.smartplatforms.client.Utils;  /* NOT PART OF GENERATED CLIENT */


public class EndClient {  /* NOT PART OF GENERATED CLIENT */
    private String requestTokenURL = null;  /* NOT PART OF GENERATED CLIENT */
    private String accessTokenURL = null;  /* NOT PART OF GENERATED CLIENT */
    private String authorizeURL = null;  /* NOT PART OF GENERATED CLIENT */
    private String consumerKey = null;  /* NOT PART OF GENERATED CLIENT */
    private String consumerSecret = null;  /* NOT PART OF GENERATED CLIENT */
    private String oauthCallback = null;  /* NOT PART OF GENERATED CLIENT */
    private Utils smartUtils = null;  /* NOT PART OF GENERATED CLIENT */
//#end

//#if(!$challenge)
    
    /** get request token
     * @return [token, secret, redirectURL]
    */
    public String[] getRequestToken(String recordId) throws SmartClientException {
        if (requestTokenURL == null) {
            throw new SmartClientException("used constructor without requestTokenURL");
        }
        AbstractHttpClient httpClient = new DefaultHttpClient();
        CommonsHttpOAuthProvider oprov = new CommonsHttpOAuthProvider(
                requestTokenURL, accessTokenURL, authorizeURL, httpClient);
        oprov.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        CommonsHttpOAuthConsumer oauthConsumer =
                new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);

        String urlWithRequestToken = null;
        String[] tokenSecret = null;

        try {
            String oldDebug = System.getProperty("debug");
            System.setProperty("debug", "true");

            List<String[]> additionalParams = new ArrayList<String[]>();
            additionalParams.add(new String[] { "offline", "true" } );
            additionalParams.add(new String[] { "record_id", recordId /*\"2000000008\"*/ } );
            oprov.setListener(new OAuthProviderListenerForSmart(additionalParams));
            urlWithRequestToken =  oprov.retrieveRequestToken(oauthConsumer, oauthCallback);// TODO FIXME oprov.retrieveRequestTokenAdditionalParameters( oauthConsumer, oauthCallback, \"offline\", \"true\", \"record_id\", recordId);
            System.out.println("consumer token/secret: " + oauthConsumer.getToken() + '/' + oauthConsumer.getTokenSecret());
            tokenSecret = new String[] {
                oauthConsumer.getToken() , oauthConsumer.getTokenSecret(), urlWithRequestToken
            };

            if (oldDebug == null) { System.clearProperty("debug"); }
            else { System.setProperty("debug", oldDebug); }
        } catch (oauth.signpost.exception.OAuthMessageSignerException mse) {
            throw new SmartClientException(mse);
        } catch (oauth.signpost.exception.OAuthNotAuthorizedException nae) {
            throw new SmartClientException(nae);
        } catch (oauth.signpost.exception.OAuthExpectationFailedException efe) {
            throw new SmartClientException(efe);
        } catch (oauth.signpost.exception.OAuthCommunicationException comE) {
            throw new SmartClientException(comE);
        }
System.out.println("requestToken: " + urlWithRequestToken);
        return tokenSecret; //urlWithRequestToken;
    }

    public String[] getAccessToken(String requestToken, String requestTokenSecret, String verifier) throws SmartClientException {
        AbstractHttpClient httpClient = new DefaultHttpClient();
        CommonsHttpOAuthProvider oprov = new CommonsHttpOAuthProvider(
                requestTokenURL, accessTokenURL, authorizeURL, httpClient);
        CommonsHttpOAuthConsumer oauthConsumer =
                new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
        System.out.println("in getAccessToken: " + accessTokenURL + " -- " +
                consumerKey + ", " + consumerSecret + ", " +
                requestToken + ", " + requestTokenSecret + ", " + verifier);
        oauthConsumer.setTokenWithSecret(requestToken, requestTokenSecret);
        try {
            // this should mutate oauthConsuer, adding the authorized token/secret
            String oldDebug = System.getProperty("debug");
            System.setProperty("debug", "true");

            oprov.retrieveAccessToken(oauthConsumer, verifier);

            if (oldDebug == null) { System.clearProperty("debug"); }
            else { System.setProperty("debug", oldDebug); }
        } catch (oauth.signpost.exception.OAuthMessageSignerException mse) {
            throw new SmartClientException(mse);
        } catch (oauth.signpost.exception.OAuthNotAuthorizedException nae) {
            throw new SmartClientException(nae);
        } catch (oauth.signpost.exception.OAuthExpectationFailedException efe) {
            throw new SmartClientException(efe);
        } catch (oauth.signpost.exception.OAuthCommunicationException comE) {
            throw new SmartClientException(comE);
        }
        String[] retVal = new String[2];
        retVal[0] = oauthConsumer.getToken();
        retVal[1] = oauthConsumer.getTokenSecret();
        if (retVal[0] == null || retVal[1] == null) {
            throw new SmartClientException("retrieveAccessToken returned null token and/or secret: " + retVal[0] + "   " + retVal[1]);
        }
        return retVal;
    }
    
    /*
    public String[] getAccessToken_GET(String requestToken, String requestTokenSecret, String verifier)
            throws SmartClientException {
        AbstractHttpClient httpClient = new DefaultHttpClient();
        CommonsHttpOAuthConsumer oauthConsumer =
                new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
        System.out.println("in getAccessToken_GET: " + accessTokenURL + " -- " +
                consumerKey + ", " + consumerSecret + ", " +
                requestToken + ", " + requestTokenSecret + ", " + verifier);
        HttpParameters addParams = new HttpParameters();
        addParams.put("oauth_verifier", verifier, true);
        oauthConsumer.setAdditionalParameters(addParams);
        oauthConsumer.setTokenWithSecret(requestToken, requestTokenSecret);
        String srr = null;
        try {
            // this should mutate oauthConsuer, adding the authorized token/secret\n" +
            String oldDebug = System.getProperty("debug");
            System.setProperty("debug", "true");

            HttpUriRequest hcRequest = new HttpGet(accessTokenURL);
            oauthConsumer.sign(hcRequest);
            Map<String,Object> options = new HashMap<String,Object>();
            org.apache.http.HttpResponse httpResponse =
                    smartUtils.smartExecute(hcRequest, options);
            srr = (String) smartUtils.smartRequestResponse(httpResponse, "GET " + accessTokenURL, options);

            System.out.println("access token response: " + srr.getClass().getName()  + ": " + srr);

            if (oldDebug == null) { System.clearProperty("debug"); }
            else { System.setProperty("debug", oldDebug); }
        } catch (oauth.signpost.exception.OAuthMessageSignerException mse) {
            throw new SmartClientException(mse);
        } catch (oauth.signpost.exception.OAuthExpectationFailedException efe) {
            throw new SmartClientException(efe);
        } catch (oauth.signpost.exception.OAuthCommunicationException comE) {
            throw new SmartClientException(comE);
        }
        String[] retVal = new String[2];
        String[] srrA = srr.split("&");
        for (int ii = 0; ii < srrA.length; ii++) {
            String[] srrAA = srrA[ii].split("=");
            if (srrAA.length != 2) {
                throw new SmartClientException("unexpected response from getAccessToken: " + srr);
            }
            if (srrAA[0].equals("ouath_token")) {
                retVal[0] = srrAA[1];
            } else if (srrAA[0].equals("oauth_token_secret")) {
                retVal[1] = srrAA[1];
            }
        }
        if (retVal[0] == null || retVal[1] == null) {
            throw new SmartClientException("retrieveAccessToken did not return token and/or secret: " + srr);
        }
        return retVal;
    }
    */
    
//    private String prepareQueryString(String... pairs) throws SmartClientException {
//        if (pairs.length % 2 != 0) {
//            throw new SmartClientException("not an even number of query param values, should be two for each name=value pair");
//        }
//
//        StringBuffer retVal = new StringBuffer();
//        if (pairs.length > 0) { retVal.append("?"); }
//        for (int ii = 0; ii < pairs.length; ii += 2) {
//            String firstOfPair = pairs[ii];
//            String secondOfPair = pairs[ii +1];
//            if (ii > 0) { retVal.append('&'); }
//            retVal.append(urlEncode(firstOfPair));
//            retVal.append('=');
//            retVal.append(urlEncode(secondOfPair));
//        }
//        return retVal.toString();
//    }
    
class OAuthProviderListenerForSmart implements OAuthProviderListener {
    List<String[]> additionalParams = null;

    OAuthProviderListenerForSmart(List<String[]> additionalParams) {
        this.additionalParams = additionalParams;
    }

    // implement OAuthProviderListener\n" +
    @Override
    public boolean onResponseReceived(HttpRequest request, HttpResponse response) {
        return false; // not handled here, use default handling
    }

    @Override
    public void prepareRequest(HttpRequest request) throws SmartClientException {
        System.out.println("request: " + request.getClass().getName() + "  " + request.getContentType());
        Object uwr = request.unwrap();
        System.out.println("UUU: " + uwr.getClass().getName());
        if (! (uwr instanceof HttpPost)) {
            throw new SmartClientException(
                    "OAuthProvider request was expected to be an instance of HttpPost: " + uwr.getClass().getName());
        }
        HttpPost httpPost = (HttpPost) uwr;

        HttpParams httpParams = httpPost.getParams();

        StringBuffer paramsAsString = new StringBuffer();
        for (String[] kv : additionalParams) {
            System.out.println("adding: " + kv[0] + ", " + kv[1]);
            if (paramsAsString.length() > 0) {
                paramsAsString.append('&');
            }
            paramsAsString.append(kv[0] + '=' + kv[1]);  // do encoding !!!!  FIXME\n" +
        }

        System.out.println("paramsAsString: " + paramsAsString);

        ByteArrayEntity bae = new ByteArrayEntity(paramsAsString.toString().getBytes());
        bae.setContentType("application/x-www-form-urlencoded");//application/x-www-form-urlencoded
        httpPost.setEntity(bae);
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        System.out.println("request www entity: " + request.getClass().getName() + "  " + request.getContentType());
        System.out.println("entity ct: " + bae.getContentType());
    }

    @Override
    public void	prepareSubmission(HttpRequest request) {} //Called after the request has been signed, but before it's being sent.
}

    private String urlEncode(String toEncode) throws org.smartplatforms.client.SmartClientException {
        try {
            return java.net.URLEncoder.encode(toEncode,"UTF-8");
        } catch (java.io.UnsupportedEncodingException uee) {
            throw new org.smartplatforms.client.SmartClientException(uee);
        }
    }
//#end## end of if(!$challenge)

//#if (1 == 2)
}  /* NOT PART OF GENERATED CLIENT */
//#end