package org.smartplatforms.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.Sail;
import org.openrdf.sail.memory.MemoryStore;


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

    public DefaultResponseTypeConversion() throws SmartClientException {
        logger = LogFactory.getLog(this.getClass());
    }


    /**
     * This is to process an http response and return the response
     * as an instance of an appropriate class.
     *
     * @param entity
     * @return http respose as an instance of an appropriate class
     * @throws SmartClientException
     */
    @Override
    public SmartResponse responseToObject(HttpEntity entity) throws SmartClientException {
        SmartResponse retObj = new SmartResponse ();
        Header contentTypeH = entity.getContentType();
        String contentType = null;
        if (contentTypeH != null) { contentType = contentTypeH.getValue(); }
        logger.info("contentType header: " + contentType);

        InputStream istrm = null;
        try {
            istrm = entity.getContent();
        } catch (IOException ioe) {
                throw new SmartClientException(ioe);
        }
        String istrmdata = dataFromStream(istrm);

        logger.info("coercing: " + istrmdata);

        retObj.data = istrmdata;
        retObj.contentType = contentType;
        
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(istrmdata.getBytes());
            Sail memstore = new MemoryStore();
            Repository myRepository = new SailRepository(memstore);
            myRepository.initialize();
            RepositoryConnection con = myRepository.getConnection();
            con.add(bais, "", RDFFormat.RDFXML);
            retObj.graph = con;
        } catch (RepositoryException rpe) {
            logger.debug(istrmdata, rpe);
            throw new SmartClientException(rpe);
        } catch (IOException ioe) {
            logger.debug(istrmdata, ioe);
            throw new SmartClientException(ioe);
        } catch (RDFParseException rdfpe) {
            logger.debug(istrmdata, rdfpe);
        }
        
        try {
            retObj.json = (JSONObject) JSONSerializer.toJSON( istrmdata );
        } catch (Exception e) {
            logger.debug(istrmdata, e);
        }
        
        return retObj;
    }

    /**
     * turn the http response data into a String.
     *
     * @param inputStrm
     * @return String representation of http response stream
     * @throws SmartClientException
     */
    String dataFromStream(InputStream inputStrm) throws SmartClientException {
        String xstr = null;
        try {
            int xcc = inputStrm.read();
            StringBuffer xstrb = new StringBuffer();
            while (xcc != -1) {
                xstrb.append((char) xcc);
                xcc = inputStrm.read();
            }
            xstr = xstrb.toString();

        } catch (IOException ioe) {
            throw new SmartClientException(ioe);
        }
        return xstr;
    }

}
