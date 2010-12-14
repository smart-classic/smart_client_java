package org.smartplatforms.client;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.Map;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
import org.w3c.dom.Document;


/**
 *
 * @author nate
 */
public class DefaultResponseTypeConversion implements ResponseTypeConversion {

    private Log logger = null;

    private DocumentBuilderFactory documentBuilderFactory = null;
    private final DocumentBuilder documentBuilder;


    public DefaultResponseTypeConversion() throws SMArtClientException {
        logger = LogFactory.getLog(this.getClass());
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (javax.xml.parsers.ParserConfigurationException pce) {
            throw new SMArtClientException(pce);
        }
    }


    public Object responseToObject(HttpEntity entity)
            throws SMArtClientException {
        Object retVal = null;
        Document docForContent = null;
        Header contentTypeH = entity.getContentType();
        String contentType = null;
        if (contentTypeH != null) { contentType = contentTypeH.getValue(); }
        logger.info("contentType header: " + contentType);
        Header encodingH = entity.getContentEncoding();
        String encoding = null;

        InputStream istrm = null;
        try {
            istrm = entity.getContent();
        } catch (IOException ioe) {
                throw new SMArtClientException(ioe);
        }
        String istrmdata = dataFromStream(istrm);

        logger.debug("coercing: " + istrmdata);

         // workaround for Pivotal 2172901
        if (contentType.startsWith("application/xml") || contentType.startsWith("text/xml")) {
            try {
//                String[] parsedProlog = Utils.getEncoding(istrmdata);
//                String prologEncoding = "UTF-8";
//                if (parsedProlog.length == 3) {
//                    prologEncoding = parsedProlog[1];
//                }

                ByteArrayInputStream bais = new ByteArrayInputStream(istrmdata.getBytes());

                Sail memstore = new MemoryStore();
                Repository myRepository = new SailRepository(memstore);
                myRepository.initialize();
                RepositoryConnection con = myRepository.getConnection();

                con.add(bais, null, RDFFormat.RDFXML);
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

        String hresp0 = StringEscapeUtils.unescapeHtml(hresp);
        logger.info("encoded entitied response body: " + hresp
                + "         encoded response body: " + hresp0);
        String[] pairs = hresp0.split("&");
        for (int tt = 0; tt < pairs.length; tt++) {
            logger.info("apair: " + pairs[tt]);
        }

        for (int ii = 0; ii < pairs.length; ii++) {
            int eix = pairs[ii].indexOf('=');
            if (eix == -1) {
                throw new SMArtClientException("did not find '=' in param: " + pairs[ii] + "\n" + hresp0);
            }
            String pName = pairs[ii].substring(0,eix);
            if (retVal.get(pName) != null) {
                throw new SMArtClientException("found multiple '" + pName + "' params." + hresp0);
            }
            retVal.put(pName, pairs[ii].substring(eix +1));
        }
        return retVal;
    }


}
