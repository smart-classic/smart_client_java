package org.smartplatforms.client;

import java.io.OutputStream;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;

import java.net.URL;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.BindingSet;
import org.openrdf.repository.RepositoryConnection;

/**
 *
 * @author nate
 */
public class TestServlet extends HttpServlet {
    private static TestServlet instance = null;
    private Utils utils = null;
    private Map<String,String[]> tokensSecrets = new HashMap<String,String[]>();
    private SMArtClient smartClient = null;

    @Override
    public void init() throws ServletException {

        try {
            String consumerKey = getInitParameter("consumerKey");
            String consumerSecret = getInitParameter("consumerSecret");
            String serverBaseURL =  getInitParameter("serverBaseURL"); // "http://sandbox-api.smartplatforms.org/"
            if (serverBaseURL.endsWith("/")) {
                serverBaseURL = serverBaseURL.substring(0, serverBaseURL.length() -1);
            }
            String requestTokenURL = getInitParameter("requestTokenURL");
            String authorizeURL = getInitParameter("authorizeURL");
            String accessTokenURL = getInitParameter("accessTokenURL");
            String oauthCallback = getInitParameter("oauthCallback");
            utils = new Utils(consumerKey, consumerSecret,
                  serverBaseURL, new DefaultResponseTypeConversion(), 30000);

            
            smartClient = new SMArtClient(consumerKey, consumerSecret, serverBaseURL,
                       serverBaseURL + '/' + requestTokenURL,
                       serverBaseURL + '/' + accessTokenURL,
                       authorizeURL, oauthCallback);

        } catch (SMArtClientException sce) {
            throw new ServletException(sce);
        }

    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        // http://localhost:8000/index.html?record_id=2000000000
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) { pathInfo = ""; }
        Map<String,String[]> paramMap = req.getParameterMap();
        String[] oauthValues = null;

        System.out.println("pathInfo: " + pathInfo);
        try {
            OutputStream ros = null;
            ros = res.getOutputStream();
            if (pathInfo.equals("/index.html")) {
                String recordId = paramMap.get("record_id")[0];
                oauthValues = doDance_step1(recordId);
                System.out.println("in doGet index.html: " + oauthValues[0] + ", " + oauthValues[1] + ", " + oauthValues[2]);
                res.sendRedirect(oauthValues[2]);

//                ros.write("<html><head><title>testing SMArtClient oauth</title></head><body>".getBytes());
//                ros.write(("<p>request_token: " + oauthValues[0] + "</p>").getBytes());
//                ros.write(("<p>request_secret: " + oauthValues[1] + "</p>").getBytes());
//                ros.write(("<p>authorize URL: " + oauthValues[2] + "</p>").getBytes());
//                ros.write("</body></html>".getBytes());
            }
            else if (pathInfo.equals("/after_auth.html")) {    //  /after_auth
                // after_auth.html?oauth_token=FmfW4tnzaaAZgY7I4Yle&oauth_verifier=UZZ3zBlbiIjZ9S4TQkyA
                String requestToken = paramMap.get("oauth_token")[0];
                String verifier = paramMap.get("oauth_verifier")[0];
                String[] requestTokenSecret_recordId = tokensSecrets.get(requestToken);
                oauthValues = doDance_step2(requestToken, requestTokenSecret_recordId[0], verifier);

                String allergyreport = getAllergyReport(oauthValues[0], oauthValues[1], requestTokenSecret_recordId[1]);

                ros.write("<html><head><title>testing SMArtClient oauth page-2</title></head><body>".getBytes());
                ros.write(("<p>access_token: " + oauthValues[0] + "</p>").getBytes());
                ros.write(("<p>access_token_secret: " + oauthValues[1] + "</p>").getBytes());
                ros.write(("<p>" + allergyreport + "</p>").getBytes());
                ros.write("</body></html>".getBytes());
            }
            else {
                throw new ServletException("unexpected pathInfo: " + pathInfo);
            }
        } catch (SMArtClientException sce) {
            throw new ServletException(sce);
        } catch (IOException ioe) {
            throw new ServletException(ioe);
        }

    }

    private String[] doDance_step1(String recordId) throws SMArtClientException {
        String[] tokenSecret = smartClient.getRequestToken(recordId);
        //http://sandbox.smartplatforms.org/oauth/authorize?oauth_token=OYJrYRzxAjRmYXgSyfko&oauth_callback=oob
        //will need the secret associated with this token later, after_auth
        tokensSecrets.put(tokenSecret[0], new String[] { tokenSecret[1], recordId });
        return tokenSecret;
    }

    private String[] doDance_step2(String requestToken, String requestTokenSecret, String verifier)
            throws SMArtClientException {
        String[] tokenSecret = smartClient.getAccessToken_GET(requestToken, requestTokenSecret, verifier);
        return tokenSecret;
    }

    private String alrgyQ =
