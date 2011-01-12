package org.smartplatforms.client.codegen;

import java.io.IOException;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.BNode;
import org.openrdf.repository.Repository;
//import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.sail.Sail;
//import org.openrdf.sail.memory.model.MemBNode;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.memory.model.MemResource;
import org.openrdf.sail.memory.model.MemValue;
import org.openrdf.sail.memory.model.MemURI;
import org.openrdf.sail.memory.model.MemStatementList;
import org.openrdf.sail.memory.model.MemStatement;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.resultio.TupleQueryResultWriter;
//import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.rio.RDFFormat;

import org.smartplatforms.client.codegen.GenClientUtils;
/**
 * Hello world!
 *
 */
public class GenerateFromSmartOwl {
    private List<String> expectedPredicates = null;
    GenClientUtils genClientUtils = new GenClientUtils();

    /*import java.io.UnsupportedEncodingException;
      import java.net.URLEncoder;*/



    private String startOfDotJava =
"package org.smartplatforms.client;\n\n" +
"import org.openrdf.repository.RepositoryConnection;\n\n" +
"import java.io.InputStream;\n" +
"import java.io.IOException;\n\n" +
"import java.util.Map;\n" +
"import java.util.HashMap;\n" +
"import java.util.List;\n" +
"import java.util.ArrayList;\n" +
"import javax.servlet.http.HttpServletRequest;\n" +
"import org.apache.http.client.methods.HttpUriRequest;\n\n" +
"import org.apache.http.impl.client.DefaultHttpClient;\n" +
"import org.apache.http.impl.client.AbstractHttpClient;\n" +
"import org.apache.http.client.methods.HttpPost;\n" +
"import org.apache.http.client.methods.HttpGet;\n" +
"import org.apache.http.params.HttpParams;\n" +
"import org.apache.http.entity.ByteArrayEntity;\n" +
"import org.apache.http.StatusLine;\n" +
"import org.apache.http.HttpEntity;\n\n" +
"import org.openrdf.repository.RepositoryConnection;\n\n" +
"import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;\n" +
"import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;\n" +
"import oauth.signpost.http.HttpRequest;\n" +
"import oauth.signpost.http.HttpResponse;\n" +
"import oauth.signpost.http.HttpParameters;\n" +
"import oauth.signpost.exception.OAuthCommunicationException;\n" +
"import oauth.signpost.exception.OAuthExpectationFailedException;\n" +
"import oauth.signpost.exception.OAuthMessageSignerException;\n" +
"import oauth.signpost.exception.OAuthNotAuthorizedException;\n" +
"import oauth.signpost.OAuthProviderListener;\n\n" +
"public class SMArtClient {\n\n" +
"    private String consumerKey = null;\n" +
"    private String consumerSecret = null;\n" +
"    private String requestTokenURL = null;\n" +
"    private String accessTokenURL = null;\n" +
"    private String oauthCallback = null;\n" +
"    private String authorizeURL = null;\n" +
"    private Utils smartUtils = null;\n" +
"    int httpTimeout = 30000;\n\n" +
"    public SMArtClient(String consumerKey, String consumerSecret, String baseURL)\n" +
"            throws SMArtClientException {\n" +
"        this.consumerKey = consumerKey;\n" +
"        this.consumerSecret = consumerSecret;\n" +
"        smartUtils = new Utils(consumerKey, consumerSecret, baseURL,\n" +
"                new DefaultResponseTypeConversion(), httpTimeout);\n" +
"    }\n\n" +
"    /**\n" +
"    for example.\n" +
"    'api_base' :          'http://sandbox-api.smartplatforms.org',\n" +
"    'request_token_url' : 'http://sandbox-api.smartplatforms.org/oauth/request_token',\n" +
"    'authorize_url':      'http://sandbox.smartplatforms.org/oauth/authorize',\n" +
"    'access_token_url':   'http://sandbox-api.smartplatforms.org/oauth/access_token',\n" +
"    'oauth_callback' :    'oob'\n" +
"    */\n" +
"    public SMArtClient(String consumerKey, String consumerSecret, String baseURL,\n" +
"            String requestTokenURL, String accessTokenURL, String authorizeURL, String oauthCallback)\n" +
"            throws SMArtClientException {\n" +
"        this.consumerKey = consumerKey;\n" +
"        this.consumerSecret = consumerSecret;\n" +
"        this.requestTokenURL = requestTokenURL;\n" +
"        this.authorizeURL = authorizeURL; // this param not actually used, because authorize address is in the response to requesTokenURL\n" +
"        this.accessTokenURL = accessTokenURL;\n" +
"        this.oauthCallback = oauthCallback;\n" +
"        smartUtils = new Utils(consumerKey, consumerSecret, baseURL,\n" +
"                new DefaultResponseTypeConversion(), httpTimeout);\n" +
"    }\n"+
"    public void setAccessToken(HttpServletRequest r){\n"+
"    	this.smartUtils.setAccessToken(r);\n"+
"    }\n"+
"    \n"+
"    public void setAccessToken(String token, String secret){\n"+
"    	this.smartUtils.setAccessToken(token, secret);\n"+
"    }\n";




