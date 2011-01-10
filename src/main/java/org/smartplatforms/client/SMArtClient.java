package org.smartplatforms.client;

import org.openrdf.repository.RepositoryConnection;

import java.io.InputStream;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.client.methods.HttpUriRequest;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpParams;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.StatusLine;
import org.apache.http.HttpEntity;

import org.openrdf.repository.RepositoryConnection;

import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpResponse;
import oauth.signpost.http.HttpParameters;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.OAuthProviderListener;

public class SMArtClient {

    private String consumerKey = null;
    private String consumerSecret = null;
    private String requestTokenURL = null;
    private String accessTokenURL = null;
    private String oauthCallback = null;
    private String authorizeURL = null;
    private Utils smartUtils = null;
    int httpTimeout = 30000;

    public SMArtClient(String consumerKey, String consumerSecret, String baseURL)
            throws SMArtClientException {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        smartUtils = new Utils(consumerKey, consumerSecret, baseURL,
                new DefaultResponseTypeConversion(), httpTimeout);
    }

    /**
    for example.
    'api_base' :          'http://sandbox-api.smartplatforms.org',
    'request_token_url' : 'http://sandbox-api.smartplatforms.org/oauth/request_token',
    'authorize_url':      'http://sandbox.smartplatforms.org/oauth/authorize',
    'access_token_url':   'http://sandbox-api.smartplatforms.org/oauth/access_token',
    'oauth_callback' :    'oob'
    */
    public SMArtClient(String consumerKey, String consumerSecret, String baseURL,
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
    public void setAccessToken(HttpServletRequest r){
    	this.smartUtils.setAccessToken(r);
    }
    
    public void setAccessToken(String token, String secret){
    	this.smartUtils.setAccessToken(token, secret);
    }
    /** Get a single allergy -- records/{record_id}/allergies/{allergy_id}
    * @param recordId server's record ID
    * @param allergyId server's internal ID for this allergy document
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_allergies_XGET(String recordId, String allergyId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/allergies/" + allergyId;
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Delete a single allergy -- records/{record_id}/allergies/{allergy_id}
    * @param recordId server's record ID
    * @param allergyId server's internal ID for this allergy document
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_allergies_XDELETE(String recordId, String allergyId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/allergies/" + allergyId;
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** GET all allergies for a record -- records/{record_id}/allergies/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_allergies_GET(String recordId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/allergies/";
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Delete all allergies from a record -- records/{record_id}/allergies/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_allergies_DELETE(String recordId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/allergies/";
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Add new allergies to a record -- records/{record_id}/allergies/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param requestBody data to send as request body
    * @param requestBodyContentType typically application/x-www-form-urlencoded
    * @param options see class note
    */
    public Object records_X_allergies_POST(String recordId,
            String accessToken, String accessTokenSecret, String requestBody, String requestContentType, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/allergies/";
        return smartUtils.smartRequest("POST", restURL, null, accessToken, accessTokenSecret, requestBody, requestContentType, null, options);
    }

    /** Get a single allergy -- records/{record_id}/allergies/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_allergies_external_id_XGET(String recordId, String externalId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/allergies/external_id/" + externalId;
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Add a single allergy by external key -- records/{record_id}/allergies/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param requestBody data to send as request body
    * @param requestBodyContentType typically application/x-www-form-urlencoded
    * @param options see class note
    */
    public Object records_X_allergies_external_id_XPUT(String recordId, String externalId,
            String accessToken, String accessTokenSecret, String requestBody, String requestContentType, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/allergies/external_id/" + externalId;
        return smartUtils.smartRequest("PUT", restURL, null, accessToken, accessTokenSecret, requestBody, requestContentType, null, options);
    }

    /** Delete a single allergy -- records/{record_id}/allergies/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_allergies_external_id_XDELETE(String recordId, String externalId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/allergies/external_id/" + externalId;
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Get a single fulfillment -- records/{record_id}/fulfillments/{fulfillment_id}
    * @param recordId server's record ID
    * @param fulfillmentId server's internal ID for this fulfillment document
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_fulfillments_XGET(String recordId, String fulfillmentId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/fulfillments/" + fulfillmentId;
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Delete a single fulfillment -- records/{record_id}/fulfillments/{fulfillment_id}
    * @param recordId server's record ID
    * @param fulfillmentId server's internal ID for this fulfillment document
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_fulfillments_XDELETE(String recordId, String fulfillmentId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/fulfillments/" + fulfillmentId;
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Get a single fulfillment -- records/{record_id}/fulfillments/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_fulfillments_external_id_XGET(String recordId, String externalId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/fulfillments/external_id/" + externalId;
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Delete a single fulfillment -- records/{record_id}/fulfillments/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_fulfillments_external_id_XDELETE(String recordId, String externalId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/fulfillments/external_id/" + externalId;
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Put a single fulfillment by its external key -- records/{record_id}/fulfillments/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param requestBody data to send as request body
    * @param requestBodyContentType typically application/x-www-form-urlencoded
    * @param options see class note
    */
    public Object records_X_fulfillments_external_id_XPUT(String recordId, String externalId,
            String accessToken, String accessTokenSecret, String requestBody, String requestContentType, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/fulfillments/external_id/" + externalId;
        return smartUtils.smartRequest("PUT", restURL, null, accessToken, accessTokenSecret, requestBody, requestContentType, null, options);
    }

    /** Get all fulfillments in the record. -- records/{record_id}/fulfillments/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_fulfillments_GET(String recordId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/fulfillments/";
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Delete all fulfillments in the record. -- records/{record_id}/fulfillments/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_fulfillments_DELETE(String recordId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/fulfillments/";
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Add fulfillments in the absence of a medication -- records/{record_id}/fulfillments/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param requestBody data to send as request body
    * @param requestBodyContentType typically application/x-www-form-urlencoded
    * @param options see class note
    */
    public Object records_X_fulfillments_POST(String recordId,
            String accessToken, String accessTokenSecret, String requestBody, String requestContentType, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/fulfillments/";
        return smartUtils.smartRequest("POST", restURL, null, accessToken, accessTokenSecret, requestBody, requestContentType, null, options);
    }

    /** Get all fulfillments for a given medication -- records/{record_id}/medications/{medication_id}/fulfillments/
    * @param recordId server's record ID
    * @param medicationId server's internal ID for this medication document
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_medications_X_fulfillments_GET(String recordId, String medicationId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/" + medicationId + "/fulfillments/";
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Deletefulfillments for a given medication -- records/{record_id}/medications/{medication_id}/fulfillments/
    * @param recordId server's record ID
    * @param medicationId server's internal ID for this medication document
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_medications_X_fulfillments_DELETE(String recordId, String medicationId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/" + medicationId + "/fulfillments/";
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Put a single fulfillmentby its external key -- records/{record_id}/medications/external_id/{med_external_id}/fulfillments/external_id/{external_id}
    * @param recordId server's record ID
    * @param medExternalId ID assigned to the medication document encompassing this fulfillment
    * @param externalId external ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param requestBody data to send as request body
    * @param requestBodyContentType typically application/x-www-form-urlencoded
    * @param options see class note
    */
    public Object records_X_medications_external_id_X_fulfillments_external_id_XPUT(String recordId, String medExternalId, String externalId,
            String accessToken, String accessTokenSecret, String requestBody, String requestContentType, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/external_id/" + medExternalId + "/fulfillments/external_id/" + externalId;
        return smartUtils.smartRequest("PUT", restURL, null, accessToken, accessTokenSecret, requestBody, requestContentType, null, options);
    }

    /** Add fulfillments for a given medication -- records/{record_id}/medications/{medication_id}/fulfillments/
    * @param recordId server's record ID
    * @param medicationId server's internal ID for this medication document
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param requestBody data to send as request body
    * @param requestBodyContentType typically application/x-www-form-urlencoded
    * @param options see class note
    */
    public Object records_X_medications_X_fulfillments_POST(String recordId, String medicationId,
            String accessToken, String accessTokenSecret, String requestBody, String requestContentType, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/" + medicationId + "/fulfillments/";
        return smartUtils.smartRequest("POST", restURL, null, accessToken, accessTokenSecret, requestBody, requestContentType, null, options);
    }

    /** Get a single medication -- records/{record_id}/medications/{medication_id}
    * @param recordId server's record ID
    * @param medicationId server's internal ID for this medication document
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_medications_XGET(String recordId, String medicationId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/" + medicationId;
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Delete a single medication -- records/{record_id}/medications/{medication_id}
    * @param recordId server's record ID
    * @param medicationId server's internal ID for this medication document
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_medications_XDELETE(String recordId, String medicationId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/" + medicationId;
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Get a single medication -- records/{record_id}/medications/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_medications_external_id_XGET(String recordId, String externalId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/external_id/" + externalId;
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Delete a single medication -- records/{record_id}/medications/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_medications_external_id_XDELETE(String recordId, String externalId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/external_id/" + externalId;
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Put a single medication by its external key -- records/{record_id}/medications/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param requestBody data to send as request body
    * @param requestBodyContentType typically application/x-www-form-urlencoded
    * @param options see class note
    */
    public Object records_X_medications_external_id_XPUT(String recordId, String externalId,
            String accessToken, String accessTokenSecret, String requestBody, String requestContentType, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/external_id/" + externalId;
        return smartUtils.smartRequest("PUT", restURL, null, accessToken, accessTokenSecret, requestBody, requestContentType, null, options);
    }

    /** Get all medications for a given record -- records/{record_id}/medications/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_medications_GET(String recordId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/";
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Delete all medications from a given record -- records/{record_id}/medications/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_medications_DELETE(String recordId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/";
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Add medications to a given record -- records/{record_id}/medications/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param requestBody data to send as request body
    * @param requestBodyContentType typically application/x-www-form-urlencoded
    * @param options see class note
    */
    public Object records_X_medications_POST(String recordId,
            String accessToken, String accessTokenSecret, String requestBody, String requestContentType, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/";
        return smartUtils.smartRequest("POST", restURL, null, accessToken, accessTokenSecret, requestBody, requestContentType, null, options);
    }

    /** Get a single note -- records/{record_id}/notes/{note_id}
    * @param recordId server's record ID
    * @param noteId server's internal ID for this note document
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_notes_XGET(String recordId, String noteId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/notes/" + noteId;
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Delete a single note -- records/{record_id}/notes/{note_id}
    * @param recordId server's record ID
    * @param noteId server's internal ID for this note document
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_notes_XDELETE(String recordId, String noteId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/notes/" + noteId;
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Get a single note -- records/{record_id}/notes/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_notes_external_id_XGET(String recordId, String externalId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/notes/external_id/" + externalId;
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Delete a single note -- records/{record_id}/notes/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_notes_external_id_XDELETE(String recordId, String externalId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/notes/external_id/" + externalId;
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Put a single note by its external key -- records/{record_id}/notes/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param requestBody data to send as request body
    * @param requestBodyContentType typically application/x-www-form-urlencoded
    * @param options see class note
    */
    public Object records_X_notes_external_id_XPUT(String recordId, String externalId,
            String accessToken, String accessTokenSecret, String requestBody, String requestContentType, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/notes/external_id/" + externalId;
        return smartUtils.smartRequest("PUT", restURL, null, accessToken, accessTokenSecret, requestBody, requestContentType, null, options);
    }

    /** Get all notes for a given record -- records/{record_id}/notes/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_notes_GET(String recordId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/notes/";
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Delete all notes from a given record -- records/{record_id}/notes/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_notes_DELETE(String recordId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/notes/";
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Add notes to a given record -- records/{record_id}/notes/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param requestBody data to send as request body
    * @param requestBodyContentType typically application/x-www-form-urlencoded
    * @param options see class note
    */
    public Object records_X_notes_POST(String recordId,
            String accessToken, String accessTokenSecret, String requestBody, String requestContentType, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/notes/";
        return smartUtils.smartRequest("POST", restURL, null, accessToken, accessTokenSecret, requestBody, requestContentType, null, options);
    }

    /** Get a single problem -- records/{record_id}/problems/{problem_id}
    * @param recordId server's record ID
    * @param problemId server's internal ID for this problem document
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_problems_XGET(String recordId, String problemId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/problems/" + problemId;
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Delete a single problem -- records/{record_id}/problems/{problem_id}
    * @param recordId server's record ID
    * @param problemId server's internal ID for this problem document
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_problems_XDELETE(String recordId, String problemId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/problems/" + problemId;
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Get a single problem -- records/{record_id}/problems/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_problems_external_id_XGET(String recordId, String externalId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/problems/external_id/" + externalId;
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Delete a single problem -- records/{record_id}/problems/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_problems_external_id_XDELETE(String recordId, String externalId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/problems/external_id/" + externalId;
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Put a single problem by its external key -- records/{record_id}/problems/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param requestBody data to send as request body
    * @param requestBodyContentType typically application/x-www-form-urlencoded
    * @param options see class note
    */
    public Object records_X_problems_external_id_XPUT(String recordId, String externalId,
            String accessToken, String accessTokenSecret, String requestBody, String requestContentType, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/problems/external_id/" + externalId;
        return smartUtils.smartRequest("PUT", restURL, null, accessToken, accessTokenSecret, requestBody, requestContentType, null, options);
    }

    /** Get all problems for a given record -- records/{record_id}/problems/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_problems_GET(String recordId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/problems/";
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Delete all problems from a given record -- records/{record_id}/problems/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_problems_DELETE(String recordId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/problems/";
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Add problems to a given record -- records/{record_id}/problems/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param requestBody data to send as request body
    * @param requestBodyContentType typically application/x-www-form-urlencoded
    * @param options see class note
    */
    public Object records_X_problems_POST(String recordId,
            String accessToken, String accessTokenSecret, String requestBody, String requestContentType, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/problems/";
        return smartUtils.smartRequest("POST", restURL, null, accessToken, accessTokenSecret, requestBody, requestContentType, null, options);
    }

    /** Get all demographics for a patient -- records/{record_id}/demographics
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_demographicsGET(String recordId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/demographics";
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Replace all demographics for a patient with a newly-supplied graph -- records/{record_id}/demographics
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param requestBody data to send as request body
    * @param requestBodyContentType typically application/x-www-form-urlencoded
    * @param options see class note
    */
    public Object records_X_demographicsPUT(String recordId,
            String accessToken, String accessTokenSecret, String requestBody, String requestContentType, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/demographics";
        return smartUtils.smartRequest("PUT", restURL, null, accessToken, accessTokenSecret, requestBody, requestContentType, null, options);
    }

    /** Find users by name (or all users if blank) -- users/search?givenName={givenName}&familyName={familyName}
    * @param givenName {givenName}
    * @param familyName {familyName}
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object users_searchGET(String givenName, String familyName,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "users/search" + prepareQueryString("givenName", givenName, "familyName", familyName);
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Get a single user by internal ID -- users/{user_id}
    * @param userId server's internal ID for this user
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object users_XGET(String userId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "users/" + userId;
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Get a list of all auxiliiary container capabilities -- capabilities/
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object capabilities_GET(
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "capabilities/";
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Retrieve ontology used by a SMArt container -- ontology
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object ontologyGET(
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "ontology";
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Get a single lab result -- records/{record_id}/lab_results/{lab_result_id}
    * @param recordId server's record ID
    * @param labResultId null
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_lab_results_XGET(String recordId, String labResultId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/lab_results/" + labResultId;
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Delete a single lab result -- records/{record_id}/lab_results/{lab_result_id}
    * @param recordId server's record ID
    * @param labResultId null
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_lab_results_XDELETE(String recordId, String labResultId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/lab_results/" + labResultId;
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Get all lab results for a given record -- records/{record_id}/lab_results/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_lab_results_GET(String recordId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/lab_results/";
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Delete all lab results from a given record -- records/{record_id}/lab_results/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_lab_results_DELETE(String recordId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/lab_results/";
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Add lab results to a given record -- records/{record_id}/lab_results/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param requestBody data to send as request body
    * @param requestBodyContentType typically application/x-www-form-urlencoded
    * @param options see class note
    */
    public Object records_X_lab_results_POST(String recordId,
            String accessToken, String accessTokenSecret, String requestBody, String requestContentType, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/lab_results/";
        return smartUtils.smartRequest("POST", restURL, null, accessToken, accessTokenSecret, requestBody, requestContentType, null, options);
    }

    /** Add lab results to a given record by external key -- records/{record_id}/lab_results/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param requestBody data to send as request body
    * @param requestBodyContentType typically application/x-www-form-urlencoded
    * @param options see class note
    */
    public Object records_X_lab_results_external_id_XPUT(String recordId, String externalId,
            String accessToken, String accessTokenSecret, String requestBody, String requestContentType, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/lab_results/external_id/" + externalId;
        return smartUtils.smartRequest("PUT", restURL, null, accessToken, accessTokenSecret, requestBody, requestContentType, null, options);
    }

    /** Get a single lab result panel -- records/{record_id}/lab_result_panels/{lab_result_panel_id}
    * @param recordId server's record ID
    * @param labResultPanelId null
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_lab_result_panels_XGET(String recordId, String labResultPanelId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/lab_result_panels/" + labResultPanelId;
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Delete a single lab result panel -- records/{record_id}/lab_result_panels/{lab_result_panel_id}
    * @param recordId server's record ID
    * @param labResultPanelId null
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_lab_result_panels_XDELETE(String recordId, String labResultPanelId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/lab_result_panels/" + labResultPanelId;
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Get all lab result panels for a given record -- records/{record_id}/lab_result_panels/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_lab_result_panels_GET(String recordId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/lab_result_panels/";
        return smartUtils.smartRequest("GET", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Delete all lab result panels from a given record -- records/{record_id}/lab_result_panels/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param options see class note
    */
    public Object records_X_lab_result_panels_DELETE(String recordId,
            String accessToken, String accessTokenSecret, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/lab_result_panels/";
        return smartUtils.smartRequest("DELETE", restURL, null, accessToken, accessTokenSecret, null, null, null, options);
    }

    /** Add lab result panels to a given record -- records/{record_id}/lab_result_panels/
    * @param recordId server's record ID
    * @param accessToken OAuth access token
    * @param accessTokenSecret OAuth access token secret
    * @param requestBody data to send as request body
    * @param requestBodyContentType typically application/x-www-form-urlencoded
    * @param options see class note
    */
    public Object records_X_lab_result_panels_POST(String recordId,
            String accessToken, String accessTokenSecret, String requestBody, String requestContentType, Map<String,Object> options)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/lab_result_panels/";
        return smartUtils.smartRequest("POST", restURL, null, accessToken, accessTokenSecret, requestBody, requestContentType, null, options);
    }

