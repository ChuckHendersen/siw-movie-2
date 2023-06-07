package it.uniroma3.siw.controller.validator;

import java.time.Year;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import it.uniroma3.siw.model.Movie;

@Component
public class UpdateMovieValidator implements org.springframework.validation.Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return Movie.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Movie updatedMovie = (Movie) target;
		if(updatedMovie.getYear().isAfter(Year.now())) {
			errors.reject("movie.wrongYear");
		}
		if(updatedMovie.getTitle().equals("") || updatedMovie.getTitle().equals(" ")) {
			errors.reject("movie.name");
		}
	}

}
