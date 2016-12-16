package tp5;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * A implementation of MoviesModel based on Jena Core RDF API.
 */
public class MoviesModelCoreRDF extends AbstractMovieModel {
	
	/**
     * looks up for a resource with a given name
     *
     * @param name name of the resource to look up for
     * @return the resource, null if there is no resource with this name
     */
	private Resource findResourceFromName(String name) {
        StmtIterator stmtIt = model.listStatements(null,nameProp,model.createLiteral(name));
        if (stmtIt.hasNext()) {
            Statement stmt = stmtIt.nextStatement();
            return (stmt.getSubject());
        }
        return null;
    }
	
	@Override
	public List<String> getMovieParticipantsBy(String movieName, String resourceTypeName) {
    	Resource movieResource = findResourceFromName(movieName);
    	if (movieResource != null) {
    		List<Resource> resources = new ArrayList<Resource>();
    		StmtIterator stmtIt = model.listStatements(movieResource,getPropForType(resourceTypeName),(RDFNode) null);
    		while(stmtIt.hasNext()) {
    			Statement stmt = stmtIt.nextStatement();
    			resources.add((Resource) stmt.getObject());
    		}
    		return getNamesInAlphabeticOrder(resources);
    	} else { // there is no resource named resourceName
    	    throw new UnknownResourceException("Movie", movieName);	
        }
	}

	/**
     * Return the list of all the movies resources a resource of a
     * given type (Actor or Director) is involved in.
     * 
     * @param resourceName resource's name
     * @param resourceTypeName the type of the resource (Actor or Director)
     * 
     * @return the list of all the movies resources the resource is involved in.
     * @throws UnknownResourceException if the resource does not exist in the
     * model
     */
    private List<Resource> getMoviesResourcesBy(String resourceName, String resourceTypeName) {

        Resource resource = findResourceFromName(resourceName);
        if (resource != null) {
            List<Resource> movies = new ArrayList<Resource>();
            StmtIterator stmtIt = model.listStatements(null, getPropForType(resourceTypeName), resource);
            for (; stmtIt.hasNext();) {
                Statement st2 = stmtIt.nextStatement();
                movies.add((Resource) st2.getSubject());
            }
            return movies;
        } else {  // there is no resource named resourceName
            throw new UnknownResourceException(resourceTypeName, resourceName);
        }
    }

    public List<String> getMoviesByActorAndDirector(String actorName, String directorName) {
        Resource directorResource = findResourceFromName(directorName);
        // find all the movies the actor is acting in
        List<Resource> allMovies = getMoviesResourcesBy(actorName, ACTOR_TYPE);
        // filter them according the director
        List<Resource> res = new ArrayList<Resource>();
        for (Resource movie : allMovies) {
            if (movie.hasProperty(directedByProp, directorResource)) {
                res.add(movie);
            }
        }
        return getNamesInAlphabeticOrder(res);
    }
    
    @Override
    public List<String> getMoviesBy(String resourceName, String resourceTypeName) {
        return getNamesInAlphabeticOrder(getMoviesResourcesBy(resourceName, resourceTypeName));
    }
    
}
