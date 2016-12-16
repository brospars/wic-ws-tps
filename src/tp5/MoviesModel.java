package tp5;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * A model that represents movies data
 */
public interface MoviesModel {

	public static final String MODEL_PREFIX = "http://mondomaine.org/test";
    public static final String STARRING = "starring" ;
    public static final String DIRECTED_BY = "directed_by" ;
    public static final String NAME = "name" ;
    public static final String MOVIE_TYPE = "Movie" ;
    public static final String ACTOR_TYPE = "Actor" ;
    public static final String DIRECTOR_TYPE = "Director" ;
	
    /**
     * Reads the movies data in a cvs file, creates the corresponding RDF
     * triples and adds them to the model.
     *
     * @param fileName name of the csv file containing the movies data.
     *
     * @throws FileNotFoundException if file does not exist.
     * @throws IOException if a problem occurs while reading the file.
     */    
    public void loadData(String fileName) throws FileNotFoundException, IOException;

    /**
     * Returns the list of all the names of all resources of a given type (Actor
     * or Director) involved in a movie.
     *
     * @param movieName the movie's name
     * @param resourceTypeName the type of the resource (Actor or Director)
     *
     * @return the list of all the names of all resources of type ResourceType
     * (Actor or Director) involved in the movie
     * @throws UnknownResourceException if the movie does not exist in the model
     */   
    public List<String> getMovieParticipantsBy(String movieName, String resourceTypeName);
    
    /**
     * Returns the list of the names of all the movies a resource of a given type
     * (Actor or Director) is involved in.
     *
     * @param resourceName resource's name
     * @param resourceTypeName the type of the resource (Actor or Director)
     * @return the list of the names of all the movies the resource is involved
     * in.
     * @throws UnknownResourceException if the resource does not exist in the
     * model
     */
    public List<String> getMoviesBy(String resourceName, String resourceTypeName);
    
    public List<String> getMoviesByActorAndDirector(String actorName, String directorName);
    
}
