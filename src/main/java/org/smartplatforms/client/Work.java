package org.smartplatforms.client;

import java.io.IOException;
import java.io.File;

import org.openrdf.model.Resource;
import org.openrdf.repository.Repository;
//import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.sail.Sail;
//import org.openrdf.sail.memory.model.MemBNode;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.TupleQueryResultHandler;
import org.openrdf.query.resultio.TupleQueryResultWriter;
//import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.query.resultio.sparqlxml.SPARQLResultsXMLWriter;
import org.openrdf.rio.turtle.TurtleWriter;
import org.openrdf.rio.RDFFormat;

/**
 * Hello world!
 *
 */
public class Work
{
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
                "CONSTRUCT { ?bs ?bp ?bo } WHERE " +
                    "{ ?bs <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://smartplatforms.org/api/call>. " +
                     " ?bs ?bp ?bo }" ;  //api:call rdfs:type

//        TupleQueryResultHandler tqrh = sparqlWriter;
        TupleQueryResultWriter tqrw = sparqlWriter;

        //GraphQueryResult gqr =
//                con.prepareTupleQuery(QueryLanguage.SPARQL, myfirstsparql).evaluate(tqrw);
                con.prepareGraphQuery(QueryLanguage.SPARQL, myfirstsparql).evaluate(turtleWriter);





    }
    
}