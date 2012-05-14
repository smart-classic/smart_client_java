package org.smartplatforms.client;

import net.sf.json.JSONObject;
import org.openrdf.repository.RepositoryConnection;

/**
 *
 * @author nshver01
 */
public class SmartResponse {
    public String data;
    public String contentType;
    public RepositoryConnection graph;
    public JSONObject json;
}