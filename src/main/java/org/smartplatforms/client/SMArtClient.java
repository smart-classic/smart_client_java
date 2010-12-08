package org.smartplatforms.client;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.HttpParams;
import org.apache.http.entity.ByteArrayEntity;

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

class SMArtClient {

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


    /** Get a single allergy -- records/{record_id}/allergies/{allergy_id}
    * @param recordId server's record ID
    * @param allergyId server's internal ID for this allergy document
    */
    public Object records_X_allergies_XGET(String recordId, String allergyId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/allergies/" + allergyId;
        return smartUtils.smartRequest(
            "GET", restURL, null, "phaToken", "phaTokenSecret",
            null, null, null, null);
    }

    /** Delete a single allergy -- records/{record_id}/allergies/{allergy_id}
    * @param recordId server's record ID
    * @param allergyId server's internal ID for this allergy document
    */
    public RepositoryConnection records_X_allergies_XDELETE(String recordId, String allergyId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/allergies/" + allergyId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** GET all allergies for a record -- records/{record_id}/allergies/
    * @param recordId server's record ID
    */
    public RepositoryConnection records_X_allergies_GET(String recordId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/allergies/";
        return null /* RepositoryConnection instance will be here */;
    }

    /** Delete all allergies from a record -- records/{record_id}/allergies/
    * @param recordId server's record ID
    */
    public RepositoryConnection records_X_allergies_DELETE(String recordId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/allergies/";
        return null /* RepositoryConnection instance will be here */;
    }

    /** Add new allergies to a record -- records/{record_id}/allergies/
    * @param recordId server's record ID
    */
    public RepositoryConnection records_X_allergies_POST(String recordId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/allergies/";
        return null /* RepositoryConnection instance will be here */;
    }

    /** Get a single allergy -- records/{record_id}/allergies/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    */
    public RepositoryConnection records_X_allergies_external_id_XGET(String recordId, String externalId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/allergies/external_id/" + externalId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Add a single allergy by external key -- records/{record_id}/allergies/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    */
    public RepositoryConnection records_X_allergies_external_id_XPUT(String recordId, String externalId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/allergies/external_id/" + externalId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Delete a single allergy -- records/{record_id}/allergies/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    */
    public RepositoryConnection records_X_allergies_external_id_XDELETE(String recordId, String externalId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/allergies/external_id/" + externalId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Get a single fulfillment -- records/{record_id}/medications/{medication_id}/fulfillments/{fulfillment_id}
    * @param recordId server's record ID
    * @param medicationId server's internal ID for this medication document
    * @param fulfillmentId server's internal ID for this fulfillment document
    */
    public RepositoryConnection records_X_medications_X_fulfillments_XGET(String recordId, String medicationId, String fulfillmentId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/" + medicationId + "/fulfillments/" + fulfillmentId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Delete a single fulfillment -- records/{record_id}/medications/{medication_id}/fulfillments/{fulfillment_id}
    * @param recordId server's record ID
    * @param medicationId server's internal ID for this medication document
    * @param fulfillmentId server's internal ID for this fulfillment document
    */
    public RepositoryConnection records_X_medications_X_fulfillments_XDELETE(String recordId, String medicationId, String fulfillmentId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/" + medicationId + "/fulfillments/" + fulfillmentId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Get a single fulfillment -- records/{record_id}/medications/external_id/{med_external_id}/fulfillments/external_id/{external_id}
    * @param recordId server's record ID
    * @param medExternalId ID assigned to the medication document encompassing this fulfillment
    * @param externalId external ID
    */
    public RepositoryConnection records_X_medications_external_id_X_fulfillments_external_id_XGET(String recordId, String medExternalId, String externalId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/external_id/" + medExternalId + "/fulfillments/external_id/" + externalId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Delete a single fulfillment -- records/{record_id}/medications/external_id/{med_external_id}/fulfillments/external_id/{external_id}
    * @param recordId server's record ID
    * @param medExternalId ID assigned to the medication document encompassing this fulfillment
    * @param externalId external ID
    */
    public RepositoryConnection records_X_medications_external_id_X_fulfillments_external_id_XDELETE(String recordId, String medExternalId, String externalId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/external_id/" + medExternalId + "/fulfillments/external_id/" + externalId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Put a single fulfillment by its external key -- records/{record_id}/medications/external_id/{med_external_id}/fulfillments/external_id/{external_id}
    * @param recordId server's record ID
    * @param medExternalId ID assigned to the medication document encompassing this fulfillment
    * @param externalId external ID
    */
    public RepositoryConnection records_X_medications_external_id_X_fulfillments_external_id_XPUT(String recordId, String medExternalId, String externalId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/external_id/" + medExternalId + "/fulfillments/external_id/" + externalId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Get a all fulfillments for a given medication -- records/{record_id}/medications/{medication_id}/fulfillments/
    * @param recordId server's record ID
    * @param medicationId server's internal ID for this medication document
    */
    public RepositoryConnection records_X_medications_X_fulfillments_GET(String recordId, String medicationId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/" + medicationId + "/fulfillments/";
        return null /* RepositoryConnection instance will be here */;
    }

    /** Delete all fulfillments for a given medication -- records/{record_id}/medications/{medication_id}/fulfillments/
    * @param recordId server's record ID
    * @param medicationId server's internal ID for this medication document
    */
    public RepositoryConnection records_X_medications_X_fulfillments_DELETE(String recordId, String medicationId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/" + medicationId + "/fulfillments/";
        return null /* RepositoryConnection instance will be here */;
    }

    /** Add fulfillments for a given medication -- records/{record_id}/medications/{medication_id}/fulfillments/
    * @param recordId server's record ID
    * @param medicationId server's internal ID for this medication document
    */
    public RepositoryConnection records_X_medications_X_fulfillments_POST(String recordId, String medicationId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/" + medicationId + "/fulfillments/";
        return null /* RepositoryConnection instance will be here */;
    }

    /** Get a single medication -- records/{record_id}/medications/{medication_id}
    * @param recordId server's record ID
    * @param medicationId server's internal ID for this medication document
    */
    public RepositoryConnection records_X_medications_XGET(String recordId, String medicationId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/" + medicationId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Delete a single medication -- records/{record_id}/medications/{medication_id}
    * @param recordId server's record ID
    * @param medicationId server's internal ID for this medication document
    */
    public RepositoryConnection records_X_medications_XDELETE(String recordId, String medicationId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/" + medicationId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Get a single medication -- records/{record_id}/medications/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    */
    public RepositoryConnection records_X_medications_external_id_XGET(String recordId, String externalId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/external_id/" + externalId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Delete a single medication -- records/{record_id}/medications/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    */
    public RepositoryConnection records_X_medications_external_id_XDELETE(String recordId, String externalId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/external_id/" + externalId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Put a single medication by its external key -- records/{record_id}/medications/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    */
    public RepositoryConnection records_X_medications_external_id_XPUT(String recordId, String externalId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/external_id/" + externalId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Get all medications for a given record -- records/{record_id}/medications/
    * @param recordId server's record ID
    */
    public RepositoryConnection records_X_medications_GET(String recordId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/";
        return null /* RepositoryConnection instance will be here */;
    }

    /** Delete all medications from a given record -- records/{record_id}/medications/
    * @param recordId server's record ID
    */
    public RepositoryConnection records_X_medications_DELETE(String recordId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/";
        return null /* RepositoryConnection instance will be here */;
    }

    /** Add medications to a given record -- records/{record_id}/medications/
    * @param recordId server's record ID
    */
    public RepositoryConnection records_X_medications_POST(String recordId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/medications/";
        return null /* RepositoryConnection instance will be here */;
    }

    /** Get a single note -- records/{record_id}/notes/{note_id}
    * @param recordId server's record ID
    * @param noteId server's internal ID for this note document
    */
    public RepositoryConnection records_X_notes_XGET(String recordId, String noteId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/notes/" + noteId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Delete a single note -- records/{record_id}/notes/{note_id}
    * @param recordId server's record ID
    * @param noteId server's internal ID for this note document
    */
    public RepositoryConnection records_X_notes_XDELETE(String recordId, String noteId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/notes/" + noteId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Get a single note -- records/{record_id}/notes/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    */
    public RepositoryConnection records_X_notes_external_id_XGET(String recordId, String externalId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/notes/external_id/" + externalId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Delete a single note -- records/{record_id}/notes/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    */
    public RepositoryConnection records_X_notes_external_id_XDELETE(String recordId, String externalId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/notes/external_id/" + externalId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Put a single note by its external key -- records/{record_id}/notes/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    */
    public RepositoryConnection records_X_notes_external_id_XPUT(String recordId, String externalId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/notes/external_id/" + externalId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Get all notes for a given record -- records/{record_id}/notes/
    * @param recordId server's record ID
    */
    public RepositoryConnection records_X_notes_GET(String recordId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/notes/";
        return null /* RepositoryConnection instance will be here */;
    }

    /** Delete all notes from a given record -- records/{record_id}/notes/
    * @param recordId server's record ID
    */
    public RepositoryConnection records_X_notes_DELETE(String recordId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/notes/";
        return null /* RepositoryConnection instance will be here */;
    }

    /** Add notes to a given record -- records/{record_id}/notes/
    * @param recordId server's record ID
    */
    public RepositoryConnection records_X_notes_POST(String recordId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/notes/";
        return null /* RepositoryConnection instance will be here */;
    }

    /** Get a single problem -- records/{record_id}/problems/{problem_id}
    * @param recordId server's record ID
    * @param problemId server's internal ID for this problem document
    */
    public RepositoryConnection records_X_problems_XGET(String recordId, String problemId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/problems/" + problemId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Delete a single problem -- records/{record_id}/problems/{problem_id}
    * @param recordId server's record ID
    * @param problemId server's internal ID for this problem document
    */
    public RepositoryConnection records_X_problems_XDELETE(String recordId, String problemId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/problems/" + problemId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Get a single problem -- records/{record_id}/problems/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    */
    public RepositoryConnection records_X_problems_external_id_XGET(String recordId, String externalId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/problems/external_id/" + externalId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Delete a single problem -- records/{record_id}/problems/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    */
    public RepositoryConnection records_X_problems_external_id_XDELETE(String recordId, String externalId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/problems/external_id/" + externalId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Put a single problem by its external key -- records/{record_id}/problems/external_id/{external_id}
    * @param recordId server's record ID
    * @param externalId external ID
    */
    public RepositoryConnection records_X_problems_external_id_XPUT(String recordId, String externalId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/problems/external_id/" + externalId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Get all problems for a given record -- records/{record_id}/problems/
    * @param recordId server's record ID
    */
    public RepositoryConnection records_X_problems_GET(String recordId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/problems/";
        return null /* RepositoryConnection instance will be here */;
    }

    /** Delete all problems from a given record -- records/{record_id}/problems/
    * @param recordId server's record ID
    */
    public RepositoryConnection records_X_problems_DELETE(String recordId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/problems/";
        return null /* RepositoryConnection instance will be here */;
    }

    /** Add problems to a given record -- records/{record_id}/problems/
    * @param recordId server's record ID
    */
    public RepositoryConnection records_X_problems_POST(String recordId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/problems/";
        return null /* RepositoryConnection instance will be here */;
    }

    /** Get all demographics for a patient -- records/{record_id}/demographics
    * @param recordId server's record ID
    */
    public RepositoryConnection records_X_demographicsGET(String recordId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/demographics";
        return null /* RepositoryConnection instance will be here */;
    }

    /** Replace all demographics for a patient with a newly-supplied graph -- records/{record_id}/demographics
    * @param recordId server's record ID
    */
    public RepositoryConnection records_X_demographicsPUT(String recordId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "records/" + recordId + "/demographics";
        return null /* RepositoryConnection instance will be here */;
    }

    /** Find users by name (or all users if blank) -- users/search?givenName={givenName}&familyName={familyName}
    * @param givenName {givenName}
    * @param familyName {familyName}
    */
    public RepositoryConnection users_searchGET(String givenName, String familyName)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "users/search" + prepareQueryString("givenName", givenName, "familyName", familyName);
        //'?'  + "givenName=" + urlEncode(givenName) + '&'  + "familyName=" + urlEncode(familyName);
        return null /* RepositoryConnection instance will be here */;
    }

    /** Get a single user by internal ID -- users/{user_id}
    * @param userId server's internal ID for this user
    */
    public RepositoryConnection users_XGET(String userId)
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "users/" + userId;
        return null /* RepositoryConnection instance will be here */;
    }

    /** Get a list of all auxiliary container capabilities -- capabilities/
    */
    public RepositoryConnection capabilities_GET()
            throws org.smartplatforms.client.SMArtClientException {
        String restURL = "capabilities/";
        return null /* RepositoryConnection instance will be here */;
    }

    /** Retrieve ontology used by a SMArt container -- ontology
    */
    public RepositoryConnection ontologyGET() throws org.smartplatforms.client.SMArtClientException {
        String restURL = "ontology";
        return null /* RepositoryConnection instance will be here */;
    }

    /** get request token */
    public String getRequestToken() throws SMArtClientException {
        if (requestTokenURL == null) {
            throw new SMArtClientException("used constructor without requestTokenURL");
        }
        AbstractHttpClient httpClient = new DefaultHttpClient();
        CommonsHttpOAuthProvider oprov = new CommonsHttpOAuthProvider(
                requestTokenURL, accessTokenURL, authorizeURL, httpClient);
        oprov.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        //offline%3Dtrue%26record_id%3D2000000008
        CommonsHttpOAuthConsumer oauthConsumer =
                new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);


        String urlWithRequestToken = null;
        try {
            String oldDebug = System.getProperty("debug");
            System.setProperty("debug", "true");
//        addParams.put("offline", "true");
//        addParams.put("record_id", "2000000008");
//            List<String[]> additionalParams = new ArrayList<String[]>();
//            additionalParams.add(new String[] { "offline", "true" } );
//            additionalParams.add(new String[] { "record_id", "2000000008" } );
//            oprov.setListener(new OAuthProviderListenerForSMArt(additionalParams));

            
//            urlWithRequestToken = oprov.retrieveRequestToken(oauthConsumer, oauthCallback);




        // invalidate current credentials, if any
        oauthConsumer.setTokenWithSecret(null, null);

        // 1.0a expects the callback to be sent while getting the request token.
        // 1.0 service providers would simply ignore this parameter.
///        retrieveToken(oauthConsumer, requestTokenURL, "oauth_callback", "oob",   "offline", "true",   "record_id", "2000000008");

        HttpParameters responseParameters = retrieveToken(
                (DefaultHttpClient) httpClient, oprov, oauthConsumer, requestTokenURL,
             "offline", "true",   "record_id", "2000000008" );

        String callbackConfirmed = responseParameters.getFirst(oauth.signpost.OAuth.OAUTH_CALLBACK_CONFIRMED);
        responseParameters.remove(oauth.signpost.OAuth.OAUTH_CALLBACK_CONFIRMED);
        boolean isOAuth10a = true; //Boolean.TRUE.toString().equals(callbackConfirmed);

        // 1.0 service providers expect the callback as part of the auth URL,
        // Do not send when 1.0a.
//        if (isOAuth10a) {
//            return oauth.signpost.OAuth.addQueryParameters(
//                    authorizationWebsiteUrl, OAuth.OAUTH_TOKEN,
//                consumer.getToken());








//            httpParameters_size = 0;
//            if (oauthConsumer.getRequestParameters() != null) { httpParameters_size = oauthConsumer.getRequestParameters().size(); }
//            System.out.println("CCC getRequestParameters.size():" + httpParameters_size);
            System.out.println("consumer token/secret: " + oauthConsumer.getToken() + "/" + oauthConsumer.getTokenSecret());
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
        return urlWithRequestToken;
    }


    private HttpParameters/*void*/ retrieveToken(DefaultHttpClient httpClient, CommonsHttpOAuthProvider oprov, oauth.signpost.OAuthConsumer consumer, String endpointUrl,
            String... additionalParameters) throws OAuthMessageSignerException,
            OAuthCommunicationException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, SMArtClientException {
        Map<String, String> defaultHeaders = oprov.getRequestHeaders();
        HttpParameters responseParams = null;
        if (consumer.getConsumerKey() == null || consumer.getConsumerSecret() == null) {
            throw new OAuthExpectationFailedException("Consumer key or secret not set");
        }

        HttpRequest request = null;
        HttpResponse response = null;
        try {
        HttpPost tempPost = new HttpPost(endpointUrl);
        request = new oauth.signpost.commonshttp.HttpRequestAdapter(tempPost);
//           request = oprov.createRequest(endpointUrl);
            for (String header : defaultHeaders.keySet()) {
                request.setHeader(header, defaultHeaders.get(header));
            }
            if (additionalParameters != null) {
                HttpParameters httpParams = new HttpParameters();
                httpParams.putAll(additionalParameters, true);
                consumer.setAdditionalParameters(httpParams);



        StringBuffer paramsAsString = new StringBuffer();
        for (int ii = 0; ii < additionalParameters.length; ii += 2) {
            System.out.println("adding: " + additionalParameters[ii] + ", " + additionalParameters[ii +1]);
            if (paramsAsString.length() > 0) {
                paramsAsString.append('&');
            }
            paramsAsString.append(additionalParameters[ii] + '=' + additionalParameters[ii +1]);  // do encoding !!!!  FIXME
            //httpParams.setParameter(kv[0], kv[1]);  // not sure why this is needed also, but it is
        }
                    System.out.println("paramsAsString: " + paramsAsString);

        ByteArrayEntity bae = new ByteArrayEntity(paramsAsString.toString().getBytes());
        bae.setContentType("application/x-www-form-urlencoded");//application/x-www-form-urlencoded
        HttpPost httpPost = (HttpPost) request.unwrap();
        httpPost.setEntity(bae);
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");



            }

//            if (this.listener != null) {
//                this.listener.prepareRequest(request);
//            }

            consumer.sign(request);

//            if (this.listener != null) {
//                this.listener.prepareSubmission(request);
//            }


            org.apache.http.HttpResponse response0 = httpClient.execute((org.apache.http.client.methods.HttpUriRequest) request.unwrap());
            response = new oauth.signpost.commonshttp.HttpResponseAdapter(response0);
                       //oauth/signpost/commonshttp/HttpResponseAdapter
        //            response = sendRequest(request);

        int statusCode = response.getStatusCode();

            boolean requestHandled = false;
//            if (this.listener != null) {
//                requestHandled = this.listener.onResponseReceived(request, response);
//            }
//            if (requestHandled) {
//                return;
//            }

            if (statusCode >= 300) {
                throw new SMArtClientException(">= 300: " + statusCode);//handleUnexpectedResponse(statusCode, response);
            }

            /*HttpParameters*/ responseParams = oauth.signpost.OAuth.decodeForm(response.getContent());

            String token = responseParams.getFirst(oauth.signpost.OAuth.OAUTH_TOKEN);
            String secret = responseParams.getFirst(oauth.signpost.OAuth.OAUTH_TOKEN_SECRET);
            responseParams.remove(oauth.signpost.OAuth.OAUTH_TOKEN);
            responseParams.remove(oauth.signpost.OAuth.OAUTH_TOKEN_SECRET);

           // setResponseParameters(responseParams);

            if (token == null || secret == null) {
                throw new OAuthExpectationFailedException(
                        "Request token or token secret not set in server reply. "
                                + "The service provider you use is probably buggy.");
            }

            consumer.setTokenWithSecret(token, secret);

        //} catch (OAuthNotAuthorizedException e) {
        //    throw e;
        } catch (OAuthExpectationFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new OAuthCommunicationException(e);
        } finally {
            try {
             ////   closeConnection(request, response);
            } catch (Exception e) {
                throw new OAuthCommunicationException(e);
            }
        }

        return responseParams;
    }




    public String[] getAccessToken(String requestToken) throws SMArtClientException {
        AbstractHttpClient httpClient = new DefaultHttpClient();
        CommonsHttpOAuthProvider oprov = new CommonsHttpOAuthProvider(
                requestTokenURL, accessTokenURL, authorizeURL, httpClient);
        CommonsHttpOAuthConsumer oauthConsumer =
                new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
        try {
            // this should mutage oauthConsuer, adding the authorized token/secret
            oprov.retrieveAccessToken(oauthConsumer, "verifier");
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

    private String urlEncode(String toEncode) throws org.smartplatforms.client.SMArtClientException {
        try {
            return java.net.URLEncoder.encode(toEncode,"UTF-8");
        } catch (java.io.UnsupportedEncodingException uee) {
            throw new org.smartplatforms.client.SMArtClientException(uee);
        }
    }




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
       
//        System.out.println("request.getHeader: " + request.getHeader("Content-type"));
//        request.setHeader("Content-type", "application/x-www-form-urlencoded");
//        System.out.println("request.getHeader: " + request.getHeader("Content-type"));
        System.out.println("request www entity: " + request.getClass().getName() + "  " + request.getContentType());
        System.out.println("entity ct: " + bae.getContentType());
    }

    @Override
    public void	prepareSubmission(HttpRequest request) {} //Called after the request has been signed, but before it's being sent.

}