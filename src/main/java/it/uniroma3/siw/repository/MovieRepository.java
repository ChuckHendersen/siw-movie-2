package it.uniroma3.siw.repository;

import java.time.Year;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Movie;

public interface MovieRepository extends CrudRepository<Movie,Long>{

	public List<Movie> findByYear(Year year);
	
	public List<Movie> findAllByOrderByTitle();//ordina per titolo
	
	public List<Movie> findAllByOrderByYear();
	
	public boolean existsByTitleAndYear (String title, Year year);
	
	//public Integer countByTitleAndYear (String title, Year year);
}