"PREFIX api: <http://smartplatforms.org/api/> \n" +
"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
"PREFIX alrgy: <http://smartplatforms.org/allergy/> \n" +
"PREFIX dc: <http://purl.org/dc/terms/> \n" +
"SELECT  ?gtitle ?gcategory ?gsubstance ?severity ?reaction WHERE { " +
                "?alrgy <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://smartplatforms.org/allergy>.\n" +
                "?alrgy alrgy:severity ?severity.\n" +
                "?alrgy alrgy:reaction ?reaction.\n" +
                "?alrgy alrgy:allergen ?allergen.\n" +
                "?allergen alrgy:category ?gcategory.\n" +
                "?allergen alrgy:substance ?gsubstance.\n" +
                "?allergen dc:title ?gtitle.\n" +
          "}";

    private String getAllergyReport(String token, String secret, String recordId) throws SMArtClientException {
        StringBuffer retVal = new StringBuffer(
                "<table><tr><th>allergy title</th><th>category</th><th>substance</th><th>severity</th><th>reaction</th></tr>\n");

        RepositoryConnection repC = null;
        Object resultObj = smartClient.records_X_allergies_GET(recordId, token, secret, null);
        if (resultObj instanceof RepositoryConnection) {
            repC = (RepositoryConnection) resultObj;
        } else {
            System.out.println("result of records_X_allergies_GET: " +
                    resultObj.getClass().getName() + "    " + resultObj);
            System.out.println(((String[])resultObj)[0] + ",  " + ((String[])resultObj)[1]);
        }

        try {
            TupleQuery tq = repC.prepareTupleQuery(QueryLanguage.SPARQL, alrgyQ);
            TupleQueryResult tqr = tq.evaluate();
            while (tqr.hasNext()) {
                BindingSet bns = tqr.next();
                String title = bns.getValue("?gtitle").stringValue();
                String category = bns.getValue("?gcategory").stringValue();
                String substance = bns.getValue("?gsubstance").stringValue();
                String severity = bns.getValue("?gseverity").stringValue();
                String reaction = bns.getValue("?greaction").stringValue();

                retVal.append("<tr><td>" + title +
                        "</td><td>" + category +
                        "</td><td>" + substance +
                        "</td><td>" + severity +
                        "</td><td>" + reaction + "</td></tr>\n");
            }
            retVal.append("</table>");
        } catch (org.openrdf.repository.RepositoryException rex) {
            throw new SMArtClientException(rex);
        } catch (org.openrdf.query.MalformedQueryException mqx) {
            throw new SMArtClientException(mqx);
        } catch (org.openrdf.query.QueryEvaluationException qvx) {
            throw new SMArtClientException(qvx);
        }

        return retVal.toString();
    }
/*

        smartClient.authorizeRequestToken(tokenSecret[0]);
        ///smartClient.getAccessToken(tokenSecret[0]);
        tokensSecrets.put(tokenSecret[0], tokenSecret[1]);

*/

}
