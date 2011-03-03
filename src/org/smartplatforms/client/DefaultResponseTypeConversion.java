package org.smartplatforms.client;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.Map;
import java.util.HashMap;

import java.net.URLDecoder;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.http.HttpEntity;
import org.apache.http.Header;

import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.Sail;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;


/**
 *
 * @author nate
 *
 * Use this if you want RDF data returned in the form of
 *     org.openrdf.repository.RepositoryConnection objects
 *
 * Create an alternate implementation of ResponseTypeConversion
 *    if you prefer to use another RDF representation package.
 */
public class DefaultResponseTypeConversion implements ResponseTypeConversion {

    private Log logger = null;

    public DefaultResponseTypeConversion() throws SMArtClientException {
        logger = LogFactory.getLog(this.getClass());
    }


    /**
     * This is to process an http response and return the response
     * as an instance of an appropriate class.
     *
     * @param entity
     * @return http respose as an instance of an appropriate class
     * @throws SMArtClientException
     */
    public Object responseToObject(HttpEntity entity)
            throws SMArtClientException {
        Object retVal = null;
        Header contentTypeH = entity.getContentType();
        String contentType = null;
        if (contentTypeH != null) { contentType = contentTypeH.getValue(); }
        logger.info("contentType header: " + contentType);
//        Header encodingH = entity.getContentEncoding();
//        String encoding = null;

        InputStream istrm = null;
        try {
            istrm = entity.getContent();
        } catch (IOException ioe) {
                throw new SMArtClientException(ioe);
        }
        String istrmdata = dataFromStream(istrm);

        logger.debug("coercing: " + istrmdata);

        if (contentType.startsWith("application/rdf+xml")) {
            try {
                ByteArrayInputStream bais = new ByteArrayInputStream(istrmdata.getBytes());
                Sail memstore = new MemoryStore();
                Repository myRepository = new SailRepository(memstore);
                myRepository.initialize();
                RepositoryConnection con = myRepository.getConnection();
                con.add(bais, "", RDFFormat.RDFXML);
                retVal = con;
            } catch (org.openrdf.repository.RepositoryException rpe) {
                logger.debug(istrmdata, rpe);
                throw new SMArtClientException(rpe);
            } catch (org.openrdf.rio.RDFParseException rdfpe) {
                logger.debug(istrmdata, rdfpe);
                throw new SMArtClientException(rdfpe);
            } catch (IOException ioe) {
                logger.debug(istrmdata, ioe);
                throw new SMArtClientException(ioe);
            }
        } else if (contentType.startsWith("application/x-www-form-urlencoded")) {
            retVal = mapFromFormEncodedString(istrmdata);
        } else if (contentType.startsWith("text/plain")) {
            retVal = istrmdata;
        } else {
            retVal = new String[2];
            ((String[])retVal)[0] = contentType;
            ((String[])retVal)[1] = istrmdata;
        }
        return retVal;
    }

    /**
     * turn the http response data into a String.
     *
     * @param inputStrm
     * @return String representation of http response stream
     * @throws SMArtClientException
     */
    String dataFromStream(InputStream inputStrm) throws SMArtClientException {
        String xstr = null;
        try {
            int xcc = inputStrm.read();
            StringBuffer xstrb = new StringBuffer();
//            StringBuffer unexpectedBuffer = new StringBuffer();
            while (xcc != -1) {
                xstrb.append((char) xcc);
                xcc = inputStrm.read();
            }
            xstr = xstrb.toString();

        } catch (java.io.IOException ioe) {
            throw new SMArtClientException(ioe);
        }
        return xstr;
    }

    /**
    * given a form-urlencoded response, return that response in the
    * form of a key-value map.
    */
    public Map<String,String> mapFromFormEncodedString(String hresp)
            throws SMArtClientException {

        Map<String,String> retVal = new HashMap<String,String>();

        //String hresp0 = StringEscapeUtils.unescapeHtml(hresp);
        //logger.info("encoded entitied response body: " + hresp
        //        + "         encoded response body: " + hresp0);
        String[] pairs = hresp.split("&");
        for (int tt = 0; tt < pairs.length; tt++) {
            logger.info("apair: " + pairs[tt]);
        }

        for (int ii = 0; ii < pairs.length; ii++) {
            int eix = pairs[ii].indexOf('=');
            if (eix == -1) {
                throw new SMArtClientException("did not find '=' in param: " + pairs[ii] + "\n" + hresp);
            }
            String pName = pairs[ii].substring(0,eix);
            if (retVal.get(pName) != null) {
                throw new SMArtClientException("found multiple '" + pName + "' params." + hresp);
            }

            String pValue = null;
            try {
                pName = URLDecoder.decode(pName, "UTF-8");
                pValue = URLDecoder.decode(pairs[ii].substring(eix +1), "UTF-8");
            } catch (java.io.UnsupportedEncodingException uee) {
                pValue = pairs[ii].substring(eix +1);
                logger.warn("failure to URLDecode " + hresp, uee);
            }

//            retVal.put(pName, pairs[ii].substring(eix +1));
            retVal.put(pName, pValue);
        }
        return retVal;
    }
}
