/*

 /**
 * This is both valid Java and valid Velocity template
 * Valid Java so it can be edited as Java, there are only a few velocity directives
 * Valid Velocity so it can be used to easily generate SMArt client
 * This is meant to be input to Velocity.mergeTemplate(...).
 * There is no need to compile this file (it may be compiled incidentally)
 * The result of this file, after mergeTemplate, is part of a needed Java class.

 */
//#if (1 == 2)  ## exclude following line from output
package org.smartplatforms.client.codegen;  /* NOT PART OF GENERATED CLIENT */
//#end
// ${linefeed}package org.smartplatforms.client;  #if (1 == 2)/* UNCOMMENT IN GENERATED CLIENT */#end

import java.util.Map;
//#if(!$challenge)

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpParams;
import org.apache.http.entity.ByteArrayEntity;
import oauth.signpost.OAuthProviderListener;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpResponse;
import oauth.signpost.http.HttpParameters;
//#end

//#if (1 == 2)  ## exclude following line from output
import org.smartplatforms.client.Utils;
import org.smartplatforms.client.DefaultResponseTypeConversion;
import org.smartplatforms.client.SMArtClientException;
//#end

/**
 *
 */
//#if (1 == 2)  ## exclude following line from output
public class StartClient {  /* NOT PART OF GENERATED CLIENT */
//#end
// ${linefeed}public class SMArtClient {    #if (1 == 2)/* UNCOMMENT IN GENERATED CLIENT */#end

    private String consumerKey = null;
    private String consumerSecret = null;
    private String requestTokenURL = null;
    private String accessTokenURL = null;
    private String oauthCallback = null;
    private String authorizeURL = null;
    private Utils smartUtils = null;
    int httpTimeout = 30000;
//#if (1 == 2)  ## exclude following line from output
    public StartClient(  /* NOT PART OF GENERATED CLIENT */
//#end
// ${linefeed}    public SMArtClient(     ${linefeed}#if (1 == 2)/* UNCOMMENT IN GENERATED CLIENT */#end
            String consumerKey, String consumerSecret, String baseURL)
            throws SMArtClientException {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        smartUtils = new Utils(consumerKey, consumerSecret, baseURL,
                new DefaultResponseTypeConversion(), httpTimeout);
    }

//#if(!$challenge)
    
    /**
    for example.
    'api_base' :          'http://sandbox-api.smartplatforms.org',
    'request_token_url' : 'http://sandbox-api.smartplatforms.org/oauth/request_token',
    'authorize_url':      'http://sandbox.smartplatforms.org/oauth/authorize',
    'access_token_url':   'http://sandbox-api.smartplatforms.org/oauth/access_token',
    'oauth_callback' :    'oob'
    */
//#if (1 == 2)  ## exclude following line from output
    public StartClient(  /* NOT PART OF GENERATED CLIENT */
//#end
// ${linefeed}    public SMArtClient(  ${linefeed}#if (1 == 2)/* UNCOMMENT IN GENERATED CLIENT */#end
            String consumerKey, String consumerSecret, String baseURL,
            String requestTokenURL, String accessTokenURL, String authorizeURL, String oauthCallback)
            throws SMArtClientException {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.requestTokenURL = requestTokenURL;
        this.authorizeURL = authorizeURL; // this param not actually used, because authorize address is in the response to requesTokenURL
        this.accessTokenURL = accessTokenURL;
        this.oauthCallback = oauthCallback;
        smartUtils = new Utils(consumerKey, consumerSecret, baseURL,
                new DefaultResponseTypeConversion(), httpTimeout);
    }
//#end
    // END OF StartClient.java
//#if (1 == 2)  ## exclude following line from output
}  /* NOT PART OF GENERATED CLIENT */
//#end