    /** get request token
     * @return [token, secret, redirectURL]
    */
    public String[] getRequestToken(String recordId) throws SMArtClientException {
        if (requestTokenURL == null) {
            throw new SMArtClientException("used constructor without requestTokenURL");
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
            additionalParams.add(new String[] { "record_id", recordId /*"2000000008"*/ } );
            oprov.setListener(new OAuthProviderListenerForSMArt(additionalParams));
            urlWithRequestToken =  oprov.retrieveRequestToken(oauthConsumer, oauthCallback);// TODO FIXME oprov.retrieveRequestTokenAdditionalParameters( oauthConsumer, oauthCallback, "offline", "true", "record_id", recordId);
            System.out.println("consumer token/secret: " + oauthConsumer.getToken() + '/' + oauthConsumer.getTokenSecret());
            tokenSecret = new String[] {
                oauthConsumer.getToken() , oauthConsumer.getTokenSecret(), urlWithRequestToken
            };

            if (oldDebug == null) { System.clearProperty("debug"); }
            else { System.setProperty("debug", oldDebug); }
        } catch (oauth.signpost.exception.OAuthMessageSignerException mse) {
            throw new SMArtClientException(mse);
        } catch (oauth.signpost.exception.OAuthNotAuthorizedException nae) {
            throw new SMArtClientException(nae);
        } catch (oauth.signpost.exception.OAuthExpectationFailedException efe) {
            throw new SMArtClientException(efe);
        } catch (oauth.signpost.exception.OAuthCommunicationException comE) {
            throw new SMArtClientException(comE);
        }
System.out.println("requestToken: " + urlWithRequestToken);
        return tokenSecret; //urlWithRequestToken;
    }

