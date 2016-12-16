import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * The purpose of this program is to demonstrate the use of Jena RDF's core API.
 * A set of movies extracted from the Freebase database is loaded in a RDF
 * model and is then queried.
 */
public class Movies {
  
    private static final Model model = ModelFactory.createDefaultModel();
    private static final Property NAME_PROP = model.createProperty("name") ;	
    private static final Property STARRING_PROP = model.createProperty("starring");
    private static final Property DIRECTED_BY_PROP = model.createProperty("directed_by");

    private static void loadData(String fileName) throws FileNotFoundException, IOException {
    	List<String> result = new ArrayList<String>();
        FileReader fr;
        
        model.setNsPrefix("", "http://freebase.com/movies");
        
		try {
			fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
	        for (String line = br.readLine(); line != null; line = br.readLine()) {
	        	String[] triplet = line.split(",");
	        	
	        	Resource subject = model.createResource(triplet[0]);
	        	Property property = model.createProperty(triplet[1]);	        	
	        	
	        	if(triplet[2].charAt(0) == '/'){
	        		Resource object = model.createResource(triplet[2]);
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
    }
    
    /**
     * looks up for a resource with a given name
     *
     * @param name name of the resource to look up for
     * @return the resource, null if there is no resource with this name
     */
    private static Resource findResourceFromName(String name) {
    	Resource result = null;
    	for(StmtIterator it = model.listStatements(null, NAME_PROP, name); it.hasNext();){
    		Statement stmt = it.nextStatement();
    		//System.out.println(stmt.getSubject().getLocalName());
    		result = stmt.getSubject();
    		break;
    	}
    	
    	return result;
    }

    /**
     * Return the list of all the resource for which a movie is subject of a
     * triple with a given property.
     *
     * @param prop the property to look for
     * @param movieName the movie name
     * @return list of all the resources for which the movie is subject of a
     * triple with property prop.
     */
    public static List<Resource> listParticipants(Property prop, String movieName) {
    	List<Resource> result = new ArrayList<Resource>();
    	RDFNode o = null; // nécessaire afin que la fonction listStatements fonctionne
    	Resource resource = findResourceFromName(movieName); // Nous récupérons la resource associé au nom du film
    	
    	if(resource == null)
    		return result;
    	
    	for(StmtIterator it = model.listStatements(resource, prop, o); it.hasNext();){
    		Statement stmt = it.nextStatement();
    		result.add(stmt.getObject().asResource());
    	}
    	
    	return result;
    }
    
    /**
     * Return the list of all the movies for which a resource is object of a
     * triple with a given property.
     *
     * @param prop the property to look for
     * @param resourceName resource's name
     * @return list of all the movies for which the resource is object of a
     * triple with property prop.
     */
    public static List<Resource> listMovies(Property prop, String resourceName) {
    	List<Resource> result = new ArrayList<Resource>();
    	RDFNode o = null; 
    	Resource resource = findResourceFromName(resourceName); // Nous récupérons la resource associé au nom du film
    	
    	if(resource == null)
    		return result;
    	
    	for(StmtIterator it = model.listStatements(null , prop, resource); it.hasNext();){
    		Statement stmt = it.nextStatement();
    		result.add(stmt.getSubject());
    	}
    	
    	return result;
    }
    
    /**
     * Returns the list of all the movies in which a given actor has acted that
     * were directed by a given director
     *
     * @param actorName actor's name
     * @param directorName director's name
     * @return the list of movies
     */
    public static List<Resource> listMovies(String actorName, String directorName) {
    	List<Resource> result = new ArrayList<Resource>();
    	RDFNode o = null; 
    	Resource actorResource = findResourceFromName(actorName);
    	
    	if(actorResource == null)
    		return result;
    	
    	List<Resource> directedBy = listMovies(DIRECTED_BY_PROP,directorName);
    	for(int i = 0 ; i< directedBy.size(); i++){
    		Resource resource = directedBy.get(i); // Nous récupérons la resource associé au nom du film
        	
        	for(StmtIterator it = model.listStatements(resource , STARRING_PROP , actorResource); it.hasNext();){
        		Statement stmt = it.nextStatement();
        		result.add(stmt.getSubject());
        	}
    	} 	
    	return result;
    }
    
    /**
     * Returns the name of a given resource
     *
     * @param r the resource
     * @return the resource name
     * @com.hp.hpl.jena.rdf.model.PropertyNotFoundException if the resource does
     * not have a name
     */
    public static String getResourceName(Resource r){
    	return r.getRequiredProperty(NAME_PROP).getObject().toString();
    }
    
    /**
     * displays on the console in alphabetical order the names of a list of
     * resources
     *
     * @param resources the list of resources
     */
    public static void displayInAlphabeticalOrder(List<Resource> resources) {
        List<String> names = new ArrayList<String>();
        for (Resource r : resources) {
            names.add(getResourceName(r));
        }
        Collections.sort(names);
        for (String name : names) {
        	System.out.println(name);
        }
    }
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
	loadData("/home/wicm2/rosparsb/ws/tp4/movies.csv");
    	Scanner scanIn = new Scanner(System.in);
    	String actorName, directorName, movieName, resourceName;
    	String line = null;
    	do {
    	    //
    	    System.out.println("\nChoose one of the following ");
            System.out.println("1: All actors starring in a movie");
            System.out.println("2: Director(s) of a movie");
            System.out.println("3: An actor's filmography");
            System.out.println("4: A director's filmography");
            System.out.println("5: An actor's filmography for a given director");
            System.out.println("6: Find resource by name");
            System.out.println("0: Quit");
            System.out.print("\nenter your choice : ");
            //
    	    line = scanIn.nextLine();
    	    int option = Integer.parseInt(line);
    	    switch(option) {
    	    case 0:
    	        System.out.println("\nBye\n");
    	        System.exit(0);
    	    case 1:
    	        System.out.print("enter the movie's name: ");
    	        movieName = scanIn.nextLine();
    	        System.out.println(movieName + " cast\n");
    	        displayInAlphabeticalOrder(listParticipants(STARRING_PROP,movieName));
    	        break;
    	    case 2:
    	    	System.out.print("enter the movie's name: ");
    	        movieName = scanIn.nextLine();
    	        System.out.println(movieName + " was directed by:\n");
    	        displayInAlphabeticalOrder(listParticipants(DIRECTED_BY_PROP,movieName));
    	        break;
    	    case 3:
    	    	System.out.print("enter the actor's name: ");
    	        actorName = scanIn.nextLine();
    	        System.out.println("\nMovies where " + actorName + "is acting:\n");
    	        displayInAlphabeticalOrder(listMovies(STARRING_PROP,actorName));
    	        break;
    	    case 4:
    	    	System.out.print("enter the director's name: ");
    	    	directorName = scanIn.nextLine();
    	        System.out.println("\nMovies directed by " + directorName + ":\n");
    	        displayInAlphabeticalOrder(listMovies(DIRECTED_BY_PROP,directorName));
    	        break;
    	    case 5:
    	    	System.out.print("enter the actor's name: ");
    	    	actorName = scanIn.nextLine();
    	    	System.out.print("enter the director's name: ");
    	    	directorName = scanIn.nextLine();
    	        System.out.println("\nMovies where " + actorName + " is acting and that are directed by " + directorName + ":\n");
    	        displayInAlphabeticalOrder(listMovies(actorName,directorName));
    	        break;
    	    case 6:
    	    	System.out.print("enter the resource name : ");
    	    	resourceName = scanIn.nextLine();
    	        System.out.println(findResourceFromName(resourceName));
    	        break;
    	    default:
    	    	System.out.println("uncorrect choice,try it again!");
    	    	break;
    	    }
        } while (line != null); 
    }	
}