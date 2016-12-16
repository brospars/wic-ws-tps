package tp5;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class MoviesApplications {

	/**
     * displays on the console in alphabetical order the names of a list of
     * resources
     *
     * @param resources the list of resources
     */
    public static void displayNames(List<String> names) {
        int i = 1;
        for (String name : names) {
            System.out.println("[" + i + "]\t" + name);
            i++;
        }
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
	//MoviesModel moviesModel = new MoviesModelCoreRDF(); 
	MoviesModel moviesModel = new MoviesModelSPARQL();
        moviesModel.loadData("/home/wicm2/rosparsb/ws/tp4/movies.csv");
        Scanner scanIn = new Scanner(System.in);
        String actorName, directorName, movieName;
        String line = null;
        do {
            System.out.println("\nChoose one of the following ");
            System.out.println("1: All actors starring in a movie");
            System.out.println("2: Director(s) of a movie");
            System.out.println("3: An actor's filmography");
            System.out.println("4: A director's filmography");
            System.out.println("5: An actor's filmography for a given director");
            System.out.println("0: Quit");
            System.out.print("\nenter your choice : ");
    	    line = scanIn.nextLine();
    	    int option = Integer.parseInt(line);
    	    switch(option) {
                    case 0:
                        System.out.println("\nBye");
                        System.exit(0);
                    case 1:
                        System.out.print("enter the movie's name : ");
                        movieName = scanIn.nextLine();
                        System.out.println(movieName + " cast:\n");
                        displayNames(moviesModel.getMovieParticipantsBy(movieName,MoviesModel.ACTOR_TYPE));
                        break;
                    case 2:
                        System.out.print("enter the movie's name : ");
                        movieName = scanIn.nextLine();
                        System.out.println(movieName + " was directed by:\n");
                        displayNames(moviesModel.getMovieParticipantsBy(movieName,MoviesModel.DIRECTOR_TYPE));
                        break;
                    case 3:
                        System.out.print("enter the actor's name: ");
                        actorName = scanIn.nextLine();
                        System.out.println("\nMovies where " + actorName + " is acting:\n");
                        displayNames(moviesModel.getMoviesBy(actorName,MoviesModel.ACTOR_TYPE));
                        break;
                    case 4:
                        System.out.print("enter the director's name: ");
                        directorName = scanIn.nextLine();
                        System.out.println("\nMovies directed by " + directorName + ":\n");
                        displayNames(moviesModel.getMoviesBy(directorName,MoviesModel.DIRECTOR_TYPE));
                        break;
                    case 5:
                        System.out.print("enter the actor's name: ");
                        actorName = scanIn.nextLine();
                        System.out.print("enter the director's name: ");
                        directorName = scanIn.nextLine();
                        System.out.println("\nMovies where " + actorName
                                + " is acting and that are directed by " + directorName + ":\n");
                        displayNames(moviesModel.getMoviesByActorAndDirector(actorName, directorName));
                        break;
                    default:
                        System.out.println("uncorrect choice, try again !");
                        break;
            }
        } while (line != null);
    }
}
