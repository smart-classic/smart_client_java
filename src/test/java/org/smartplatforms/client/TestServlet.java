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

/**
 *
 * @author nate
 */
public class TestServlet extends HttpServlet {
    private static TestServlet instance = null;
    private Utils utils = null;
    private Map<String,String> tokensSecrets = new HashMap<String,String>();
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
                String requestTokenSecret = tokensSecrets.get(requestToken);
                oauthValues = doDance_step2(requestToken, requestTokenSecret, verifier);
                ros.write("<html><head><title>testing SMArtClient oauth page-2</title></head><body>".getBytes());
                ros.write(("<p>access_token: " + oauthValues[0] + "</p>").getBytes());
                ros.write(("<p>access_token_secret: " + oauthValues[1] + "</p>").getBytes());
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
        tokensSecrets.put(tokenSecret[0], tokenSecret[1]);
        return tokenSecret;
    }

    private String[] doDance_step2(String requestToken, String requestTokenSecret, String verifier)
            throws SMArtClientException {
        String[] tokenSecret = smartClient.getAccessToken(requestToken, requestTokenSecret, verifier);
        return new String[2];
    }
/*

        smartClient.authorizeRequestToken(tokenSecret[0]);
        ///smartClient.getAccessToken(tokenSecret[0]);
        tokensSecrets.put(tokenSecret[0], tokenSecret[1]);

*/

}
