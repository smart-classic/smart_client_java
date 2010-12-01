package org.smartplatforms.client;

import java.io.IOException;
import java.io.File;

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

import org.indivo.client.codegen.GenClientUtils;
/**
 * Hello world!
 *
 */
public class Work {
    private List<String> expectedPredicates = null;
    GenClientUtils genClientUtils = new GenClientUtils();

    private Work() {
        expectedPredicates = Arrays.asList("description", "method", "path", "target", "by_internal_id", "category");
    }

    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        Work instance = new Work();
        instance.dowork();
        System.out.println( "'later World!" );
    }

    private void dowork() {
        try {
            dowork0();
        } catch (org.openrdf.OpenRDFException repE) {
            throw new RuntimeException(repE);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private void dowork0() throws org.openrdf.OpenRDFException, IOException {
        TurtleWriter turtleWriter = new TurtleWriter(System.out);
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



/*
statement: org.openrdf.sail.memory.model.MemBNode    http://www.w3.org/1999/02/22-rdf-syntax-ns# : type   http://smartplatforms.org/api/call
statement: org.openrdf.sail.memory.model.MemBNode    http://smartplatforms.org/api/ : description   Get a single allergy
statement: org.openrdf.sail.memory.model.MemBNode    http://smartplatforms.org/api/ : method   GET
statement: org.openrdf.sail.memory.model.MemBNode    http://smartplatforms.org/api/ : path   http://smartplatforms.org/records/{record_id}/allergies/{allergy_id}
statement: org.openrdf.sail.memory.model.MemBNode    http://smartplatforms.org/api/ : target   http://smartplatforms.org/allergy
statement: org.openrdf.sail.memory.model.MemBNode    http://smartplatforms.org/api/ : by_internal_id   true
statement: org.openrdf.sail.memory.model.MemBNode    http://smartplatforms.org/api/ : category   record_item
*/
            }
            // now we have a map, callPO, of the significant predicate/object pairs
            //"description", "method", "path", "target", "by_internal_id", "category"
            String path0 = callPO.get("path");
            String path = path0;
            int qix = path0.indexOf('?');
            String query = "";
            if (qix > 0) {
                path = path0.substring(0, qix);
                query = path0.substring(qix +1);
            }
            String[] tokenListA = path.split("/");
            boolean endsWithSlash = path.charAt(path.length() -1) == '/';
            List<String> tokenList = Arrays.asList(tokenListA);
            StringBuffer javaDoc = new StringBuffer(callPO.get("description"));
            StringBuffer methSig = new StringBuffer();
            List<String> paramTypedList = new ArrayList();
            StringBuffer requestURL_SB = new StringBuffer();
            int[] stateA = new int[] { GenClientUtils.STATE_START };

            genClientUtils.tokensToSig(
                    tokenList,
                    methSig,
                    new ArrayList<String>(),
                    javaDoc,
                    requestURL_SB,
                    path,
                    stateA,
                    new boolean[] { true }
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

            methSig.append(callPO.get("method"));

            System.out.println("javaDoc: " + javaDoc);
            System.out.println("methSig: " + methSig);
            System.out.println("requestURL_SB: " + requestURL_SB);
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