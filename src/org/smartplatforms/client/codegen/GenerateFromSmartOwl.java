package org.smartplatforms.client.codegen;


import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;

import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.OutputStream;
import java.io.FileOutputStream;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.BNode;
import org.openrdf.repository.Repository;
//import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.sail.Sail;
//import org.openrdf.sail.memory.model.MemBNode;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.memory.model.MemResource;
import org.openrdf.sail.memory.model.MemValue;
import org.openrdf.sail.memory.model.MemURI;
import org.openrdf.sail.memory.model.MemStatementList;
import org.openrdf.sail.memory.model.MemStatement;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.resultio.TupleQueryResultWriter;
//import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.rio.RDFFormat;

/**
 * Hello world!
 *
 */
public class GenerateFromSmartOwl {
    private boolean isForChallenge = false;
    private Map<String,String> javaDocTable = null;

    //private List<String> expectedPredicates = null;


    private GenerateFromSmartOwl() {
        javaDocTable = new HashMap<String,String>();
        javaDocTable.put("recordId","server's record ID");
        javaDocTable.put("externalId", "external ID");
        javaDocTable.put("allergyId", "server's internal ID for this allergy document");
        javaDocTable.put("medicationId", "server's internal ID for this medication document");
        javaDocTable.put("fulfillmentId", "server's internal ID for this fulfillment document");
        javaDocTable.put("medExternalId", "ID assigned to the medication document encompassing this fulfillment");
        javaDocTable.put("noteId", "server's internal ID for this note document");
        javaDocTable.put("problemId", "server's internal ID for this problem document");
        javaDocTable.put("userId", "server's internal ID for this user");
        javaDocTable.put("labResultId", "server's internal ID for this lab result");
        javaDocTable.put("labResultPanelId", "server's internal ID for this lab result panel");
        javaDocTable.put("smartAppId", "server's internal ID for this app");
        javaDocTable.put("immunizationId", "server's internal ID for this immunization record");
        javaDocTable.put("vitalSignSetId", "server's internal ID for this vital signs record");
        javaDocTable.put("descriptor", "server's internal ID for this app");
        javaDocTable.put("encounterId", "server's internal ID for this encounter");
        javaDocTable.put("clinicalNoteId", "server's internal ID for this clinical note");
        javaDocTable.put("procedureId", "server's internal ID for this procedure");
    }

    public static void main( String[] args )
    {
        System.out.println( "Starting client generation" );
        GenerateFromSmartOwl instance = new GenerateFromSmartOwl();

        if (args.length == 2) {
            if (args[1].equals("challenge")) {
                instance.isForChallenge = true;
            } else if (args[1].equals("all")) {
                instance.isForChallenge = false;
            } else if (args[1].trim().length() == 0) {
                instance.isForChallenge = false;
            } else {
                throw new RuntimeException("GenerateFromSmartOwl <path_to_smart.owl> [\"challenge\"]\n" +
                    "arg1 was: " + args[0] + "    arg2 was: " + args[1]);
            }
        }
        if (args.length < 1 || args.length > 2) {
            throw new RuntimeException("GenerateFromSmartOwl <path_to_smart.owl> [\"challenge\"]");
        }

        /* first, we init the runtime engine.  Defaults are fine. */
        try {
        Velocity.init();
        } catch (Exception excp) {
            throw new RuntimeException(excp);
        }
        instance.dowork(args[0] /*full path to smart.owl*/);
        System.out.println( "Finished client generation" );
    }


