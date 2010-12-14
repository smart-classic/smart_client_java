package org.smartplatforms.client;

import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;

import java.util.Map;
import java.util.HashMap;

import java.net.URL;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.BindingSet;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;

/**
 *
 * @author nate
 */
public class TestServlet extends HttpServlet {
    private static TestServlet instance = null;
    private Utils utils = null;
    private Map<String,String[]> tokensSecrets = new HashMap<String,String[]>();
    private Map<String,String[]> recordsAccess = new HashMap<String,String[]>();
    private SMArtClient smartClient = null;

    private String[][] alrgyArr = {
        new String[] { "ACACIA CONCINNA FRUIT", "S9108H4YLE" },
        new String[] { "ALLIS SHAD", "O78642SOFA" },
        new String[] { "AMYLMETACRESOL", "05W904P57F" },
        new String[] { "ATEVIRDINE", "N24015WC6D" },
        new String[] { "BENTNOSE MACOMA", "KTJ47522L1" },
        new String[] { "BLUE CATFISH", "100052TGN4" },
        new String[] { "BUTYLPHENYL METHYLPROPIONAL", "T7540GJV69" },
        new String[] { "CARYA ILLINOINENSIS SHELL", "0QNC8RS9D2" },
        new String[] { "CHLOROPHENOTHANE", "CIW5S16655" },
        new String[] { "COAGULATION FACTOR VIIA RECOMBINANT HUMAN", "AC71R787OV" },
        new String[] { "CYSTEAMINE", "5UX2SD1KE2" },
        new String[] { "DIHYDROERGOTAMINE MESYLATE", "81AXN7R2QT" },
        new String[] { "ELONGATE HATCHETFISH", "3U3K82M06M" },
        new String[] { "EUROPEAN CONGER", "F0263V59WV" },
        new String[] { "FLUPROSTENOL", "358S7VUE5N" },
        new String[] { "GLYCERYL MONOSTEARATE", "230OU9XXE4" },
        new String[] { "HOMOCHLORCYCLIZINE", "N5MVC31W2N" },
        new String[] { "IODOPHTHALEIN SODIUM", "B4MZ5QGA1M" },
        new String[] { "LAKE TROUT", "6I21OKO9EA" },
        new String[] { "LONGSPINE PORGY", "A5774X1FJT" },
    };

    Map<String,String> alrgyMap = new HashMap<String,String>();
    String alrgySelect = null;

    private String allergyFormA = "<form action=\"http://localhost:8000/SMArtClientJavaOAuthTester"; // + getContextPath()
    private String allergyFormB = "\" method=\"post\">\n" +
"<input type=\"hidden\" name=\"recordId\">";
    private String allergyFormC =
"</input><strong>enter a new allergy for this patient</strong><br/>\n" +
"allergen: " +
"<select name=\"allergy_substance\">\n";
    private String allergyFormD =
"</select>\n" +
"<br/>Category: "  +
"<select name=\"allergy_category\">\n" +
  "<option value=\"food\">food</option>\n" +
  "<option value=\"skin\">skin</option>\n" +
  "<option value=\"seasonal\">seasonal</option>\n" +
"</select>\n" +
"<br/>Severity: " +
"<select name=\"allergy_severity\">\n" +
  "<option value=\"mild\">mild</option>\n" +
  "<option value=\"medium\">medium</option>\n" +
  "<option value=\"severe\">severe</option>\n" +
"</select>\n" +
"<br/>Reaction: <input type=\"textarea\" cols=\"80\" rows=\"5\" name=\"allergy_reaction\"></input>\n" +
"<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"submit\"></submit>\n" +
"</form>\n";





//<th>allergy title</th><th>category</th><th>substance</th><th>severity</th><th>reaction</th>

