package it.uniroma3.siw.controller.validator;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.uniroma3.siw.controller.form.UpdateMovieForm;

@Component
public class UpdateMovieValidator{

	@Autowired
	private MovieService movieService;

	public void validate(UpdateMovieForm updateMovieForm, Movie movie, Errors errors) {
		if(!updateMovieForm.getTitle().equals(movie.getTitle()) && updateMovieForm.getYear().equals(movie.getYear())) {
			if(this.movieService.alreadyExists(updateMovieForm.getTitle(), updateMovieForm.getYear())) {
				errors.reject("movie.duplicate");
			}
		}
	}

}
