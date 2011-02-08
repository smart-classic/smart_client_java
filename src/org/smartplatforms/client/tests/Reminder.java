/**
 * Example SMArt REST Application: Parses OAuth tokens from
 * browser-supplied cookie, then provides a list of which prescriptions
 * will need to be refilled soon (based on dispense quantity + date, and
 * the unrealistic simplifying assumption that one pill per day is
 * consumed).
 *
 * Josh Mandel
 * Children's Hospital Boston, 2010
 *
 * Translated from python to Java by Nate Finstein
 */


package org.smartplatforms.client.tests;

import java.io.OutputStream;
import java.io.IOException;

import java.util.GregorianCalendar;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import org.openrdf.query.QueryLanguage;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.BindingSet;

import org.smartplatforms.client.SMArtClient;
import org.smartplatforms.client.SMArtClientException;
//import org.smartplatforms.client.SMArtClientExceptionHttp404;
import org.smartplatforms.client.TokenSecret;

public class Reminder extends HttpServlet {
    private String bootstrapPage =
"<!DOCTYPE html><html>" +
"<head><script src=\"http://sample-apps.smartplatforms.org/framework/smart/scripts/smart-api-client.js\"></script></head>" +
"<body></body></html>";

    String reminderHeader = "<!DOCTYPE html>\n<html><head><title>java generated</title></head>\n<body>\n";
    String reminderFooter = "</body></html>";

    private DatatypeFactory dtf = null;

    private String sparqlForReminders =
        "PREFIX dc:<http://purl.org/dc/elements/1.1/>\n" +
        "PREFIX dcterms:<http://purl.org/dc/terms/>\n" +
        "PREFIX sp:<http://smartplatforms.org/terms#>\n" +
        "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
        "   SELECT  ?med ?name ?quant ?when\n" +
        "   WHERE {\n" +
        "          ?med rdf:type sp:Medication .\n" +
        "          ?med sp:drugName ?medc.\n" +
        "          ?medc dcterms:title ?name.\n" +
        "          ?med sp:fulfillment ?fill.\n" +
        "          ?fill sp:dispenseQuantity ?quant.\n" +
        "          ?fill dc:date ?when.\n" +
        "   }";


