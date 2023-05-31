package it.uniroma3.siw.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;

public interface ArtistRepository extends CrudRepository<Artist, Long>{
	
	/**
	 * Checks if a artist already exists in the DB based on the name and surname
	 * @param name
	 * @param surname
	 * @return
	 */
	public boolean existsByNameAndSurname(String name, String surname);
	
	/**
	 * Provides a set of artist who have not acted in the specified movie
	 * @param movie a movie
	 * @return Set of artist
	 */
	public Set<Artist> findAllByListaFilmRecitatiIsNotContaining(Movie movie);
	
	/**
	 * Provides a set of artist who have acted in a movie ordered by their surnames
	 * @param movie movie in which the artist have acted
	 * @return Set of artist ordered by surname
	 */
	public Set<Artist> findAllByListFilmRecitatiContainingMovieOrderBySurname(Movie movie);
}