    /*org.openrdf.repository.RepositoryConnection*/
    private void dowork(String smartOwlFileName) {
        try {
            
            OutputStream fos = new FileOutputStream("../build/SmartClient.java");
            Writer writer = new OutputStreamWriter(fos);
            //writeJavaFragment("StartClient.java", fos);

            VelocityContext startEndVC = new VelocityContext();
            System.out.println ("challenge version: " + isForChallenge);
            startEndVC.put("challenge", isForChallenge);
            startEndVC.put("linefeed", "\n");
            
            try {
                Velocity.mergeTemplate("StartClient.java", "UTF-8", startEndVC, writer );
            } catch (Exception excp) {
                throw new RuntimeException(excp);
            }
            writer.flush();

            dowork0(writer, smartOwlFileName);
            //writeJavaFragment("EndClient.java", fos);
            try {
                Velocity.mergeTemplate("EndClient.java", "UTF-8", startEndVC, writer );
            } catch (Exception excp) {
                throw new RuntimeException(excp);
            }
            writer.flush();

            fos.write("\n}\n".getBytes());
            writer.close();
        } catch (org.openrdf.OpenRDFException repE) {
            throw new RuntimeException(repE);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /*
package org.smartplatforms.client.codegen;  /* NOT PART OF GENERATED CLIENT +/
// package org.smartplatforms.client;  /* UNCOMMENT IN GENERATED CLIENT +/
    */
    private void writeJavaFragment(String filename, OutputStream fos) {
        System.out.println("in writeJavaFragment for: " + filename);
        try {
            InputStream fis = new FileInputStream("../java/org/smartplatforms/client/codegen/" + filename);
            java.io.LineNumberReader lnr = new java.io.LineNumberReader(new java.io.InputStreamReader(fis));
            String aline = lnr.readLine();
            while (aline != null) {
                if (aline.trim().endsWith("/* NOT PART OF GENERATED CLIENT */")) {
                    // do nothing, drop line that was there only to make file valid java standing alone
                } else if (aline.trim().startsWith("//")
                        && aline.trim().endsWith("/* UNCOMMENT IN GENERATED CLIENT */")) {
                    // commented to make it valid java as a stand-alone file, uncomment for use in generated client
                    fos.write((aline.replaceFirst("//", "").replace(
                            "/* UNCOMMENT IN GENERATED CLIENT */", "") + '\n').getBytes());
                }
                else {
                    fos.write((aline + '\n').getBytes());
                }
                aline = lnr.readLine();
            }
            lnr.close();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /** use velocity to help generate the java code */
    private void dowork0(Writer writer, String smartOwlFileName) throws org.openrdf.OpenRDFException, IOException {

        Sail memstore = new MemoryStore();
        Repository myRepository = new SailRepository(memstore);
        myRepository.initialize();

        RepositoryConnection con = myRepository.getConnection();

//        File apiOwlFile = new File("/home/jmandel/smart/smart_server/smart/document_processing/schema/smart.owl");
        File apiOwlFile = new File(smartOwlFileName);
        con.add(apiOwlFile, null, RDFFormat.RDFXML, new Resource[0]);

        String sparqlAllTheWay =
            "PREFIX api: <http://smartplatforms.org/terms/api#> \n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
            "SELECT  ?call ?description ?method ?method_name ?path ?target ?by_internal_id ?category ?cardinality  \n" +
            "    WHERE { { ?call <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> api:Call }.\n" +
            "            { ?call rdfs:comment ?description }.\n" +
            "            { ?call api:httpMethod ?method }.\n" +
            "            { ?call api:clientMethodName ?method_name }.\n" +
            "            { ?call api:path ?path }.\n" +
            "            { ?call api:cardinality ?cardinality }.\n" +
            "           OPTIONAL { ?call api:target ?target }.\n" +
            "           OPTIONAL { ?call api:by_internal_id ?by_internal_id }.\n" +
            "           OPTIONAL { ?call api:category ?category }.\n" +
            "        }";

        TupleQuery tq = con.prepareTupleQuery(QueryLanguage.SPARQL, sparqlAllTheWay);
        TupleQueryResult tqr = tq.evaluate();

        // for each api:call
        while (tqr.hasNext()) {
            BindingSet bindSet = tqr.next();  // get next api:call
            //System.out.println("description: " + bindSet.getValue("description").stringValue());
            
            //api:clientParameterName
            //api:hasFilter
            
                    
            String callURI = "<" + bindSet.getValue("call").stringValue() + ">";
            String sparql =
                "PREFIX api: <http://smartplatforms.org/terms/api#> \n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
                "SELECT ?param \n" +
                "    WHERE { { " + callURI + " <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> api:Call }.\n" +
                "            { " + callURI + " api:hasFilter ?f }.\n" +
                "            { ?f api:clientParameterName ?param }.\n" +
                "        }";
            
            TupleQuery tq2 = con.prepareTupleQuery(QueryLanguage.SPARQL, sparql);
            TupleQueryResult tqr2 = tq2.evaluate();
            String strFilters = "";
            
           if (bindSet.getValue("cardinality").stringValue().equals("multiple")) {
               strFilters = "limit,offset";
           }
            
            while (tqr2.hasNext()) {
                BindingSet bindSet2 = tqr2.next();
                if (strFilters.length() > 0) strFilters += ",";
                strFilters += bindSet2.getValue("param").stringValue();
            }
            
            String[] filtersArr = strFilters.split(",");
            String filters = "";
            
            if (strFilters.length() > 0) {
                for (int i = 0; i < filtersArr.length; i++) {
                    if (i > 0) filters += ",";
                    filters += "\"" + filtersArr[i] + "\"";
                }
            } else filters = null;
            
            processACall(bindSet, filters, writer);
        }
    }

    private void processACall(BindingSet bindSet, String filters, Writer writer) throws IOException {
        VelocityContext callPO = new VelocityContext();
        callPO.put("booleanTrue", true);
        callPO.put("booleanFalse", false);
        callPO.put("challenge", isForChallenge);

        String pathBase = "/";

        String[] preds =  new String[] {
            "description",  "method", "method_name", "path", "target", "by_internal_id", "category", "cardinality" };
        for (String pred : preds) {
            Value objectVal = bindSet.getValue(pred);
            String objectStr = null;
            if (objectVal == null
                    && ( pred.equals("target")
                        || pred.equals("by_internal_id")
                        || pred.equals("category") )
               ) {
                objectStr = "";
            } else {
                objectStr = bindSet.getValue(pred).stringValue();
            }
            if (pred.equals("path")) {
                if (! objectStr.startsWith(pathBase)) {
                    throw new RuntimeException(
                            "expected path to start with ''" + pathBase + "'': " + objectStr);
                }
                String woBase = objectStr.substring(pathBase.length());
                String path0 = woBase;

                int qix = path0.indexOf('?');
                String query = "";
                if (qix > 0) {
                    woBase = path0.substring(0, qix);
                    query = path0.substring(qix +1);
                }

                //http://smartplatforms.org/users/search?givenName={givenName}&amp;familyName={familyName}
                Map<String,String> queryMap = new HashMap<String,String>();
                if (query.length() > 0) {
                    String[] queryA = query.split("&");
                    for (int ii = 0; ii < queryA.length; ii++) {
                        String aparam = queryA[ii];
                        String[] aparamA = aparam.split("=");
                        if (aparamA.length != 2) {
                            throw new RuntimeException("param without '=': " + query);
                        }
                        
                        if (aparamA[1].charAt(0) == '{' && aparamA[1].charAt(aparamA[1].length() -1) == '}') {
                            aparamA[1] = aparamA[1].substring(1, aparamA[1].length() -1);
                        }

                        queryMap.put(aparamA[0], aparamA[1]);
                    }
                }
                callPO.put("queryMap", queryMap);

                callPO.put("path", woBase);
                callPO.put("query", query);
                
                String[] tokenListA = woBase.split("/");
                callPO.put("tokenList", tokenListA);

            } else {
                callPO.put(pred, objectStr);
            }
        }

        callPO.put("javadocTable", javaDocTable);
        callPO.put("generateFromSmartOwl", this);
        callPO.put("filters", filters);

        //preVelocity(callPO, fos);
        useVelocity(callPO, writer);
    }

    private void useVelocity(VelocityContext callPO, Writer writer) {
        try {
            useVelocity0(callPO, writer);
        } catch (Exception excp) {
            throw new RuntimeException(excp);
        }
    }

    private void useVelocity0(VelocityContext callPO, Writer writer) throws Exception {
        /* lets render a template */

        Velocity.mergeTemplate("generatedSource.vsl", "UTF-8", callPO, writer );
        writer.flush();
    }



    public String tokenUnbracked(String token) {
        String retVal = token.substring(1, token.length() -1);
        int uix = retVal.indexOf('_');
        while (uix > 0 && uix < retVal.length() -1) {
            String retVal0 = retVal.substring(0, uix);
            retVal0 += retVal.substring(uix +1, uix +2).toUpperCase();
            retVal0 += retVal.substring(uix +2);
            retVal = retVal0;
            uix = retVal.indexOf('_');
        }
        return retVal;
    }

}
