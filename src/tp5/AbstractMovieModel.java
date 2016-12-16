package tp5;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.vocabulary.RDF;

public abstract class AbstractMovieModel implements MoviesModel {
    
	protected Model model = ModelFactory.createDefaultModel();
	protected final Property nameProp = model.createProperty(MODEL_PREFIX + NAME);
	protected final Property starringProp = model.createProperty(MODEL_PREFIX + STARRING);
	protected final Property directedByProp = model.createProperty(MODEL_PREFIX + DIRECTED_BY);
	protected final Resource movieType = model.createResource(MODEL_PREFIX + MOVIE_TYPE);
	protected final Resource actorType = model.createResource(MODEL_PREFIX + ACTOR_TYPE);
	protected final Resource directorType = model.createResource(MODEL_PREFIX + DIRECTOR_TYPE);
	
	/**
     * Reads the movies data in a cvs file, creates the corresponding RDF
     * triples and adds them to the model.
     *
     * @param fileName name of the csv file containing the movies data.
     *
     * @throws FileNotFoundException if file does not exist.
     * @throws IOException if a problem occurs while reading the file.
     */
	@Override
	public void loadData(String fileName) throws FileNotFoundException, IOException {
		BufferedReader bfr = new BufferedReader(new FileReader(fileName));
        try {
            String line = bfr.readLine();
        	while (line != null) {
            	String[] tripleElements = line.split(",");
        	    Resource subject = model.createResource(MODEL_PREFIX + tripleElements[0]);
        	    Property predicate = model.createProperty(MODEL_PREFIX + tripleElements[1]);
        	    RDFNode object;
        	    if (tripleElements[2].startsWith("/")) {
        	    	object = model.createResource(MODEL_PREFIX + tripleElements[2]);
        	    } else {
        		    object = model.createLiteral(tripleElements[2]);
        		}
        	    model.add(model.createStatement(subject,predicate,object));
        	    line = bfr.readLine();
            }
        	addTypes();
        }
        finally {
            if (bfr != null) bfr.close();
        }
	}
	
	private void addTypes() {
		ResIterator movies = model.listResourcesWithProperty(starringProp);
		while(movies.hasNext()) {
			Resource r = movies.next();
			model.add(model.createStatement(r, RDF.type, movieType));
		}
		NodeIterator actors = model.listObjectsOfProperty(starringProp);
		while(actors.hasNext()) {
			Resource r = (Resource) actors.next();
			model.add(model.createStatement(r, RDF.type, actorType));
		}
		NodeIterator directors = model.listObjectsOfProperty(directedByProp);
		while(directors.hasNext()) {
			Resource r = (Resource) directors.next();
			model.add(model.createStatement(r, RDF.type, directorType));
		}
	}
	
	/**
     * Returns the name of a given resource
     *
     * @param r the resource
     * @return the resource name
     * @com.hp.hpl.jena.rdf.model.PropertyNotFoundException if the resource does
     * not have a name
     */
	public String getResourceName(Resource r){
    	return r.getRequiredProperty(nameProp).getObject().toString();
    }
	
	/**
     * Returns a list (in alphabetical order) of the names of the resources
     * stored in a list.
     *
     * @param resources the list of resources
     * @return the alphabetically sorted list of resources names
     */
	protected List<String> getNamesInAlphabeticOrder(List<Resource> resources) {
		List<String> names = new ArrayList<String>();
        for (Resource r : resources) {
            names.add(getResourceName(r));
        }
        Collections.sort(names);
        return names;
	}
	
	protected Property getPropForType(String resourceType) {
		if(resourceType.equals(ACTOR_TYPE)) {
		    return starringProp;
		} else if (resourceType.equals(DIRECTOR_TYPE))  {
			return directedByProp;	
		} else {
			throw new IllegalArgumentException(resourceType + " is an invalid type");
		}
	}
}
