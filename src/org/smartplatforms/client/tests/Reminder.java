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
import javax.servlet.ServletConfig;
import org.openrdf.query.QueryLanguage;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.BindingSet;

import org.smartplatforms.client.SMArtClient;
import org.smartplatforms.client.SMArtClientException;
import org.smartplatforms.client.SMArtOAuthParser;
import org.smartplatforms.client.TokenSecret;

public class Reminder extends HttpServlet {
	private String bootstrapPage = "<!DOCTYPE html><html>"
			+ "<head><script src=\"http://sample-apps.smartplatforms.org/framework/smart/scripts/smart-api-client.js\"></script></head>"
			+ "<body></body></html>";

	String reminderHeader = "<!DOCTYPE html>\n<html><head>"
			+ "<script src=\"http://sample-apps.smartplatforms.org/framework/smart/scripts/smart-api-page.js\">"
			+ "</script><title>java generated</title></head>\n<body>\n";

	String reminderFooter = "</body></html>";

        private ServletConfig sConfig = null;
	private DatatypeFactory dtf = null;

	private String sparqlForReminders = "PREFIX dc:<http://purl.org/dc/elements/1.1/>\n"
			+ "PREFIX dcterms:<http://purl.org/dc/terms/>\n"
			+ "PREFIX sp:<http://smartplatforms.org/terms#>\n"
			+ "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
			+ "   SELECT  ?med ?name ?quant ?when\n"
			+ "   WHERE {\n"
			+ "          ?med rdf:type sp:Medication .\n"
			+ "          ?med sp:drugName ?medc.\n"
			+ "          ?medc dcterms:title ?name.\n"
			+ "          ?med sp:fulfillment ?fill.\n"
			+ "          ?fill sp:dispenseQuantity ?quant.\n"
			+ "          ?fill dc:date ?when.\n" + "   }";


	@Override
	public void init() throws ServletException {
	    System.out.println("in init() for Reminder");
	    
	    this.sConfig = getServletConfig();
	    
	    try {
		dtf = DatatypeFactory.newInstance();
	    } catch (javax.xml.datatype.DatatypeConfigurationException dce) {
		throw new ServletException(dce);
	    }
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException {
		System.out.println("in doGet() for Reminder  --  " + req.getPathInfo());
		String pathInfo = req.getPathInfo();
		if (pathInfo.equals("/bootstrap.html")) {
			try {
				OutputStream ros = res.getOutputStream();
				ros.write(bootstrapPage.getBytes());
				ros.close();
			} catch (IOException ioe) {
				throw new ServletException(ioe);
			}
		} else if (pathInfo.equals("/index.html")) {
			presentReminders(req, res);
		}
	}

	private void presentReminders(HttpServletRequest req,
			HttpServletResponse res) throws ServletException {

		try {
			OutputStream resOut = res.getOutputStream();
			resOut.write(reminderHeader.getBytes());
		} catch (IOException ioe) {
			throw new ServletException(ioe);
		}

		SMArtOAuthParser authParams = new SMArtOAuthParser(req);
		String recordId = authParams.getParam("smart_record_id");
		TokenSecret tokenSecret = new TokenSecret(authParams);

		Map<String, GregorianCalendar> pillDates = new HashMap<String, GregorianCalendar>();

		// Represent the list as an RDF graph
		try {
			SMArtClient client = new SMArtClient(
							     sConfig.getInitParameter("consumerKey"),
							     sConfig.getInitParameter("consumerSecret"),
							     sConfig.getInitParameter("serverBaseURL"));

			RepositoryConnection meds = (RepositoryConnection) client
					.records_X_medications_GET(recordId, tokenSecret, null);

			String pillWhen = null;
			String pillQuant = null;
			String pillName = null;
			try {
				TupleQuery tq = meds.prepareTupleQuery(QueryLanguage.SPARQL,
						sparqlForReminders);
				TupleQueryResult tqr = tq.evaluate();
				while (tqr.hasNext()) {
					BindingSet bns = tqr.next();
					// "   SELECT  ?med ?name ?quant ?when\n" +
					pillWhen = bns.getValue("when").stringValue();
					pillQuant = bns.getValue("quant").stringValue();
					pillName = bns.getValue("name").stringValue();

					XMLGregorianCalendar xgreg = dtf
							.newXMLGregorianCalendar(pillWhen);
					GregorianCalendar greg = xgreg.toGregorianCalendar();
					greg.add(GregorianCalendar.DAY_OF_YEAR,
							new Float(pillQuant).intValue());

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
				String xmlFormatDate = dtf.newXMLGregorianCalendar(dayFromMap)
						.toXMLFormat();
				retStrb.append(aMed + ": <b>" + xmlFormatDate.substring(0, 10)
						+ "</b><br>");
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
}
