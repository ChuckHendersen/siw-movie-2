package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;

public interface ReviewRepository extends CrudRepository<Review,Long> {
	//fare repositiories
	public boolean existsByReviewedMovieAndAuthor(Movie reviewedMovie, User author);
	
	//public List<Review> findByReviewedMovie(Movie reviewedMovie);
}
