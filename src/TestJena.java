import java.io.*;
import java.util.*;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;

/**
 * Tests to experiment with the Jena Core RDF API.
 */
public class TestJena {

    private static String MODEL_PREFIX = "http://mondomaine.org/test#";

    public static void displayStatements(StmtIterator stmtIterator) {
        for (; stmtIterator.hasNext();) {
            Statement st = stmtIterator.nextStatement();
            System.out.println(st);
        }
    }
    
    public static void main(String[] args) {
    	final String filename = "/home/wicm2/rosparsb/ws/tp4/movies.csv";
        Model model = csvToRdf(filename);
        
        findAllActorsInMovie("Blade Runner",model);
        
    }
    
    public void testJena(){
    //  create a new empty model
        Model model = ModelFactory.createDefaultModel();

        model.setNsPrefix("", "http://mondomaine.org/test#");

        // create some resources, properties and literals
        Resource r1 = model.createResource(MODEL_PREFIX + "r1");
        Resource r2 = model.createResource(MODEL_PREFIX + "r2");
        Resource r3 = model.createResource(MODEL_PREFIX + "r3");
        Property p1 = model.createProperty(MODEL_PREFIX + "p1");
        Property p2 = model.createProperty(MODEL_PREFIX + "p2");
        Property p3 = model.createProperty(MODEL_PREFIX + "p3");
        Literal l1 = model.createLiteral("literal1");

        // create a statement
        Statement stmt = model.createStatement(r1, p1, l1);
        // the statement must be explicitly added to the model
        model.add(stmt);

        // create new statement with resource r1 as subject
        // the statement is implicitly added to the model
        r1.addProperty(p2, r2);
        
        // create new statements with resource r2 as subject
        r2.addProperty(p3, MODEL_PREFIX + "r3");
        r2.addProperty(p2,r3).addLiteral(p3, "r1");
        // create new statement with resource r3 as subject
        r3.addLiteral(p2, l1);

        // display all the statements on the console
        System.out.println("All the statements");
        displayStatements(model.listStatements());

        System.out.println("\n----------------------------\nLe modèle en RDF/XML\n");
        model.write(System.out, "RDF/XML", "http://mondomaine.org/test#");
    }
    
    public static Model csvToRdf(String filename) {

    	List<String> result = new ArrayList<String>();
        FileReader fr;
        
        Model model = ModelFactory.createDefaultModel();
        model.setNsPrefix("", "http://freebase.com/movies");
        
		try {
			fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
	        for (String line = br.readLine(); line != null; line = br.readLine()) {
	        	String[] triplet = line.split(",");
	        	
	        	Resource subject = model.createResource(MODEL_PREFIX + triplet[0]);
	        	Property property = model.createProperty(MODEL_PREFIX + triplet[1]);
	        	
	        	
	        	if(triplet[2].charAt(0) == '/'){
	        		Resource object = model.createResource(MODEL_PREFIX + triplet[2]);
	        		Statement stmt = model.createStatement(subject, property, object);
	        		model.add(stmt);
	        	}else{
	        		Literal object = model.createLiteral(triplet[2]);
	        		Statement stmt = model.createStatement(subject, property, object);
	        		model.add(stmt);
	        	}
	        	
	        	//Print as RDF XML : model.write(System.out, "RDF/XML", "http://mondomaine.org/test#");
	        }
	        br.close();
	        fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return model;
        
    }
    
    public static List<String> findAllActorsInMovie(String movieName, Model model){
    	List<String> result = null;
    	
    	Property prop = model.getProperty("starring");
    	
//    	for(StmtIterator it = model.listStatements(Resource s, Property p, RDF); it.hasNext();){
//    		Statement stmt = it.nextStatement();
//    		System.out.println(stmt.getSubject().getLocalName());
//    	}
    	
    	String queryString = "SELECT DISTINCT ?actor WHERE { ?movieUri "+prop+" ?actor. }";
    	
		Query query = QueryFactory.create(queryString);
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				RDFNode x = soln.get("varName");
				Resource r = soln.getResource("VarR"); 
				Literal l = soln.getLiteral("VarL");
				
				System.out.println(r);
			}
		}
    	  
    	return result;
    	
    }
}
