package tp5;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.ArrayList;
import java.util.List;

public class MoviesModelSPARQL extends AbstractMovieModel {
	
    /**
     * Execute a SPARQL request of the form :
     *   SELECT DISTINCT ?name WHERE { .....} ORDER BY ?name
     * The values found for the ?name variable are stored in a list of String
     * which is returned by this method
     * @param queryString the SELECT query
     * @return the list of names in alphabetical order
     */
    private List<String> executeSelectNameQuery(String queryString) {
    	
    	List<String> culotte = new ArrayList<String>();
    	
		Query query = QueryFactory.create(queryString);
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();
			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				RDFNode x = soln.get("varName");
				Resource r = soln.getResource("Subject"); 
				Literal l = soln.getLiteral("varL");
				
				culotte.add(r.toString());
				
			}
		}
		
		return culotte;
    }
	
    @Override
    public List<String> getMovieParticipantsBy(String movieName, String resourceTypeName) {
    	
    	String queryMovieRessource = "PREFIX pre: <" + MoviesModel.MODEL_PREFIX + "> " +
    								 "SELECT ?Subject "+
    								 	"WHERE { ?Subject pre:name \""+movieName+"\".} ";
    	
    	String movieResource = executeSelectNameQuery(queryMovieRessource).get(0);
    	
    	String queryMovieParticipants =	"PREFIX pre: <" + MoviesModel.MODEL_PREFIX + "> " +
			 							"SELECT ?Subject "+
			 								"WHERE { ?movie pre:starring ?Subject . "+
			 									"FILTER (?movie = <" + movieResource + ">) }";
    	
    	
    	
    	return executeSelectNameQuery(queryMovieParticipants);
    }
	
    @Override
    public List<String> getMoviesByActorAndDirector(String actorName, String directorName) {
    	return executeSelectNameQuery("");
    }
	
    @Override
    public List<String> getMoviesBy(String resourceName, String resourceTypeName) {
    	return executeSelectNameQuery("");
    }
	
}