    @Override
    public void init() throws ServletException {
        System.out.println("in init() for Reminder");
        try {
            dtf = DatatypeFactory.newInstance();
        } catch (javax.xml.datatype.DatatypeConfigurationException dce) {
            throw new ServletException(dce);
        }
    }


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        System.out.println("in doGet() for Reminder  --  " + req.getPathInfo());
        String pathInfo = req.getPathInfo();
        if (pathInfo.equals("/bootstrap.html")) {
            try {
                OutputStream ros = res.getOutputStream();
                ros.write(bootstrapPage.getBytes());
                ros.close();
                System.out.println("aaaa");
            } catch (IOException ioe) {
                throw new ServletException(ioe);
            }
        } else if (pathInfo.equals("/index.html")) {
            presentReminders(req, res);
        }
    }


    private void presentReminders(HttpServletRequest req, HttpServletResponse res) throws ServletException {
        try {
            OutputStream resOut = res.getOutputStream();
            resOut.write(reminderHeader.getBytes());
        } catch (IOException ioe) {
            throw new ServletException(ioe);
        }
        
        String authHeader = req.getHeader("Authorization");
        System.out.println("Authorization: " + authHeader);
        Map<String,String[]> reqParams = req.getParameterMap();
        Iterator<String> paramKeys = reqParams.keySet().iterator();
        StringBuffer rpStrb = new StringBuffer();
        while(paramKeys.hasNext()) {
            String paramKey = paramKeys.next();
            String[] paramValues = reqParams.get(paramKey);
            if (rpStrb.length() > 0) { rpStrb.append(", "); }
            rpStrb.append(paramKey + ":");
            for (String paramValue : paramValues) {
                rpStrb.append(paramValue + "   ");
            }
        }
        System.out.println("request params: " + rpStrb);

        String recordId = null;
        String[] cookie_name = reqParams.get("cookie_name");
        if (cookie_name.length != 1) {
            throw new ServletException(
                    "unexpected number of cookie_name param values: " + cookie_name.length);
        }
        Cookie[] cookies = req.getCookies();
        System.out.println("cookies.length: " + cookies.length);
        for (Cookie cookie : cookies) {
            System.out.println("cookie: " + cookie.getName() + "  --  " + cookie.getValue());
            if (cookie.getName().equals(cookie_name[0])) {
                System.out.println(cookie.getValue());
                Map<String,String> aoMap = mapFromAuthHeader(cookie.getValue());
                recordId = aoMap.get("smart_record_id");
            }
        }

        oauth.signpost.http.HttpParameters authParams = oauth.signpost.OAuth.oauthHeaderToParamsMap(authHeader);
        //String recordId = authParams.getFirst("smart_record_id", true);
        TokenSecret tokenSecret = new TokenSecret(req);

        System.out.println("token: " + tokenSecret.getToken() + "   secret: " + tokenSecret.getTokenSecret());

        Map<String,GregorianCalendar> pillDates = new HashMap<String,GregorianCalendar>();

        // Represent the list as an RDF graph
        try {
            SMArtClient client = new SMArtClient(
                    "my-app@apps.smartplatforms.org","smartapp-secret","http://sandbox-api.smartplatforms.org");
            RepositoryConnection meds = null;
            if (/*true*/ false) { // testing
                Object oauthDebug = client.getUtils().smartRequest("GET", "/oauth/debug", "", tokenSecret, null, null, null, null);
                System.out.println("/oauth/debug: " + oauthDebug.getClass().getName() + " -- " + oauthDebug);
            } else {
                /*RepositoryConnection*/ meds = (RepositoryConnection)
                        client.records_X_medications_GET(recordId, tokenSecret, null);
            }

            String pillWhen = null;
            String pillQuant = null;
            String pillName = null;
            try {
                TupleQuery tq = meds.prepareTupleQuery(QueryLanguage.SPARQL, sparqlForReminders);
                TupleQueryResult tqr = tq.evaluate();
                while (tqr.hasNext()) {
                    BindingSet bns = tqr.next();
                    //        "   SELECT  ?med ?name ?quant ?when\n" +
                    pillWhen = bns.getValue("when").stringValue();
                    pillQuant = bns.getValue("quant").stringValue();
                    pillName = bns.getValue("name").stringValue();

                    XMLGregorianCalendar xgreg = dtf.newXMLGregorianCalendar(pillWhen);
                    GregorianCalendar greg = xgreg.toGregorianCalendar();
                    greg.add(GregorianCalendar.DAY_OF_YEAR, new Float(pillQuant).intValue());

                    GregorianCalendar priorWhen = pillDates.get("pillName");
                    if (priorWhen == null || priorWhen.before(greg)) {
                        pillDates.put(pillName, greg);
                    }
                }
            } catch (org.openrdf.repository.RepositoryException rex) {
                throw new ServletException(rex);
            } catch (org.openrdf.query.MalformedQueryException mqx) {
                throw new ServletException(mqx);
            } catch (org.openrdf.query.QueryEvaluationException qvx) {
                throw new ServletException(qvx);
            }

            Iterator<String> medNames = pillDates.keySet().iterator();
            StringBuffer retStrb = new StringBuffer();
            GregorianCalendar today = new GregorianCalendar();
            while (medNames.hasNext()) {
                String aMed = medNames.next();
                GregorianCalendar dayFromMap = pillDates.get(aMed);
                if (today.before(dayFromMap)) {
                    retStrb.append("<i>LATE!</i> ");
                }
                String xmlFormatDate = dtf.newXMLGregorianCalendar(dayFromMap).toXMLFormat();
                retStrb.append(aMed + ": <b>" + xmlFormatDate.substring(0,10)  + "</b><br>");
            }
            if (retStrb.length() == 0) {
                retStrb.append("Up to date on all meds.");
            }

        try {
            OutputStream resOut = res.getOutputStream();
            resOut.write("Refills due!<br><br>".getBytes());
            resOut.write(retStrb.toString().getBytes());
            resOut.write(reminderFooter.getBytes());
            resOut.close();
        } catch (IOException ioe) {
            throw new ServletException(ioe);
        }

        } catch (SMArtClientException sme) {
            System.out.println("sme:::: " + sme.getClass().getName());
            throw new ServletException(sme);
        }
    }


    private Map<String,String> mapFromAuthHeader(String header0) throws ServletException {
        String header = null;
        try {
            header = java.net.URLDecoder.decode(header0, "UTF-8");
        } catch (java.io.UnsupportedEncodingException uee) {
            throw new ServletException(uee);
        }
        Map<String,String> retVal = new HashMap<String,String>();

        if (! header.toUpperCase().startsWith("Authorization:".toUpperCase())) {
            throw new RuntimeException("unexpected cookie value: " + header);
        }
        header = header.substring("Authorization:".length()).trim();
        if (! header.toUpperCase().startsWith("OAuth".toUpperCase())) {
            throw new RuntimeException("unexpected cookie value: " + header);
        }
        header = header.substring("OAuth".length());
        String[] parts = header.split(",");
        for (String param : parts) {
            param = param.trim();
            String[] param_parts = param.split("=", 2);
            if (param_parts.length != 2) {
                throw new ServletException("unexpected Authorization value: " + param + "  --  " + header);
            }
            String param_val = param_parts[1];
            if (param_val.charAt(0) == '"') { param_val = param_val.substring(1, param_val.length() -1); }
            
            String decoded = null;
            try {
                decoded = java.net.URLDecoder.decode(param_val, "UTF-8");
            } catch (java.io.UnsupportedEncodingException uee) {
                throw new ServletException(uee);
            }

            System.out.println("putting into AuthHeader Map: " + param_parts[0] + " ,   " + decoded);
            retVal.put(param_parts[0], decoded);
        }
        return retVal;
    }
}


