import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import oauth.signpost.OAuth;
import oauth.signpost.http.HttpParameters;

import org.openrdf.OpenRDFException;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;

//import org.smartplatforms.client.SMArtClientException;
import org.smartplatforms.client.SMArtClient;

import java.net.URL;

import javax.servlet.Servlet;

/**
 * Servlet implementation class SampleApp
 */
public class SampleApp extends HttpServlet {
       private static final long serialVersionUID = 1L;

   /**
    * @see HttpServlet#HttpServlet()
    */
   public SampleApp() {
       super();
       // TODO Auto-generated constructor stub
   }


   public static String getCookieValue(Cookie[] cookies, String cookieName,
                   String defaultValue) {
           for (int i = 0; i < cookies.length; i++) {
                   Cookie cookie = cookies[i];
                   if (cookieName.equals(cookie.getName()))
                           return (cookie.getValue());
           }
           return (defaultValue);
   }

HttpParameters parseOAuthCookieFromRequest(HttpServletRequest request) {

           String cn = request.getParameter("cookie_name");
           String c = getCookieValue(request.getCookies(), cn, null);
           try {
                   c = java.net.URLDecoder.decode(c, "UTF-8");
           } catch (UnsupportedEncodingException e) {
                   // TODO Auto-generated catch block
                   e.printStackTrace();
           }
           
           c = c.split("Authorization: ")[1];
           HttpParameters p = OAuth.oauthHeaderToParamsMap(c);
           return p;
}

       /**
        * @see HttpServlet#doGet(HttpServletRequest request,
HttpServletResponse response)
        */
       protected void doGet(HttpServletRequest request, HttpServletResponse
response) throws ServletException, IOException {
       PrintWriter out = response.getWriter();

       String consumerKey = getInitParameter("consumerKey");
       String consumerSecret = getInitParameter("consumerSecret");

       String serverBaseURL =  "http://sandbox-api.smartplatforms.org/";

       try {
    	   	   SMArtClient smartClient = new SMArtClient("my-app@apps.smartplatforms.org","smartapp-secret", serverBaseURL);
               RepositoryConnection repC = (RepositoryConnection) smartClient.ontologyGET(null);
               
               out.println("Loaded ontology!");

               /* Now smartClient can request items from the patient record... */
               smartClient.setAccessToken(request);
               out.println("Set access tokens!");

               HttpParameters p = parseOAuthCookieFromRequest(request);
               String record_id = p.getFirst("smart_record_id");
               
               RepositoryConnection meds = (RepositoryConnection) smartClient.records_X_medications_GET(record_id, null);
               out.println("All meds retrieved:");
               RDFHandler w = new RDFXMLWriter(out);
               meds.export(w);
               

       } catch (Exception e) {
                       // TODO Auto-generated catch block
                       out.println("We had a problelm");
                       e.printStackTrace();
               }
       }

       /**
        * @see HttpServlet#doPost(HttpServletRequest request,
HttpServletResponse response)
        */
       protected void doPost(HttpServletRequest request, HttpServletResponse
response) throws ServletException, IOException {
               // TODO Auto-generated method stub
       }

}