    @Override
    public void init() throws ServletException {

        StringBuffer alrgySelectB = new StringBuffer();
        for (String[] alrgyPair : alrgyArr) {
            alrgyMap.put(alrgyPair[1], alrgyPair[0]);
            alrgySelectB.append("<option value=\"" + alrgyPair[1] + "\">" + alrgyPair[0] + "</option>\n");
        }
        alrgySelect = alrgySelectB.toString();

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


    private String allergyExampleToSeverity =
"<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
"<rdf:RDF " +
"  xmlns:dcterms=\"http://purl.org/dc/terms/\" " +
"  xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" " +
"  xmlns:sp=\"http://smartplatforms.org/\" " +
"  xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" " +
"  xmlns:allergy=\"http://smartplatforms.org/allergy/\">\n" +
"  <rdf:Description>\n" +
"      <rdf:type rdf:resource=\"http://smartplatforms.org/allergy\"/>\n" +
"      <allergy:severity rdf:resource=\"http://smartplatforms.org/allergy/severity/";
    private String allergyExampleToReaction = "\"/>\n" +
"      <allergy:reaction>";

    private String allergyExampleToCategory = "</allergy:reaction>\n" +
"      <allergy:allergen>\n" +
"      <rdf:Description>\n" +
"        <allergy:category rdf:resource=\"http://smartplatforms.org/allergy/category/";
    private String allergyExampleToSubstance = "\" />\n" +
"        <allergy:substance rdf:resource=\"http://fda.gov/UNII/"; /*QE1QX6B99R*/
    private String allergyExampleToTitle = "\"/>\n" +
"        <dcterms:title>"; /*Peanut*/
    private String allergyExampleToEND = "</dcterms:title>\n" +
"      </rdf:Description>\n" +
"      </allergy:allergen>\n" +
"   </rdf:Description>\n" +
"</rdf:RDF>";
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null) { pathInfo = ""; }
        Map<String,String[]> paramMap = req.getParameterMap();
        System.out.println("pathInfo: " + pathInfo);
        try {
            OutputStream ros = null;
            ros = res.getOutputStream();
            if (pathInfo.equals("/new_allergy")) {    //  /after_auth
                String recordId = paramMap.get("recordId")[0];
                String allergy_substance = paramMap.get("allergy_substance")[0];
                String allergy_category = paramMap.get("allergy_category")[0];
                String allergy_severity = paramMap.get("allergy_severity")[0];
                String allergy_reaction = paramMap.get("allergy_reaction")[0];
                ros.write("<html><head><title>testing SMArtClient working on adding allergy</title></head><body>".getBytes());
                ros.write(("<p>allergy: " + alrgyMap.get(allergy_substance) + "</p>").getBytes());
                ros.write(("<p>allergy substance: " + allergy_substance + "</p>").getBytes());
                ros.write(("<p>allergy category: " + allergy_category + "</p>").getBytes());
                ros.write(("<p>allergy severity: " + allergy_severity + "</p>").getBytes());
                ros.write(("<p>allergy reaction: " + allergy_reaction + "</p>").getBytes());

                Sail memstore = new MemoryStore();
                Repository myRepository = new SailRepository(memstore);
                RepositoryConnection con = null;

                String xmlToAdd = allergyExampleToSeverity + allergy_severity +
                        allergyExampleToReaction + allergy_reaction +
                        allergyExampleToCategory + allergy_category +
                        allergyExampleToSubstance + allergy_substance +
                        allergyExampleToTitle + alrgyMap.get(allergy_substance) +
                        allergyExampleToEND;

                String[] tokenSecret = recordsAccess.get(recordId);

                try {
                    Object response = smartClient.records_X_allergies_POST(recordId, tokenSecret[0], tokenSecret[1],
                            xmlToAdd, "application/rdf+xml", null);

                    System.out.println("allergies add response: " +
                            response.getClass().getName() + "   " + response);
                } catch (SMArtClientException sce) {
                    throw new ServletException(sce);
                }

                /*
                ByteArrayInputStream bais = new ByteArrayInputStream(xmlToAdd.getBytes());

                //Object asdfaasf = (org.openrdf.model.Resource[])
                try {
                    con.add(bais,"", RDFFormat.RDFXML);
                    myRepository.initialize();
                    con = myRepository.getConnection();
                } catch (org.openrdf.repository.RepositoryException rre) {
                    throw new ServletException(rre);
                } catch (org.openrdf.rio.RDFParseException rpe) {
                    throw new ServletException(rpe);
                }*/

                ros.write("</body></html>".getBytes());
            }
            else {
                throw new ServletException("unexpected pathInfo: " + pathInfo);
            }
//        } catch (SMArtClientException sce) {
//            throw new ServletException(sce);
        } catch (IOException ioe) {
            throw new ServletException(ioe);
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
                recordsAccess.put(requestTokenSecret_recordId[1],
                        new String[] { oauthValues[0], oauthValues[1] }
                );

                String allergyreport = getAllergyReport(oauthValues[0], oauthValues[1], requestTokenSecret_recordId[1]);

                ros.write("<html><head><title>testing SMArtClient oauth page-2</title></head><body>".getBytes());
                ros.write(("<span>recordId: " + requestTokenSecret_recordId[1] + "</span><br/>").getBytes());
                ros.write(("<span>access_token: " + oauthValues[0] + "</span><br/>").getBytes());
                ros.write(("<span>access_token_secret: " + oauthValues[1] + "</span>").getBytes());
                ros.write(("<p>" + allergyreport + "</p>").getBytes());
                ros.write((allergyFormA + req.getContextPath() + "/new_allergy" +
                        allergyFormB + requestTokenSecret_recordId[1] +
                        allergyFormC + alrgySelect + allergyFormD).getBytes());
                ros.write("</body></html>".getBytes());
            }
//            else if (pathInfo.equals("/new_allergy")) {    //  /after_auth
//                String allergy_substance = paramMap.get("allergy_substance")[0];
//                String allergy_category = paramMap.get("allergy_category")[0];
//                String allergy_severity = paramMap.get("allergy_severity")[0];
//                String allergy_reaction = paramMap.get("allergy_reaction")[0];
//                ros.write("<html><head><title>testing SMArtClient working on adding allergy</title></head><body>".getBytes());
//                ros.write(("<p>allergy: " + alrgyMap.get(allergy_substance) + "</p>").getBytes());
//                ros.write(("<p>allergy substance: " + allergy_substance + "</p>").getBytes());
//                ros.write(("<p>allergy category: " + allergy_category + "</p>").getBytes());
//                ros.write(("<p>allergy severity: " + allergy_severity + "</p>").getBytes());
//                ros.write(("<p>allergy reaction: " + allergy_reaction + "</p>").getBytes());
//                ros.write("</body></html>".getBytes());
//            }
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
            retVal.append("</table><hr/>");
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