/*
  """
  split an oauth header into a dictionary
  """
  params = {}

  # remove the first "OAuth "
  header_without_oauth_prefix = header[6:]

  parts = header_without_oauth_prefix.split(',')
  for param in parts:
    # remove whitespace
    param = param.strip()

    # split key-value
    param_parts = param.split('=', 1)

    # remove quotes and unescape the value, but only if it's not realm
    if param_parts[0] == 'realm':
      continue

    params[param_parts[0]] = urllib.unquote(param_parts[1].strip('\"'))

*/



/*
"""
Example SMArt REST Application: Parses OAuth tokens from
browser-supplied cookie, then provides a list of which prescriptions
will need to be refilled soon (based on dispense quantity + date, and
the unrealistic simplifying assumption that one pill per day is
consumed).

Josh Mandel
Children's Hospital Boston, 2010
"""

import web, RDF, urllib
import datetime
import smart_client
from smart_client import oauth
from smart_client.smart import SmartClient
from smart_client.common.util import serialize_rdf

# Basic configuration:  the consumer key and secret we'll use
# to OAuth-sign requests.
SMART_SERVER_OAUTH = {'consumer_key': 'my-app@apps.smartplatforms.org', 
                      'consumer_secret': 'smartapp-secret'}


# The SMArt contianer we're planning to talk to
SMART_SERVER_PARAMS = {
    'api_base' :          'http://sandbox-api.smartplatforms.org'
}


"""
 A SMArt app serves at least two URLs: 
   * "bootstrap.html" page to load the client library
   * "index.html" page to supply the UI.
"""
urls = ('/smartapp/bootstrap.html', 'bootstrap',
        '/smartapp/index.html',     'RxReminder')


# Required "bootstrap.html" page just includes SMArt client library
class bootstrap:
    def GET(self):
        return """<!DOCTYPE html>
                   <html>
                    <head>
                     <script src="http://sample-apps.smartplatforms.org/framework/smart/scripts/smart-api-client.js"></script>
                    </head>
                    <body></body>
                   </html>"""


# Exposes pages through web.py
class RxReminder:

    """An SMArt REST App start page"""
    def GET(self):
        # Fetch and use
        cookie_name = web.input().cookie_name
        smart_oauth_header = web.cookies().get(cookie_name)
        smart_oauth_header = urllib.unquote(smart_oauth_header)
        client = get_smart_client(smart_oauth_header)

        # Represent the list as an RDF graph
        meds = client.records_X_medications_GET()

        # Find a list of all fulfillments for each med.
        q = """
            PREFIX dc:<http://purl.org/dc/elements/1.1/>
            PREFIX dcterms:<http://purl.org/dc/terms/>
            PREFIX sp:<http://smartplatforms.org/terms#>
            PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>
               SELECT  ?med ?name ?quant ?when
               WHERE {
                      ?med rdf:type sp:Medication .
                     ?med sp:drugName ?medc.
                      ?medc dcterms:title ?name.
                      ?med sp:fulfillment ?fill.
                      ?fill sp:dispenseQuantity ?quant.
                      ?fill dc:date ?when.
               }
            """
        pills = RDF.SPARQLQuery(q).execute(meds)
        # Find the last fulfillment date for each medication
        self.last_pill_dates = {}
        for pill in pills:
            self.update_pill_dates(pill)

        #Print a formatted list
        return header + self.format_last_dates() + footer

    def update_pill_dates(self, pill):        
        ISO_8601_DATETIME = '%Y-%m-%dT%H:%M:%SZ'
        def runs_out(pill):
            print "Date", str(pill['when'])
            s = datetime.datetime.strptime(str(pill['when']), ISO_8601_DATETIME)
            s += datetime.timedelta(days=int(float(str(pill['quant']))))
            return s

        r = runs_out(pill)
        previous_value = self.last_pill_dates.setdefault(pill['name'], r)
        if r > previous_value:
            self.last_pill_dates[pill['name']] = r


    def format_last_dates(self):
        ret = ""
        for (medname, day) in self.last_pill_dates.iteritems():
            late = ""
            if day < datetime.datetime.today(): 
                late = "<i>LATE!</i> "
            ret += late+str(medname)+ ": <b>" + day.isoformat()[:10]+"</b><br>"

        if (ret == ""): return "Up to date on all meds."
        ret =  "Refills due!<br><br>" + ret
        return ret


header = """<!DOCTYPE html>
<html>
  <head>                     <script src="http://sample-apps.smartplatforms.org/framework/smart/scripts/smart-api-page.js"></script></head>
  <body>
"""

footer = """
</body>
</html>"""


"""Convenience function to initialize a new SmartClient"""
def get_smart_client(authorization_header, resource_tokens=None):
    authorization_header = authorization_header.split("Authorization: ",1)[1]
    oa_params = oauth.parse_header(authorization_header)
    
    resource_tokens={'oauth_token':       oa_params['smart_oauth_token'],
                     'oauth_token_secret':oa_params['smart_oauth_token_secret']}

    ret = SmartClient(SMART_SERVER_OAUTH['consumer_key'], 
                       SMART_SERVER_PARAMS, 
                       SMART_SERVER_OAUTH, 
                       resource_tokens)
    
    ret.record_id=oa_params['smart_record_id']
    return ret

app = web.application(urls, globals())
if __name__ == "__main__":
    app.run()
*/
