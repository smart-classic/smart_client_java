package org.smartplatforms.client.codegen;

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
import org.openrdf.rio.turtle.TurtleWriter;
import org.openrdf.rio.RDFFormat;

import org.smartplatforms.client.codegen.GenClientUtils;
/**
 * Hello world!
 *
 */
public class GenerateFromSmartOwl {
    private List<String> expectedPredicates = null;
    GenClientUtils genClientUtils = new GenClientUtils();

    /*import java.io.UnsupportedEncodingException;
      import java.net.URLEncoder;*/
    String endOfClassSource =
        "\n    private String urlEncode(String toEncode) throws org.smartplatforms.client.SMArtClientException {\n" +
        "        try {\n" +
        "            return java.net.URLEncoder.encode(toEncode,\"UTF-8\");\n" +
        "        } catch (java.io.UnsupportedEncodingException uee) {\n" +
        "            throw new org.smartplatforms.client.SMArtClientException(uee);\n" +
        "        }\n" +
        "    }\n\n";


    private GenerateFromSmartOwl() {
        expectedPredicates = Arrays.asList("description", "method", "path", "target", "by_internal_id", "category");
    }

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        GenerateFromSmartOwl instance = new GenerateFromSmartOwl();
        instance.dowork();
        System.out.println( "'later World! ============" );
    }


    /*org.openrdf.repository.RepositoryConnection*/
    private void dowork() {
        try {
            
            OutputStream fos = new FileOutputStream("SMArtClient.java");
            fos.write("package org.smartplatforms.client;\n\n".getBytes());
            fos.write("import org.openrdf.repository.RepositoryConnection;\n\n".getBytes());
            fos.write("class SMArtClient {\n\n".getBytes());
            dowork0(fos);
            fos.write(endOfClassSource.getBytes());
            fos.write("}\n".getBytes());
            fos.close();
        } catch (org.openrdf.OpenRDFException repE) {
            throw new RuntimeException(repE);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private void dowork0(OutputStream fos) throws org.openrdf.OpenRDFException, IOException {
//        TurtleWriter turtleWriter = new TurtleWriter(System.out);
        SPARQLResultsXMLWriter sparqlWriter = new SPARQLResultsXMLWriter(System.out);
        //SPARQLResultsXMLWriter

        Sail memstore = new MemoryStore();
        Repository myRepository = new SailRepository(memstore);
        myRepository.initialize();

        RepositoryConnection con = myRepository.getConnection();

        File apiOwlFile = new File("/home/nate/SMArt/smart_server/smart/document_processing/schema/smart.owl");
        con.add(apiOwlFile, null, RDFFormat.RDFXML, new Resource[0]);

        String myfirstsparql =
            "PREFIX api: <http://smartplatforms.org/api/> \n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n" +
            "SELECT ?bs WHERE { ?bs <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://smartplatforms.org/api/call>. }";
        TupleQuery tq = con.prepareTupleQuery(QueryLanguage.SPARQL, myfirstsparql);
        TupleQueryResult tqr = tq.evaluate();

        // for each api:call
        String methodPrefix = "    public RepositoryConnection ";
        String throwsClause = "throws org.smartplatforms.client.SMArtClientException {";
        while (tqr.hasNext()) {
            BindingSet bindSet = tqr.next();  // get next api:call
            Value subject = bindSet.getValue("bs");
            if ( !(subject instanceof /*MemResource*/ BNode)) {
                throw new RuntimeException("expected BNode: " + subject.getClass().getName());
            }

            MemResource mres = (MemResource) subject;
            MemStatementList msl = mres.getSubjectStatementList();
            System.out.println("\n\n\n");
            Map<String,String> callPO = new HashMap<String,String>();
            for (int ii = 0; ii < msl.size(); ii++) {
                MemStatement msta = msl.get(ii);
                BNode stmntSubject = (BNode) msta.getSubject();
                System.out.println("statement: " +
                        stmntSubject.getClass().getName() + " " +
                        "   " + msta.getPredicate().getNamespace() + " : " + msta.getPredicate().getLocalName() +
                        "   " + msta.getObject().stringValue() );
                addToMap(callPO, msta);
            }
            // now we have a map, callPO, of the significant predicate/object pairs
            //"description", "method", "path", "target", "by_internal_id", "category"
            String path0 = callPO.get("path");
            String path = path0;
            int qix = path0.indexOf('?');
            String query = null;
            if (qix > 0) {
                path = path0.substring(0, qix);
                query = path0.substring(qix +1);
            }
            String[] tokenListA = path.split("/");
            boolean endsWithSlash = path.charAt(path.length() -1) == '/';
            List<String> tokenList = Arrays.asList(tokenListA);
            StringBuffer javaDoc = new StringBuffer("    /** " + callPO.get("description") + " -- " + path0);
            StringBuffer methSig = new StringBuffer();
            StringBuffer requestURL_SB = new StringBuffer();
            int[] stateA = new int[] { GenClientUtils.STATE_START };
            List<String> paramTypedList = new ArrayList<String>();

            genClientUtils.tokensToSig(
                    tokenList,
                    methSig,
                    paramTypedList,
                    javaDoc,
                    requestURL_SB,
                    path,
                    stateA
            );
            int state = stateA[0];
            if (endsWithSlash) {
                methSig.append('_');
                if (state == GenClientUtils.STATE_CONSTANT) {
                    requestURL_SB.append("/\"");
                } else {
                    requestURL_SB.append(" + \"/\"");
                }
            } else {
                if (state == GenClientUtils.STATE_CONSTANT) {
                    requestURL_SB.append('"');
                }
            }

            methSig.append(callPO.get("method") + "(");
            for (int ii = 0; ii < paramTypedList.size(); ii++) {
                if (ii > 0) {
                    methSig.append(", ");
                }
                methSig.append(paramTypedList.get(ii));
            }

            if (query != null) {
                String[] queryA = query.split("&");
                for (int ii = 0; ii < queryA.length; ii++) {
                    String aparam = queryA[ii];
                    String[] aparamA = aparam.split("=");
                    if (aparamA.length != 2) {
                        throw new RuntimeException("param without '=': " + query);
                    }
                    javaDoc.append("\n    * @param " + aparamA[0] + ' ' + aparamA[1]);
                    if (ii == 0) { requestURL_SB.append(" + '?' "); }
                    else { requestURL_SB.append(" + '&' "); }

                    requestURL_SB.append(" + \"" + aparamA[0] + "=\" + urlEncode(" + aparamA[0] + ")");

                    if (ii > 0 || paramTypedList.size() > 0) { methSig.append(", "); }
                    methSig.append("String " + aparamA[0]);
                }
            }

            methSig.append(')');

            fos.write((javaDoc + "\n    */\n").getBytes());
            fos.write((methodPrefix + methSig).getBytes());
            if ((methodPrefix.length() + methSig.length() + throwsClause.length()) > 100) {
                fos.write("\n           ".getBytes());
            }
            fos.write((" " + throwsClause + "\n").getBytes());
            fos.write(("        String restURL = " + requestURL_SB + ";\n").getBytes());
            fos.write(("        return null /* RepositoryConnection instance will be here */;\n    }\n\n").getBytes());
            System.out.println("stateA: " + stateA[0]);
        }
    }

    private void addToMap(Map<String,String> callPO, MemStatement msta) {
        MemURI pUri = msta.getPredicate();
        MemValue object = msta.getObject();
        String pathBase = "http://smartplatforms.org/";

        if (pUri.getNamespace().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#") && pUri.getLocalName().equals("type")) {
            // we know the type, ignore
        } else if (! pUri.getNamespace().equals("http://smartplatforms.org/api/")) {
            throw new RuntimeException("unexpected predicate namespace: " + pUri.stringValue());
        } else if (! expectedPredicates.contains(pUri.getLocalName())) {
            throw new RuntimeException("unexpected predicate localName: " + pUri.stringValue());
        } else if (pUri.getLocalName().equals("path")) {
            String path = object.stringValue();
            if (! path.startsWith(pathBase)) {
                throw new RuntimeException(
                        "expected path to start with ''" + pathBase + "'': " + path);
            }
            callPO.put("path", path.substring(pathBase.length()));
        } else {
            callPO.put(pUri.getLocalName(), object.stringValue());
        }
    }    
}