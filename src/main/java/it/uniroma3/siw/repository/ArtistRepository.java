package it.uniroma3.siw.repository;

import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;

public interface ArtistRepository extends CrudRepository<Artist, Long>{
	public boolean existsByNameAndSurname(String name, String surname);
	public Set<Artist> findAllByListaFilmRecitatiIsNotContaining(Movie movie);
}