    private String endOfClassSource =
"    /** get request token\n" +
"     * @return [token, secret, redirectURL]\n" +
"    */\n" +            
"    public String[] getRequestToken(String recordId) throws SMArtClientException {\n" +
"        if (requestTokenURL == null) {\n" +
"            throw new SMArtClientException(\"used constructor without requestTokenURL\");\n" +
"        }\n" +
"        AbstractHttpClient httpClient = new DefaultHttpClient();\n" +
"        CommonsHttpOAuthProvider oprov = new CommonsHttpOAuthProvider(\n" +
"                requestTokenURL, accessTokenURL, authorizeURL, httpClient);\n" +
"        oprov.setRequestHeader(\"Content-type\", \"application/x-www-form-urlencoded\");\n" +
"        CommonsHttpOAuthConsumer oauthConsumer =\n" +
"                new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);\n\n" +
"        String urlWithRequestToken = null;\n" +
"        String[] tokenSecret = null;\n\n" +
"        try {\n" +
"            String oldDebug = System.getProperty(\"debug\");\n" +
"            System.setProperty(\"debug\", \"true\");\n\n" +
"            List<String[]> additionalParams = new ArrayList<String[]>();\n" +
"            additionalParams.add(new String[] { \"offline\", \"true\" } );\n" +
"            additionalParams.add(new String[] { \"record_id\", recordId /*\"2000000008\"*/ } );\n" +
"            oprov.setListener(new OAuthProviderListenerForSMArt(additionalParams));\n" +
"            urlWithRequestToken =  oprov.retrieveRequestToken(oauthConsumer, oauthCallback);// TODO FIXME oprov.retrieveRequestTokenAdditionalParameters( oauthConsumer, oauthCallback, \"offline\", \"true\", \"record_id\", recordId);\n" +
"            System.out.println(\"consumer token/secret: \" + oauthConsumer.getToken() + '/' + oauthConsumer.getTokenSecret());\n" +
"            tokenSecret = new String[] {\n" +
"                oauthConsumer.getToken() , oauthConsumer.getTokenSecret(), urlWithRequestToken\n" +
"            };\n\n" +
"            if (oldDebug == null) { System.clearProperty(\"debug\"); }\n" +
"            else { System.setProperty(\"debug\", oldDebug); }\n" +
"        } catch (oauth.signpost.exception.OAuthMessageSignerException mse) {\n" +
"            throw new SMArtClientException(mse);\n" +
"        } catch (oauth.signpost.exception.OAuthNotAuthorizedException nae) {\n" +
"            throw new SMArtClientException(nae);\n" +
"        } catch (oauth.signpost.exception.OAuthExpectationFailedException efe) {\n" +
"            throw new SMArtClientException(efe);\n" +
"        } catch (oauth.signpost.exception.OAuthCommunicationException comE) {\n" +
"            throw new SMArtClientException(comE);\n" +
"        }\n" +
"System.out.println(\"requestToken: \" + urlWithRequestToken);\n" +
"        return tokenSecret; //urlWithRequestToken;\n" +
"    }\n\n" +
"    public String[] getAccessToken(String requestToken, String requestTokenSecret, String verifier) throws SMArtClientException {\n" +
"        AbstractHttpClient httpClient = new DefaultHttpClient();\n" +
"        CommonsHttpOAuthProvider oprov = new CommonsHttpOAuthProvider(\n" +
"                requestTokenURL, accessTokenURL, authorizeURL, httpClient);\n" +
"        CommonsHttpOAuthConsumer oauthConsumer =\n" +
"                new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);\n" +
"        System.out.println(\"in getAccessToken: \" + accessTokenURL + \" -- \" +\n" +
"                consumerKey + \", \" + consumerSecret + \", \" +\n" +
"                requestToken + \", \" + requestTokenSecret + \", \" + verifier);\n" +
"        oauthConsumer.setTokenWithSecret(requestToken, requestTokenSecret);\n" +
"        try {\n" +
"            // this should mutate oauthConsuer, adding the authorized token/secret\n" +
"            String oldDebug = System.getProperty(\"debug\");\n" +
"            System.setProperty(\"debug\", \"true\");\n\n" +
"            oprov.retrieveAccessToken(oauthConsumer, verifier);\n\n" +
"            if (oldDebug == null) { System.clearProperty(\"debug\"); }\n" +
"            else { System.setProperty(\"debug\", oldDebug); }\n" +
"        } catch (oauth.signpost.exception.OAuthMessageSignerException mse) {\n" +
"            throw new SMArtClientException(mse);\n" +
"        } catch (oauth.signpost.exception.OAuthNotAuthorizedException nae) {\n" +
"            throw new SMArtClientException(nae);\n" +
"        } catch (oauth.signpost.exception.OAuthExpectationFailedException efe) {\n" +
"            throw new SMArtClientException(efe);\n" +
"        } catch (oauth.signpost.exception.OAuthCommunicationException comE) {\n" +
"            throw new SMArtClientException(comE);\n" +
"        }\n" +
"        String[] retVal = new String[2];\n" +
"        retVal[0] = oauthConsumer.getToken();\n" +
"        retVal[1] = oauthConsumer.getTokenSecret();\n" +
"        if (retVal[0] == null || retVal[1] == null) {\n" +
"            throw new SMArtClientException(\"retrieveAccessToken returned null token and/or secret: \" + retVal[0] + \"   \" + retVal[1]);\n" +
"        }\n" +
"        return retVal;\n" +
"    }\n\n" +
"    public String[] getAccessToken_GET(String requestToken, String requestTokenSecret, String verifier)\n" +
"            throws SMArtClientException {\n" +
"        AbstractHttpClient httpClient = new DefaultHttpClient();\n" +
"        CommonsHttpOAuthConsumer oauthConsumer =\n" +
"                new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);\n" +
"        System.out.println(\"in getAccessToken_GET: \" + accessTokenURL + \" -- \" +\n" +
"                consumerKey + \", \" + consumerSecret + \", \" +\n" +
"                requestToken + \", \" + requestTokenSecret + \", \" + verifier);\n" +
"        HttpParameters addParams = new HttpParameters();\n" +
"        addParams.put(\"oauth_verifier\", verifier, true);\n" +
"        oauthConsumer.setAdditionalParameters(addParams);\n" +
"        oauthConsumer.setTokenWithSecret(requestToken, requestTokenSecret);\n" +
"        String srr = null;\n" +
"        try {\n" +
"            // this should mutate oauthConsuer, adding the authorized token/secret\n" +
"            String oldDebug = System.getProperty(\"debug\");\n" +
"            System.setProperty(\"debug\", \"true\");\n\n" +
"            HttpUriRequest hcRequest = new HttpGet(accessTokenURL);\n" +
"            oauthConsumer.sign(hcRequest);\n" +
"            Map<String,Object> options = new HashMap<String,Object>();\n" +
"            org.apache.http.HttpResponse httpResponse =\n" +
"                    smartUtils.smartExecute(hcRequest, options);\n" +
"            srr = (String) smartUtils.smartRequestResponse(httpResponse, null, \"GET \" + accessTokenURL, options);\n\n" +
"            System.out.println(\"access token response: \" + srr.getClass().getName()  + \": \" + srr);\n\n" +
"            if (oldDebug == null) { System.clearProperty(\"debug\"); }\n" +
"            else { System.setProperty(\"debug\", oldDebug); }\n" +
"        } catch (oauth.signpost.exception.OAuthMessageSignerException mse) {\n" +
"            throw new SMArtClientException(mse);\n" +
"        } catch (oauth.signpost.exception.OAuthExpectationFailedException efe) {\n" +
"            throw new SMArtClientException(efe);\n" +
"        } catch (oauth.signpost.exception.OAuthCommunicationException comE) {\n" +
"            throw new SMArtClientException(comE);\n" +
"        }\n" +
"        String[] retVal = new String[2];\n" +
"        String[] srrA = srr.split(\"&\");\n" +
"        for (int ii = 0; ii < srrA.length; ii++) {\n" +
"            String[] srrAA = srrA[ii].split(\"=\");\n" +
"            if (srrAA.length != 2) {\n" +
"                throw new SMArtClientException(\"unexpected response from getAccessToken: \" + srr);\n" +
"            }\n" +
"            if (srrAA[0].equals(\"ouath_token\")) {\n" +
"                retVal[0] = srrAA[1];\n" +
"            } else if (srrAA[0].equals(\"oauth_token_secret\")) {\n" +
"                retVal[1] = srrAA[1];\n" +
"            }\n" +
"        }\n" +
"//        retVal[0] = oauthConsumer.getToken();\n" +
"//        retVal[1] = oauthConsumer.getTokenSecret();\n" +
"        if (retVal[0] == null || retVal[1] == null) {\n" +
"            throw new SMArtClientException(\"retrieveAccessToken did not return token and/or secret: \" + srr);\n" +
"        }\n" +
"        return retVal;\n" +
"    }\n\n" +
"    private String prepareQueryString(String... pairs) throws SMArtClientException {\n" +
"        if (pairs.length % 2 != 0) {\n" +
"            throw new SMArtClientException(\"not an even number of query param values, should be two for each name=value pair\");\n" +
"        }\n\n" +
"        StringBuffer retVal = new StringBuffer();\n" +
"        if (pairs.length > 0) { retVal.append(\"?\"); }\n" +
"        for (int ii = 0; ii < pairs.length; ii += 2) {\n" +
"            String firstOfPair = pairs[ii];\n" +
"            String secondOfPair = pairs[ii +1];\n" +
"            if (ii > 0) { retVal.append('&'); }\n" +
"            retVal.append(urlEncode(firstOfPair));\n" +
"            retVal.append('=');\n" +
"            retVal.append(urlEncode(secondOfPair));\n" +
"        }\n" +
"        return retVal.toString();\n" +
"    }\n\n" +
"class OAuthProviderListenerForSMArt implements OAuthProviderListener {\n" +
"    List<String[]> additionalParams = null;\n\n" +
"    OAuthProviderListenerForSMArt(List<String[]> additionalParams) {\n" +
"        this.additionalParams = additionalParams;\n" +
"    }\n\n" +
"    // implement OAuthProviderListener\n" +
"    @Override\n" +
"    public boolean onResponseReceived(HttpRequest request, HttpResponse response) {\n" +
"        return false; // not handled here, use default handling\n" +
"    }\n\n" +
"    @Override\n" +
"    public void prepareRequest(HttpRequest request) throws SMArtClientException {\n" +
"        System.out.println(\"request: \" + request.getClass().getName() + \"  \" + request.getContentType());\n" +
"        Object uwr = request.unwrap();\n" +
"        System.out.println(\"UUU: \" + uwr.getClass().getName());\n" +
"        if (! (uwr instanceof HttpPost)) {\n" +
"            throw new SMArtClientException(\n" +
"                    \"OAuthProvider request was expected to be an instance of HttpPost: \" + uwr.getClass().getName());\n" +
"        }\n" +
"        HttpPost httpPost = (HttpPost) uwr;\n\n" +
"        HttpParams httpParams = httpPost.getParams();\n\n" +
"        StringBuffer paramsAsString = new StringBuffer();\n" +
"        for (String[] kv : additionalParams) {\n" +
"            System.out.println(\"adding: \" + kv[0] + \", \" + kv[1]);\n" +
"            if (paramsAsString.length() > 0) {\n" +
"                paramsAsString.append('&');\n" +
"            }\n" +
"            paramsAsString.append(kv[0] + '=' + kv[1]);  // do encoding !!!!  FIXME\n" +
"            //httpParams.setParameter(kv[0], kv[1]);  // not sure why this is needed also, but it is\n" +
"        }\n\n" +
"        System.out.println(\"paramsAsString: \" + paramsAsString);\n\n" +
"        ByteArrayEntity bae = new ByteArrayEntity(paramsAsString.toString().getBytes());\n" +
"        bae.setContentType(\"application/x-www-form-urlencoded\");//application/x-www-form-urlencoded\n" +
"        httpPost.setEntity(bae);\n" +
"        httpPost.setHeader(\"Content-type\", \"application/x-www-form-urlencoded\");\n" +
"        System.out.println(\"request www entity: \" + request.getClass().getName() + \"  \" + request.getContentType());\n" +
"        System.out.println(\"entity ct: \" + bae.getContentType());\n" +
"    }\n\n" +
"    @Override\n" +
"    public void	prepareSubmission(HttpRequest request) {} //Called after the request has been signed, but before it's being sent.\n" +
"}\n\n" +
"    private String urlEncode(String toEncode) throws org.smartplatforms.client.SMArtClientException {\n" +
"        try {\n" +
"            return java.net.URLEncoder.encode(toEncode,\"UTF-8\");\n" +
"        } catch (java.io.UnsupportedEncodingException uee) {\n" +
"            throw new org.smartplatforms.client.SMArtClientException(uee);\n" +
"        }\n" +
"    }\n";