    public String[] getAccessToken(String requestToken, String requestTokenSecret, String verifier) throws SMArtClientException {
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
            throw new SMArtClientException(mse);
        } catch (oauth.signpost.exception.OAuthNotAuthorizedException nae) {
            throw new SMArtClientException(nae);
        } catch (oauth.signpost.exception.OAuthExpectationFailedException efe) {
            throw new SMArtClientException(efe);
        } catch (oauth.signpost.exception.OAuthCommunicationException comE) {
            throw new SMArtClientException(comE);
        }
        String[] retVal = new String[2];
        retVal[0] = oauthConsumer.getToken();
        retVal[1] = oauthConsumer.getTokenSecret();
        if (retVal[0] == null || retVal[1] == null) {
            throw new SMArtClientException("retrieveAccessToken returned null token and/or secret: " + retVal[0] + "   " + retVal[1]);
        }
        return retVal;
    }

    public String[] getAccessToken_GET(String requestToken, String requestTokenSecret, String verifier)
            throws SMArtClientException {
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
            // this should mutate oauthConsuer, adding the authorized token/secret
            String oldDebug = System.getProperty("debug");
            System.setProperty("debug", "true");

            HttpUriRequest hcRequest = new HttpGet(accessTokenURL);
            oauthConsumer.sign(hcRequest);
            Map<String,Object> options = new HashMap<String,Object>();
            org.apache.http.HttpResponse httpResponse =
                    smartUtils.smartExecute(hcRequest, options);
            srr = (String) smartUtils.smartRequestResponse(httpResponse, null, "GET " + accessTokenURL, options);

            System.out.println("access token response: " + srr.getClass().getName()  + ": " + srr);

            if (oldDebug == null) { System.clearProperty("debug"); }
            else { System.setProperty("debug", oldDebug); }
        } catch (oauth.signpost.exception.OAuthMessageSignerException mse) {
            throw new SMArtClientException(mse);
        } catch (oauth.signpost.exception.OAuthExpectationFailedException efe) {
            throw new SMArtClientException(efe);
        } catch (oauth.signpost.exception.OAuthCommunicationException comE) {
            throw new SMArtClientException(comE);
        }
        String[] retVal = new String[2];
        String[] srrA = srr.split("&");
        for (int ii = 0; ii < srrA.length; ii++) {
            String[] srrAA = srrA[ii].split("=");
            if (srrAA.length != 2) {
                throw new SMArtClientException("unexpected response from getAccessToken: " + srr);
            }
            if (srrAA[0].equals("ouath_token")) {
                retVal[0] = srrAA[1];
            } else if (srrAA[0].equals("oauth_token_secret")) {
                retVal[1] = srrAA[1];
            }
        }