    private GenerateFromSmartOwl() {
        expectedPredicates = Arrays.asList("description", "method", "path", "target", "by_internal_id", "category", "above");
    }

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        GenerateFromSmartOwl instance = new GenerateFromSmartOwl();
        instance.dowork();
        System.out.println( "'later World! ============" );
    }


    /*org.openrdf.repository.RepositoryConnection*/
    private void dowork() {
        try {
            
            OutputStream fos = new FileOutputStream("SMArtClient.java");
            fos.write(startOfDotJava.getBytes());
            dowork0(fos);
            fos.write(endOfClassSource.getBytes());
            fos.write("}\n".getBytes());
            fos.close();
        } catch (org.openrdf.OpenRDFException repE) {
            throw new RuntimeException(repE);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private void dowork0(OutputStream fos) throws org.openrdf.OpenRDFException, IOException {

        Sail memstore = new MemoryStore();
        Repository myRepository = new SailRepository(memstore);
        myRepository.initialize();

        RepositoryConnection con = myRepository.getConnection();

        File apiOwlFile = new File("/home/jmandel/smart/smart_server/smart/document_processing/schema/smart.owl");
        con.add(apiOwlFile, null, RDFFormat.RDFXML, new Resource[0]);

        String myfirstsparql =
            "PREFIX api: <http://smartplatforms.org/api/> \n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
            "SELECT ?bs WHERE { ?bs <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://smartplatforms.org/api/call>. }";
        TupleQuery tq = con.prepareTupleQuery(QueryLanguage.SPARQL, myfirstsparql);
        TupleQueryResult tqr = tq.evaluate();

        // for each api:call
        String methodPrefix = "    public Object ";
        String throwsClause = "throws org.smartplatforms.client.SMArtClientException {";
        while (tqr.hasNext()) {
            BindingSet bindSet = tqr.next();  // get next api:call
            Value subject = bindSet.getValue("bs");
            if ( !(subject instanceof /*MemResource*/ BNode)) {
                throw new RuntimeException("expected BNode: " + subject.getClass().getName());
            }

            MemResource mres = (MemResource) subject;
            MemStatementList msl = mres.getSubjectStatementList();
            System.out.println("\n\n\n");
            Map<String,String> callPO = new HashMap<String,String>();
            for (int ii = 0; ii < msl.size(); ii++) {
                MemStatement msta = msl.get(ii);
                BNode stmntSubject = (BNode) msta.getSubject();
                System.out.println("statement: " +
                        stmntSubject.getClass().getName() + " " +
                        "   " + msta.getPredicate().getNamespace() + " : " + msta.getPredicate().getLocalName() +
                        "   " + msta.getObject().stringValue() );
                addToMap(callPO, msta);
            }

            boolean putOrPost = false;
            if (callPO.get("method").equals("PUT") || callPO.get("method").equals("POST")) {
                putOrPost = true;
            }

            // now we have a map, callPO, of the significant predicate/object pairs
            //"description", "method", "path", "target", "by_internal_id", "category"
            String path0 = callPO.get("path");
            String path = path0;
            int qix = path0.indexOf('?');
            String query = null;
            if (qix > 0) {
                path = path0.substring(0, qix);
                query = path0.substring(qix +1);
            }
            String[] tokenListA = path.split("/");
            boolean endsWithSlash = path.charAt(path.length() -1) == '/';
            List<String> tokenList = Arrays.asList(tokenListA);
            StringBuffer javaDoc = new StringBuffer("    /** " + callPO.get("description") + " -- " + path0);
            StringBuffer methSig = new StringBuffer();
            StringBuffer requestURL_SB = new StringBuffer();
            int[] stateA = new int[] { GenClientUtils.STATE_START };
            List<String> paramTypedList = new ArrayList<String>();

            genClientUtils.tokensToSig(
                    tokenList,
                    methSig,
                    paramTypedList,
                    javaDoc,
                    requestURL_SB,
                    path,
                    stateA
            );
            int state = stateA[0];
            if (endsWithSlash) {
                methSig.append('_');
                if (state == GenClientUtils.STATE_CONSTANT) {
                    requestURL_SB.append("/\"");
                } else {
                    requestURL_SB.append(" + \"/\"");
                }
            } else {
                if (state == GenClientUtils.STATE_CONSTANT) {
                    requestURL_SB.append('"');
                }
            }

            methSig.append(callPO.get("method") + "(");
            for (int ii = 0; ii < paramTypedList.size(); ii++) {
                if (ii > 0) {
                    methSig.append(", ");
                }
                methSig.append(paramTypedList.get(ii));
            }

            if (query != null) {
                String[] queryA = query.split("&");
                for (int ii = 0; ii < queryA.length; ii++) {
                    String aparam = queryA[ii];
                    String[] aparamA = aparam.split("=");
                    if (aparamA.length != 2) {
                        throw new RuntimeException("param without '=': " + query);
                    }
                    javaDoc.append("\n    * @param " + aparamA[0] + ' ' + aparamA[1]);
                    if (ii == 0) { requestURL_SB.append(" + prepareQueryString("); }
                    else { requestURL_SB.append(", "); }
                    requestURL_SB.append("\"" + aparamA[0] + "\", " + aparamA[0]);
//                    if (ii == 0) { requestURL_SB.append(" + '?' "); }
//                    else { requestURL_SB.append(" + '&' "); }
//
//                    requestURL_SB.append(" + \"" + aparamA[0] + "=\" + urlEncode(" + aparamA[0] + ")");
//
                    if (ii > 0 || paramTypedList.size() > 0) { methSig.append(", "); }
                    methSig.append("String " + aparamA[0]);
                }
                requestURL_SB.append(")");
            }

            if (query != null || paramTypedList.size() > 0) {
                methSig.append(",");
            }
            javaDoc.append("\n    * @param accessToken OAuth access token");
            javaDoc.append("\n    * @param accessTokenSecret OAuth access token secret");
            if (putOrPost) {
                javaDoc.append("\n    * @param requestBody data to send as request body");
                javaDoc.append("\n    * @param requestBodyContentType typically application/x-www-form-urlencoded");
            }
            javaDoc.append("\n    * @param options see class note");

            methSig.append("\n            String accessToken, String accessTokenSecret, ");
            if (putOrPost) {
                methSig.append("String requestBody, String requestContentType, ");
            }
            methSig.append("Map<String,Object> options)");

            fos.write((javaDoc + "\n    */\n").getBytes());
            fos.write((methodPrefix + methSig).getBytes());
            if ((methodPrefix.length() + methSig.length() + throwsClause.length()) > 100) {
                fos.write("\n           ".getBytes());
            }
            fos.write((" " + throwsClause + "\n").getBytes());
            fos.write(("        String restURL = " + requestURL_SB + ";\n").getBytes());
            fos.write(("        return smartUtils.smartRequest(\"" + callPO.get("method") + "\", ").getBytes());
              fos.write(("restURL, null, accessToken, accessTokenSecret, ").getBytes());
              if (putOrPost) {
                  fos.write(("requestBody, requestContentType, ").getBytes());
              } else {
                  fos.write(("null, null, ").getBytes());
              }
              fos.write(("null, options);\n    }\n\n").getBytes());
            System.out.println("stateA: " + stateA[0]);
        }
    }

    private void addToMap(Map<String,String> callPO, MemStatement msta) {
        MemURI pUri = msta.getPredicate();
        MemValue object = msta.getObject();
        String pathBase = "http://smartplatforms.org/";

        if (pUri.getNamespace().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#") && pUri.getLocalName().equals("type")) {
            // we know the type, ignore
        } else if (! pUri.getNamespace().equals("http://smartplatforms.org/api/")) {
            throw new RuntimeException("unexpected predicate namespace: " + pUri.stringValue());
        } else if (! expectedPredicates.contains(pUri.getLocalName())) {
            throw new RuntimeException("unexpected predicate localName: " + pUri.stringValue());
        } else if (pUri.getLocalName().equals("path")) {
            String path = object.stringValue();
            if (! path.startsWith(pathBase)) {
                throw new RuntimeException(
                        "expected path to start with ''" + pathBase + "'': " + path);
            }
            callPO.put("path", path.substring(pathBase.length()));
        } else {
            callPO.put(pUri.getLocalName(), object.stringValue());
        }
    }    
}


/////////////////////////////