//        retVal[0] = oauthConsumer.getToken();
//        retVal[1] = oauthConsumer.getTokenSecret();
        if (retVal[0] == null || retVal[1] == null) {
            throw new SMArtClientException("retrieveAccessToken did not return token and/or secret: " + srr);
        }
        return retVal;
    }

    private String prepareQueryString(String... pairs) throws SMArtClientException {
        if (pairs.length % 2 != 0) {
            throw new SMArtClientException("not an even number of query param values, should be two for each name=value pair");
        }

        StringBuffer retVal = new StringBuffer();
        if (pairs.length > 0) { retVal.append("?"); }
        for (int ii = 0; ii < pairs.length; ii += 2) {
            String firstOfPair = pairs[ii];
            String secondOfPair = pairs[ii +1];
            if (ii > 0) { retVal.append('&'); }
            retVal.append(urlEncode(firstOfPair));
            retVal.append('=');
            retVal.append(urlEncode(secondOfPair));
        }
        return retVal.toString();
    }

class OAuthProviderListenerForSMArt implements OAuthProviderListener {
    List<String[]> additionalParams = null;

    OAuthProviderListenerForSMArt(List<String[]> additionalParams) {
        this.additionalParams = additionalParams;
    }

    // implement OAuthProviderListener
    @Override
    public boolean onResponseReceived(HttpRequest request, HttpResponse response) {
        return false; // not handled here, use default handling
    }

    @Override
    public void prepareRequest(HttpRequest request) throws SMArtClientException {
        System.out.println("request: " + request.getClass().getName() + "  " + request.getContentType());
        Object uwr = request.unwrap();
        System.out.println("UUU: " + uwr.getClass().getName());
        if (! (uwr instanceof HttpPost)) {
            throw new SMArtClientException(
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
            paramsAsString.append(kv[0] + '=' + kv[1]);  // do encoding !!!!  FIXME
            //httpParams.setParameter(kv[0], kv[1]);  // not sure why this is needed also, but it is
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

    private String urlEncode(String toEncode) throws org.smartplatforms.client.SMArtClientException {
        try {
            return java.net.URLEncoder.encode(toEncode,"UTF-8");
        } catch (java.io.UnsupportedEncodingException uee) {
            throw new org.smartplatforms.client.SMArtClientException(uee);
        }
    }
}
